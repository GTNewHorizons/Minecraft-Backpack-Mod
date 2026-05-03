package de.eydamos.backpack.gui.tab;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractInventoryTab extends GuiButton {

    private static final ResourceLocation TABS_TEXTURE = new ResourceLocation(
            "textures/gui/container/creative_inventory/tabs.png");
    private static final RenderItem ITEM_RENDERER = new RenderItem();

    protected final ItemStack renderStack;
    protected final int xOffset;

    public AbstractInventoryTab(int id, int posX, int posY, ItemStack renderStack, int xOffset) {
        super(id, posX, posY, 28, 32, "");
        this.renderStack = renderStack;
        this.xOffset = xOffset;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int yTexPos = enabled ? 2 : 32;
        int ySize = enabled ? 25 : 32;
        int yPos = yPosition + (enabled ? 3 : 0);

        mc.renderEngine.bindTexture(TABS_TEXTURE);
        drawTexturedModalRect(xPosition, yPos, xOffset * 28, yTexPos, 28, ySize);
        if (enabled) {
            drawUnselectedTabDecorations(mc, xOffset, yTexPos, yPos, ySize);
        }

        RenderHelper.enableGUIStandardItemLighting();
        this.zLevel = 100.0F;
        ITEM_RENDERER.zLevel = 100.0F;
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        ITEM_RENDERER.renderItemAndEffectIntoGUI(
                mc.fontRenderer,
                mc.renderEngine,
                renderStack,
                xPosition + 6,
                yPosition + 8);
        ITEM_RENDERER
                .renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, renderStack, xPosition + 6, yPosition + 8);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        ITEM_RENDERER.zLevel = 0.0F;
        this.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
    }

    protected void drawUnselectedTabDecorations(Minecraft mc, int textureXOffset, int textureYStart, int tabTopY,
            int tabHeight) {}

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean inWindow = visible && mouseX >= xPosition
                && mouseY >= yPosition
                && mouseX < xPosition + width
                && mouseY < yPosition + height;
        if (!inWindow) return false;

        boolean shift = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        if (shift) {
            return onShiftTabClicked();
        }
        if (enabled) {
            onTabClicked();
            return true;
        }
        return false;
    }

    public boolean isHovering(int mouseX, int mouseY) {
        return visible && mouseX >= xPosition
                && mouseY >= yPosition
                && mouseX < xPosition + width
                && mouseY < yPosition + height;
    }

    public abstract void onTabClicked();

    public boolean onShiftTabClicked() {
        return false;
    }

    public List<String> getTooltip() {
        return null;
    }

    public abstract boolean shouldAddToList();
}
