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

public class SaveFileHandler {

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

    public NBTTagCompound loadBackpack(String UUID) {
        return load(backpackDir, UUID);
    }

    public void saveBackpack(NBTTagCompound data, String UUID) {
        save(data, backpackDir, UUID);
    }

    public void deleteBackpack(String UUID) {
        delete(backpackDir, UUID);
    }

    public NBTTagCompound loadPlayer(String UUID) {
        return load(playerDir, UUID);
    }

    public void savePlayer(NBTTagCompound data, String UUID) {
        save(data, playerDir, UUID);
    }

    public synchronized boolean backpackSaveExists(String UUID) {
        if (!isValidUUID(UUID)) return false;

        File f = new File(backpackDir, UUID + ".dat");
        if (cachedFiles.containsKey(f)) return true;
        return f.exists() || new File(backpackDir, UUID + ".dat_old").exists();
    }

    public synchronized boolean playerSaveExists(String UUID) {
        if (!isValidUUID(UUID)) return false;

        File f = new File(playerDir, UUID + ".dat");
        if (cachedFiles.containsKey(f)) return true;
        return f.exists() || new File(playerDir, UUID + ".dat_old").exists();
    }

    public synchronized NBTTagCompound load(File directory, String fileName) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        if (directory == null || !isValidUUID(fileName)) return nbtTagCompound;

        File file = new File(directory, fileName + ".dat");
        CachedFile cachedFile = cachedFiles.get(file);
        if (cachedFile != null) return (NBTTagCompound) cachedFile.data.copy();

        NBTTagCompound loaded = read(file);
        boolean loadedFallback = false;
        if (loaded == null) {
            File fallback = new File(directory, fileName + ".dat_old");
            loaded = read(fallback);
            if (loaded != null) {
                loadedFallback = true;
                logger.info("[Backpack] Loaded fallback data from {}.", fallback);
            }
        }

        if (loaded == null) return nbtTagCompound;

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
        }
        cachedFile.version = ++nextVersion;
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
            ThreadedFileIOBase.threadedIOInstance.queueIO(new SaveBatch(pendingSaves));
        }
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
        private long version;
        private long queuedVersion;

        private CachedFile(NBTTagCompound data) {
            this.data = data;
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
            if (isCurrent(pendingSave)) {
                if (!write(pendingSave.data, pendingSave.file)) retryOnNextSave(pendingSave);
            }
            return nextSave < pendingSaves.size();
        }
    }
}
