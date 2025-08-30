package de.eydamos.backpack.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.backpack.network.message.MessageBackpackInfo;
import de.eydamos.backpack.network.message.MessageBackpackInfoRequest;
import de.eydamos.backpack.network.message.MessageGuiCommand;
import de.eydamos.backpack.network.message.MessageOpenBackpack;
import de.eydamos.backpack.network.message.MessageOpenGui;
import de.eydamos.backpack.network.message.MessageOpenPersonalSlot;
import de.eydamos.backpack.network.message.MessagePersonalBackpack;
import de.eydamos.backpack.network.message.MessageRecipe;
import de.eydamos.backpack.network.message.MessageRenameBackpack;

public class PacketHandlerBackpack {

    public final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID);

    public void initialise() {
        // these packages are send from the client to the server
        networkWrapper.registerMessage(MessageRenameBackpack.class, MessageRenameBackpack.class, 0, Side.SERVER);
        networkWrapper.registerMessage(MessageOpenGui.class, MessageOpenGui.class, 1, Side.SERVER);
        networkWrapper.registerMessage(MessageGuiCommand.class, MessageGuiCommand.class, 2, Side.SERVER);
        networkWrapper.registerMessage(MessagePersonalBackpack.class, MessagePersonalBackpack.class, 3, Side.SERVER);
        networkWrapper.registerMessage(MessageRecipe.class, MessageRecipe.class, 4, Side.SERVER);
        networkWrapper
                .registerMessage(MessageBackpackInfoRequest.class, MessageBackpackInfoRequest.class, 5, Side.SERVER);

        // these packages are send from the server to the client
        networkWrapper.registerMessage(MessageOpenPersonalSlot.class, MessageOpenPersonalSlot.class, 10, Side.CLIENT);
        networkWrapper.registerMessage(MessageOpenBackpack.class, MessageOpenBackpack.class, 11, Side.CLIENT);
        networkWrapper.registerMessage(MessagePersonalBackpack.class, MessagePersonalBackpack.class, 12, Side.CLIENT);
        networkWrapper.registerMessage(MessageBackpackInfo.class, MessageBackpackInfo.class, 13, Side.CLIENT);
    }
}
