package de.eydamos.backpack.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Caches backpack data and queues dirty files on Minecraft's file IO thread during world saves. */
public class SaveFileHandler {

    /** Checked in order when the primary file is missing or unreadable. */
    private static final String[] FALLBACK_SUFFIXES = { ".dat_old", ".dat_new" };

    protected final Logger logger = LogManager.getLogger();

    protected File worldDir = null;
    protected File backpackDir = null;
    protected File playerDir = null;

    private final Map<File, CachedFile> cachedFiles = new HashMap<>();
    private long nextVersion;

    public synchronized void init() {
        cachedFiles.clear();
        backpackDir = null;
        playerDir = null;

        worldDir = DimensionManager.getCurrentSaveRootDirectory();
        if (worldDir == null) return;

        backpackDir = new File(worldDir, "backpacks/backpacks");
        if (!backpackDir.isDirectory() && !backpackDir.mkdirs()) {
            logger.warn("[Backpack] Couldn't create backpack save directory: {}", backpackDir);
        }
        playerDir = new File(worldDir, "backpacks/player");
        if (!playerDir.isDirectory() && !playerDir.mkdirs()) {
            logger.warn("[Backpack] Couldn't create player save directory: {}", playerDir);
        }
    }

    public synchronized NBTTagCompound loadBackpack(String UUID) {
        return load(backpackDir, UUID);
    }

    public synchronized void saveBackpack(NBTTagCompound data, String UUID) {
        save(data, backpackDir, UUID);
    }

    public synchronized void deleteBackpack(String UUID) {
        delete(backpackDir, UUID);
    }

    public synchronized NBTTagCompound loadPlayer(String UUID) {
        return load(playerDir, UUID);
    }

    public synchronized void savePlayer(NBTTagCompound data, String UUID) {
        save(data, playerDir, UUID);
    }

    public synchronized boolean backpackSaveExists(String UUID) {
        return saveExists(backpackDir, UUID);
    }

    public synchronized boolean playerSaveExists(String UUID) {
        return saveExists(playerDir, UUID);
    }

    private boolean saveExists(File directory, String fileName) {
        if (directory == null || !isValidUUID(fileName)) return false;

        CachedFile cachedFile = cachedFiles.get(new File(directory, fileName + ".dat"));
        return cachedFile != null ? cachedFile.exists : readExists(directory, fileName);
    }

    public synchronized NBTTagCompound load(File directory, String fileName) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        if (directory == null || !isValidUUID(fileName)) return nbtTagCompound;

        File file = new File(directory, fileName + ".dat");
        CachedFile cachedFile = cachedFiles.get(file);
        if (cachedFile != null) return (NBTTagCompound) cachedFile.data.copy();

        NBTTagCompound loaded = read(file);
        boolean loadedFallback = false;
        for (int i = 0; loaded == null && i < FALLBACK_SUFFIXES.length; i++) {
            File fallback = new File(directory, fileName + FALLBACK_SUFFIXES[i]);
            loaded = read(fallback);
            if (loaded != null) {
                loadedFallback = true;
                logger.info("[Backpack] Loaded fallback data from {}.", fallback);
            }
        }

        if (loaded == null) {
            cachedFiles.put(file, CachedFile.missing());
            return nbtTagCompound;
        }

        CachedFile loadedFile = new CachedFile((NBTTagCompound) loaded.copy());
        if (loadedFallback) loadedFile.version = ++nextVersion;
        cachedFiles.put(file, loadedFile);
        return loaded;
    }

    public synchronized void save(NBTTagCompound data, File directory, String fileName) {
        if (data == null || directory == null || !isValidUUID(fileName)) return;

        File file = new File(directory, fileName + ".dat");
        CachedFile cachedFile = cachedFiles.get(file);
        if (cachedFile == null) {
            cachedFile = new CachedFile((NBTTagCompound) data.copy());
            cachedFiles.put(file, cachedFile);
        } else {
            cachedFile.data = (NBTTagCompound) data.copy();
            cachedFile.exists = true;
        }
        cachedFile.version = ++nextVersion;
    }

    private boolean readExists(File directory, String fileName) {
        if (new File(directory, fileName + ".dat").isFile()) return true;

        for (String suffix : FALLBACK_SUFFIXES) {
            if (new File(directory, fileName + suffix).isFile()) return true;
        }

        return false;
    }

    public synchronized void queueDirtyFiles() {
        List<PendingSave> pendingSaves = new ArrayList<>();
        for (Map.Entry<File, CachedFile> entry : cachedFiles.entrySet()) {
            CachedFile cachedFile = entry.getValue();
            if (cachedFile.version <= cachedFile.queuedVersion) continue;

            cachedFile.queuedVersion = cachedFile.version;
            pendingSaves
                    .add(new PendingSave(entry.getKey(), (NBTTagCompound) cachedFile.data.copy(), cachedFile.version));
        }
        if (!pendingSaves.isEmpty()) {
            queueIO(new SaveBatch(pendingSaves));
        }
    }

    // Package-private for deterministic tests.
    void queueIO(IThreadedFileIO batch) {
        ThreadedFileIOBase.threadedIOInstance.queueIO(batch);
    }

    private NBTTagCompound read(File file) {
        if (!file.isFile()) return null;

        try (FileInputStream stream = new FileInputStream(file)) {
            return CompressedStreamTools.readCompressed(stream);
        } catch (IOException ioException) {
            logger.warn("[Backpack] Couldn't load data from {}.", file, ioException);
            return null;
        }
    }

    private boolean write(NBTTagCompound data, File file) {
        File fileNew = new File(file.getParentFile(), file.getName() + "_new");
        File fileOld = new File(file.getParentFile(), file.getName() + "_old");

        try {
            try (FileOutputStream stream = new FileOutputStream(fileNew)) {
                CompressedStreamTools.writeCompressed(data, stream);
            }

            if (!deleteIfExists(fileOld)) return false;

            if (!renameIfExists(file, fileOld)) return false;

            if (!deleteIfExists(file)) return false;

            if (!renameIfExists(fileNew, file)) {
                renameIfExists(fileOld, file);
                return false;
            }

            if (fileNew.exists() && !fileNew.delete()) {
                logger.warn("[Backpack] Couldn't delete temporary save file: {}", fileNew);
            }

            return true;
        } catch (IOException ioException) {
            logger.warn("[Backpack] Couldn't save data to {}.", file, ioException);
            return false;
        }
    }

    // No current callers, add per-file serialization if deletion is used with queued writes.
    public synchronized void delete(File directory, String fileName) {
        if (directory == null || !isValidUUID(fileName)) return;

        File fileNew = new File(directory, fileName + ".dat_new");
        File fileOld = new File(directory, fileName + ".dat_old");
        File file = new File(directory, fileName + ".dat");

        deleteIfExists(fileOld);
        deleteIfExists(file);
        deleteIfExists(fileNew);

        cachedFiles.remove(file);
    }

    private boolean deleteIfExists(File file) {
        if (!file.exists()) return true;
        if (file.delete()) return true;
        logger.warn("[Backpack] Couldn't delete save file: {}", file);
        return false;
    }

    private boolean renameIfExists(File from, File to) {
        if (!from.exists()) return true;
        if (from.renameTo(to)) return true;
        logger.warn("[Backpack] Couldn't rename save file from {} to {}.", from, to);
        return false;
    }

    private boolean isValidUUID(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    private synchronized boolean isCurrent(PendingSave pendingSave) {
        CachedFile cachedFile = cachedFiles.get(pendingSave.file);
        return cachedFile != null && cachedFile.queuedVersion == pendingSave.version;
    }

    private synchronized void retryOnNextSave(PendingSave pendingSave) {
        CachedFile cachedFile = cachedFiles.get(pendingSave.file);
        if (cachedFile == null || cachedFile.queuedVersion != pendingSave.version) return;
        cachedFile.queuedVersion = 0;
    }

    private static class CachedFile {

        private NBTTagCompound data;
        /** False for a cached missing file. */
        private boolean exists = true;
        private long version;
        private long queuedVersion;

        private CachedFile(NBTTagCompound data) {
            this.data = data;
        }

        private static CachedFile missing() {
            CachedFile cachedFile = new CachedFile(new NBTTagCompound());
            cachedFile.exists = false;
            return cachedFile;
        }
    }

    private static class PendingSave {

        private final File file;
        private final NBTTagCompound data;
        private final long version;

        private PendingSave(File file, NBTTagCompound data, long version) {
            this.file = file;
            this.data = data;
            this.version = version;
        }
    }

    private class SaveBatch implements IThreadedFileIO {

        private final List<PendingSave> pendingSaves;
        private int nextSave;

        private SaveBatch(List<PendingSave> pendingSaves) {
            this.pendingSaves = pendingSaves;
        }

        @Override
        public boolean writeNextIO() {
            PendingSave pendingSave = pendingSaves.get(nextSave++);
            try {
                if (isCurrent(pendingSave) && !write(pendingSave.data, pendingSave.file)) {
                    retryOnNextSave(pendingSave);
                }
            } catch (RuntimeException | StackOverflowError failure) {
                // Do not let malformed backpack data kill Minecraft's shared file IO thread.
                logger.error("[Backpack] Unexpected error while saving backpack data.", failure);
            }
            return nextSave < pendingSaves.size();
        }
    }
}
