package de.eydamos.backpack.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFileHandler {
    protected Logger logger = LogManager.getLogger();

    protected File worldDir = null;
    protected File backpackDir = null;
    protected File playerDir = null;

    public void init() {
        worldDir = DimensionManager.getCurrentSaveRootDirectory();
        backpackDir = new File(worldDir, "backpacks/backpacks");
        if (!backpackDir.exists()) {
            backpackDir.mkdirs();
        }
        playerDir = new File(worldDir, "backpacks/player");
        if (!playerDir.exists()) {
            playerDir.mkdirs();
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
        return new File(backpackDir, UUID + ".dat").exists();
    }

    public boolean playerSaveExists(String UUID) {
        return new File(playerDir, UUID + ".dat").exists();
    }

    public NBTTagCompound load(File directory, String fileName) {
        NBTTagCompound nbtTagCompound = new NBTTagCompound();

        File file = new File(directory, fileName + ".dat");

        if (file.exists()) {
            try {
                nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
                return nbtTagCompound;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        logger.info("[Backpack] Couldn't load data. Using fallback file.");

        file = new File(directory, fileName + ".dat_old");

        if (file.exists()) {
            try {
                nbtTagCompound = CompressedStreamTools.readCompressed(new FileInputStream(file));
            } catch (IOException ioException) {
                ioException.printStackTrace();
                logger.warn("[Backpack] Couldn't load data at all.");
            }
        }

        return nbtTagCompound;
    }

    public void save(NBTTagCompound data, File directory, String fileName) {
        File fileNew = new File(directory, fileName + ".dat_new");
        File fileOld = new File(directory, fileName + ".dat_old");
        File file = new File(directory, fileName + ".dat");

        try {
            CompressedStreamTools.writeCompressed(data, new FileOutputStream(fileNew));

            if (fileOld.exists()) {
                fileOld.delete();
            }

            file.renameTo(fileOld);

            if (file.exists()) {
                file.delete();
            }

            fileNew.renameTo(file);

            if (fileNew.exists()) {
                fileNew.delete();
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            logger.warn("[Backpack] Couldn't save data.");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            logger.warn("[Backpack] Couldn't save data.");
        }
    }

    public void delete(File directory, String fileName) {
        File fileNew = new File(directory, fileName + ".dat_new");
        File fileOld = new File(directory, fileName + ".dat_old");
        File file = new File(directory, fileName + ".dat");

        if (fileOld.exists()) {
            fileOld.delete();
        }

        if (file.exists()) {
            file.delete();
        }

        if (fileNew.exists()) {
            fileNew.delete();
        }
    }
}
