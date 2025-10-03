package de.eydamos.backpack.proxy;

import java.lang.reflect.Method;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import de.eydamos.backpack.gui.GuiWorkbenchBackpack;
import de.eydamos.backpack.handler.EventHandlerClientOnly;
import de.eydamos.backpack.handler.KeyInputHandler;
import de.eydamos.backpack.misc.BackpackUsageCache;
import de.eydamos.backpack.misc.ConfigurationBackpack;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.nei.OverlayHandlerBackpack;

public class ClientProxy extends CommonProxy {

    @Override
    public void registerHandlers() {
        super.registerHandlers();
        FMLCommonHandler.instance().bus().register(new KeyInputHandler());

        if (ConfigurationBackpack.RENDER_BACKPACK_MODEL) {
            MinecraftForge.EVENT_BUS.register(new EventHandlerClientOnly());
        }
    }

    @Override
    public void registerKeybindings() {
        ClientRegistry.registerKeyBinding(KeyInputHandler.personalBackpack);
    }

    @Override
    public void addNeiSupport() {
        try {
            Class<?> API = Class.forName("codechicken.nei.api.API");
            Class<?> IOverlayHandler = Class.forName("codechicken.nei.api.IOverlayHandler");
            Method registerGuiOverlayHandler = API
                    .getDeclaredMethod("registerGuiOverlayHandler", Class.class, IOverlayHandler, String.class);

            registerGuiOverlayHandler.invoke(API, GuiWorkbenchBackpack.class, new OverlayHandlerBackpack(), "crafting");

            ConfigurationBackpack.NEISupport = true;

            FMLLog.log(Constants.MOD_ID, Level.INFO, "[Backpacks] NEI Support enabled");
        } catch (Exception e) {
            FMLLog.log(Constants.MOD_ID, Level.INFO, "[Backpacks] NEI Support couldn't be enabled");
        }
    }

    @Override
    public void invalidateBackpackCache(String uuid) {
        BackpackUsageCache.invalidate(uuid);
    }
}
