package de.eydamos.backpack.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFileHandler {

    protected final Logger logger = LogManager.getLogger();

    protected File worldDir = null;
    protected File backpackDir = null;
    protected File playerDir = null;

    private final HashMap<File, NBTTagCompound> cachedFiles = new HashMap<>();

    public void init() {
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
        if (!backpackSaveExists(UUID)) {
            return new NBTTagCompound();
        }

        return load(backpackDir, UUID);
    }

    public void saveBackpack(NBTTagCompound data, String UUID) {
        save(data, backpackDir, UUID);
    }

    public void deleteBackpack(String UUID) {
        delete(backpackDir, UUID);
    }

    public NBTTagCompound loadPlayer(String UUID) {
        if (!playerSaveExists(UUID)) {
            return new NBTTagCompound();
        }

        return load(playerDir, UUID);
    }

    public void savePlayer(NBTTagCompound data, String UUID) {
        save(data, playerDir, UUID);
    }

    public boolean backpackSaveExists(String UUID) {
        if (!isValidUUID(UUID)) return false;

        File f = new File(backpackDir, UUID + ".dat");
        if (cachedFiles.containsKey(f)) return true;
        return f.exists();
    }

    public boolean playerSaveExists(String UUID) {
        if (!isValidUUID(UUID)) return false;

        File f = new File(playerDir, UUID + ".dat");
        if (cachedFiles.containsKey(f)) return true;
        return f.exists();
    }

    public NBTTagCompound load(File directory, String fileName) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        if (!isValidUUID(fileName)) return nbtTagCompound;

        File file = new File(directory, fileName + ".dat");

        if (cachedFiles.containsKey(file)) return (NBTTagCompound) cachedFiles.get(file).copy();

        if (file.exists()) {
            try {
                nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
                cachedFiles.put(file, (NBTTagCompound) nbtTagCompound.copy());
                return nbtTagCompound;
            } catch (IOException ioException) {
                logger.warn("[Backpack] Couldn't load data from {}.", file, ioException);
            }
        }

        logger.info("[Backpack] Couldn't load data. Using fallback file.");

        file = new File(directory, fileName + ".dat_old");

        if (file.exists()) {
            try {
                nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
                cachedFiles.put(file, (NBTTagCompound) nbtTagCompound.copy());
            } catch (IOException ioException) {
                logger.warn("[Backpack] Couldn't load fallback data from {}.", file, ioException);
            }
        }

        return nbtTagCompound;
    }

    public void save(NBTTagCompound data, File directory, String fileName) {
        if (!isValidUUID(fileName)) return;

        File fileNew = new File(directory, fileName + ".dat_new");
        File fileOld = new File(directory, fileName + ".dat_old");
        File file = new File(directory, fileName + ".dat");

        try {
            CompressedStreamTools.writeCompressed(data, new FileOutputStream(fileNew));

            if (!deleteIfExists(fileOld)) return;

            if (!renameIfExists(file, fileOld)) return;

            if (!deleteIfExists(file)) return;

            if (!renameIfExists(fileNew, file)) {
                renameIfExists(fileOld, file);
                return;
            }

            if (fileNew.exists() && !fileNew.delete()) {
                logger.warn("[Backpack] Couldn't delete temporary save file: {}", fileNew);
            }

            cachedFiles.put(file, (NBTTagCompound) data.copy());
        } catch (IOException fileNotFoundException) {
            logger.warn("[Backpack] Couldn't save data to {}.", file, fileNotFoundException);
        }
    }

    public void delete(File directory, String fileName) {
        if (!isValidUUID(fileName)) return;

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
}
