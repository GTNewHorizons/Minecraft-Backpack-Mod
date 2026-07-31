package de.eydamos.backpack.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.ThreadedFileIOBase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class SaveFileHandlerTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void defersAndCoalescesWrites() throws Exception {
        SaveFileHandler handler = createHandler();
        String uuid = "00000000-0000-0000-0000-000000000001";
        File file = new File(handler.backpackDir, uuid + ".dat");

        handler.saveBackpack(tag(1), uuid);
        handler.saveBackpack(tag(2), uuid);
        assertFalse(file.exists());

        handler.queueDirtyFiles();
        ThreadedFileIOBase.threadedIOInstance.waitForFinish();

        assertTrue(file.isFile());
        assertEquals(2, read(file).getInteger("value"));
    }

    @Test
    public void loadsAndRepairsFallbackFile() throws Exception {
        SaveFileHandler handler = createHandler();
        String uuid = "00000000-0000-0000-0000-000000000002";
        File primary = new File(handler.backpackDir, uuid + ".dat");
        File fallback = new File(handler.backpackDir, uuid + ".dat_old");
        write(fallback, tag(3));

        assertTrue(handler.backpackSaveExists(uuid));
        assertEquals(3, handler.loadBackpack(uuid).getInteger("value"));

        handler.queueDirtyFiles();
        ThreadedFileIOBase.threadedIOInstance.waitForFinish();

        assertTrue(primary.isFile());
        assertEquals(3, read(primary).getInteger("value"));
    }

    private SaveFileHandler createHandler() {
        File world = temporaryDirectory.toFile();
        SaveFileHandler handler = new SaveFileHandler();
        handler.backpackDir = new File(world, "backpacks/backpacks");
        handler.playerDir = new File(world, "backpacks/player");
        assertTrue(handler.backpackDir.mkdirs());
        assertTrue(handler.playerDir.mkdirs());
        return handler;
    }

    private NBTTagCompound tag(int value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("value", value);
        return tag;
    }

    private void write(File file, NBTTagCompound tag) throws Exception {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            CompressedStreamTools.writeCompressed(tag, stream);
        }
    }

    private NBTTagCompound read(File file) throws Exception {
        try (FileInputStream stream = new FileInputStream(file)) {
            return CompressedStreamTools.readCompressed(stream);
        }
    }
}
