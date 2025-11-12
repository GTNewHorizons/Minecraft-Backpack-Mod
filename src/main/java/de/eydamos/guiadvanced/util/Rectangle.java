package de.eydamos.guiadvanced.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import de.eydamos.backpack.misc.Constants;
import de.eydamos.guiadvanced.util.RenderHelper.BackgroundRepeat;

public class Rectangle {

    protected int width;
    protected int height;
    protected int u = 0;
    protected int v = 0;
    protected int z = 0;
    protected int uMax = 1;
    protected int vMax = 1;
    protected ResourceLocation graphic = Constants.guiCombined;
    protected BackgroundRepeat repeat = BackgroundRepeat.NONE;

    public Rectangle() {
        this(0, 0);
    }

    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }

    protected Minecraft getMinecraft() {
        return FMLClientHandler.instance().getClient();
    }

    public void setWidth(int value) {
        width = value;
    }

    public void setHeight(int value) {
        height = value;
    }

    public void setBackgroundPosition(int xOffset, int yOffset) {
        u = xOffset;
        v = yOffset;
    }

    public void setBackgroundSize(int sizeX, int sizeY) {
        uMax = sizeX;
        vMax = sizeY;
    }

    public void setBackgroundRepeat(BackgroundRepeat backgroundRepeat) {
        repeat = backgroundRepeat;
    }

    public void setBackground(ResourceLocation resourceLocation) {
        graphic = resourceLocation;
    }

    public void startDrawing() {
        Tessellator.instance.startDrawingQuads();
    }

    public void addBoxVertices(int x, int y) {
        Tessellator tessellator = Tessellator.instance;
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        if (repeat == BackgroundRepeat.NONE) {
            tessellator.addVertexWithUV(x, y + height, z, u * f, (v + height) * f1);
            tessellator.addVertexWithUV(x + width, y + height, z, (u + width) * f, (v + height) * f1);
            tessellator.addVertexWithUV(x + width, y, z, (u + width) * f, v * f1);
            tessellator.addVertexWithUV(x, y, z, u * f, v * f1);
        } else if (repeat == BackgroundRepeat.REPEAT) {
            uMax = Math.min(width, uMax);
            vMax = Math.min(height, vMax);
            int drawWidth, drawHeight;
            for (int i = 0; i <= width; i += uMax) {
                for (int j = 0; j <= height; j += vMax) {
                    drawWidth = Math.min(i + uMax, width);
                    drawHeight = Math.min(j + vMax, height);
                    tessellator.addVertexWithUV(x + i, y + drawHeight, z, u * f, (v + vMax) * f1);
                    tessellator.addVertexWithUV(x + drawWidth, y + drawHeight, z, (u + uMax) * f, (v + vMax) * f1);
                    tessellator.addVertexWithUV(x + drawWidth, y, z, (u + uMax) * f, v * f1);
                    tessellator.addVertexWithUV(x + i, y, z, u * f, v * f1);
                }
            }
        } else if (repeat == BackgroundRepeat.REPEAT_X) {
            int drawHeight = vMax = Math.min(height, vMax);
            int drawWidth;
            for (int i = 0; i <= width; i += uMax) {
                drawWidth = Math.min(i + uMax, width);
                tessellator.addVertexWithUV(x + i, y + drawHeight, z, u * f, (v + drawHeight) * f1);
                tessellator.addVertexWithUV(x + drawWidth, y + drawHeight, z, (u + uMax) * f, (v + drawHeight) * f1);
                tessellator.addVertexWithUV(x + drawWidth, y, z, (u + uMax) * f, v * f1);
                tessellator.addVertexWithUV(x + i, y, z, u * f, v * f1);
            }
        } else if (repeat == BackgroundRepeat.REPEAT_Y) {
            int drawWidth = uMax = Math.min(width, uMax);
            int drawHeight;
            for (int i = 0; i <= height; i += vMax) {
                drawHeight = Math.min(i + vMax, height);
                tessellator.addVertexWithUV(x, y + drawHeight, z, u * f, (v + vMax) * f1);
                tessellator.addVertexWithUV(x + drawWidth, y + drawHeight, z, (u + drawWidth) * f, (v + vMax) * f1);
                tessellator.addVertexWithUV(x + drawWidth, y + i, z, (u + drawWidth) * f, v * f1);
                tessellator.addVertexWithUV(x, y + i, z, u * f, v * f1);
            }
        } else {
            tessellator.addVertexWithUV(x, y + height, z, u * f, vMax * f1);
            tessellator.addVertexWithUV(x + width, y + height, z, uMax * f, vMax * f1);
            tessellator.addVertexWithUV(x + width, y, z, uMax * f, v * f1);
            tessellator.addVertexWithUV(x, y, z, u * f, v * f1);
        }
    }

    public void performDrawing() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        getMinecraft().getTextureManager().bindTexture(graphic);
        Tessellator.instance.draw();
    }
}
