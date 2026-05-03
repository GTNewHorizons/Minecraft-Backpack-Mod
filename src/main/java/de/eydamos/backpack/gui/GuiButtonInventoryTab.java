package de.eydamos.backpack.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonInventoryTab extends GuiButton {

    private static final ResourceLocation TABS_TEXTURE = new ResourceLocation(
            "textures/gui/container/creative_inventory/tabs.png");
    private static final RenderItem ITEM_RENDERER = new RenderItem();

    private final ItemStack backpackStack;

    public GuiButtonInventoryTab(int id, int x, int y, ItemStack backpackStack) {
        super(id, x, y, 28, 32, "");
        this.backpackStack = backpackStack;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        // enabled=true → raised (clickable), enabled=false → pressed (current tab)
        int yTexPos = enabled ? 2 : 32;
        int ySize = enabled ? 25 : 32;
        int yPos = yPosition + (enabled ? 3 : 0);

        mc.renderEngine.bindTexture(TABS_TEXTURE);
        // Use left-tab UV (xOffset=0): first column of the tabs texture
        drawTexturedModalRect(xPosition, yPos, 0, yTexPos, 28, ySize);

        RenderHelper.enableGUIStandardItemLighting();
        this.zLevel = 100.0F;
        ITEM_RENDERER.zLevel = 100.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        ITEM_RENDERER.renderItemAndEffectIntoGUI(
                mc.fontRenderer,
                mc.renderEngine,
                backpackStack,
                xPosition + 6,
                yPosition + 8);
        ITEM_RENDERER.renderItemOverlayIntoGUI(
                mc.fontRenderer,
                mc.renderEngine,
                backpackStack,
                xPosition + 6,
                yPosition + 8);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        ITEM_RENDERER.zLevel = 0.0F;
        this.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return enabled && visible
                && mouseX >= xPosition
                && mouseY >= yPosition
                && mouseX < xPosition + width
                && mouseY < yPosition + height;
    }
}
