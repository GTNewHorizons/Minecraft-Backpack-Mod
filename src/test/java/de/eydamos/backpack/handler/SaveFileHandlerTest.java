package de.eydamos.backpack.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

// Prevent a dead shared IO thread from hanging the test process.
@Timeout(30)
public class SaveFileHandlerTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    public void defersWritesUntilTheWorldIsSaved() throws Exception {
        SaveFileHandler handler = new SaveFileHandler();
        prepareDirectories(handler);
        String uuid = uuid(1);
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
    public void skipsStaleBatchQueuedBeforeANewerOne() {
        CapturingSaveFileHandler handler = createHandler();
        String uuid = uuid(3);

        handler.saveBackpack(tag(1), uuid);
        for (int i = 0; i < 4; i++) {
            handler.saveBackpack(tag(1), uuid(100 + i));
        }
        handler.queueDirtyFiles();

        handler.saveBackpack(tag(2), uuid);
        handler.queueDirtyFiles();

        assertEquals(2, handler.batches.size());
        handler.drainNewestFirst();

        assertEquals(2, readBackpack(handler, uuid).getInteger("value"));
    }

    @Test
    public void retriesOnTheNextWorldSaveWhenAWriteFails() {
        CapturingSaveFileHandler handler = createHandler();
        String uuid = uuid(4);

        handler.saveBackpack(tag(1), uuid);
        handler.queueDirtyFiles();

        File blocker = new File(handler.backpackDir, uuid + ".dat_new");
        assertTrue(blocker.mkdirs());
        handler.drainRoundRobin();
        assertFalse(new File(handler.backpackDir, uuid + ".dat").exists());

        assertTrue(blocker.delete());
        handler.queueDirtyFiles();
        handler.drainRoundRobin();

        assertEquals(1, readBackpack(handler, uuid).getInteger("value"));
    }

    @Test
    public void loadsAndRepairsFallbackFile() {
        CapturingSaveFileHandler handler = createHandler();
        String uuid = uuid(5);
        File primary = new File(handler.backpackDir, uuid + ".dat");
        write(new File(handler.backpackDir, uuid + ".dat_old"), tag(3));

        assertTrue(handler.backpackSaveExists(uuid));
        assertEquals(3, handler.loadBackpack(uuid).getInteger("value"));

        handler.queueDirtyFiles();
        handler.drainRoundRobin();

        assertTrue(primary.isFile());
        assertEquals(3, read(primary).getInteger("value"));
    }

    @Test
    public void readsHalfRotatedWriteFromTheTemporaryFile() {
        CapturingSaveFileHandler handler = createHandler();
        String uuid = uuid(6);
        write(new File(handler.backpackDir, uuid + ".dat_new"), tag(7));

        assertTrue(handler.backpackSaveExists(uuid));
        assertEquals(7, handler.loadBackpack(uuid).getInteger("value"));

        handler.queueDirtyFiles();
        handler.drainRoundRobin();

        assertEquals(7, readBackpack(handler, uuid).getInteger("value"));
    }

    @Test
    public void remembersMissesWithoutTurningThemIntoWrites() {
        CapturingSaveFileHandler handler = createHandler();
        String uuid = uuid(9);

        assertFalse(handler.playerSaveExists(uuid));
        assertTrue(handler.loadPlayer(uuid).hasNoTags());
        assertFalse(handler.playerSaveExists(uuid));
        handler.queueDirtyFiles();
        assertTrue(handler.batches.isEmpty());
        assertFalse(new File(handler.playerDir, uuid + ".dat").exists());

        handler.savePlayer(tag(4), uuid);
        assertTrue(handler.playerSaveExists(uuid));
        handler.queueDirtyFiles();
        handler.drainRoundRobin();

        assertEquals(4, read(new File(handler.playerDir, uuid + ".dat")).getInteger("value"));
    }

    @Test
    public void ignoresSavesBeforeTheWorldDirectoryIsKnown() {
        SaveFileHandler handler = new SaveFileHandler();
        String uuid = uuid(8);

        handler.saveBackpack(tag(1), uuid);
        handler.savePlayer(tag(1), uuid);

        assertFalse(handler.backpackSaveExists(uuid));
        assertFalse(handler.playerSaveExists(uuid));
        assertTrue(handler.loadBackpack(uuid).hasNoTags());
        assertTrue(handler.loadPlayer(uuid).hasNoTags());
    }

    private static class CapturingSaveFileHandler extends SaveFileHandler {

        private final List<IThreadedFileIO> batches = new ArrayList<>();

        @Override
        void queueIO(IThreadedFileIO batch) {
            batches.add(batch);
        }

        private void drainRoundRobin() {
            while (!batches.isEmpty()) {
                batches.removeIf(batch -> !batch.writeNextIO());
            }
        }

        private void drainNewestFirst() {
            for (int i = batches.size() - 1; i >= 0; i--) {
                IThreadedFileIO batch = batches.get(i);
                while (batch.writeNextIO());
            }
            batches.clear();
        }
    }

    private CapturingSaveFileHandler createHandler() {
        CapturingSaveFileHandler handler = new CapturingSaveFileHandler();
        prepareDirectories(handler);
        return handler;
    }

    private void prepareDirectories(SaveFileHandler handler) {
        File world = temporaryDirectory.toFile();
        handler.backpackDir = new File(world, "backpacks/backpacks");
        handler.playerDir = new File(world, "backpacks/player");
        assertTrue(handler.backpackDir.mkdirs());
        assertTrue(handler.playerDir.mkdirs());
    }

    private static String uuid(int value) {
        return String.format("00000000-0000-0000-0000-%012d", value);
    }

    private static NBTTagCompound tag(int value) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("value", value);
        return tag;
    }

    private static NBTTagCompound readBackpack(SaveFileHandler handler, String uuid) {
        return read(new File(handler.backpackDir, uuid + ".dat"));
    }

    private static void write(File file, NBTTagCompound tag) {
        try (FileOutputStream stream = new FileOutputStream(file)) {
            CompressedStreamTools.writeCompressed(tag, stream);
        } catch (Exception exception) {
            throw new AssertionError("Couldn't write " + file, exception);
        }
    }

    private static NBTTagCompound read(File file) {
        try (FileInputStream stream = new FileInputStream(file)) {
            return CompressedStreamTools.readCompressed(stream);
        } catch (Exception exception) {
            throw new AssertionError("Couldn't read " + file, exception);
        }
    }
}
