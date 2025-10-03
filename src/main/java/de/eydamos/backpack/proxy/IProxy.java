package de.eydamos.backpack.proxy;

public interface IProxy {

    void registerHandlers();

    void registerKeybindings();

    void addNeiSupport();

    void invalidateBackpackCache(String uuid);
}
