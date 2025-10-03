package de.eydamos.backpack.proxy;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import de.eydamos.backpack.Backpack;
import de.eydamos.backpack.handler.EventHandlerBackpack;

public abstract class CommonProxy implements IProxy {

    @Override
    public void registerHandlers() {
        Backpack.packetHandler.initialise();

        EventHandlerBackpack eventHandler = new EventHandlerBackpack();

        FMLCommonHandler.instance().bus().register(eventHandler);
        MinecraftForge.EVENT_BUS.register(eventHandler);
    }

    @Override
    public void registerKeybindings() {}

    @Override
    public void addNeiSupport() {}

    @Override
    public void invalidateBackpackCache(String uuid) {
        // Do nothing by default (server side)
    }
}
