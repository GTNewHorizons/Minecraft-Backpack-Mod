package de.eydamos.backpack.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.opengl.GL11;

import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.misc.Constants;

public class BackpackModelWorker {

    final ModelBiped[] backModels = { new ModelBackpackS(), new ModelBackpackM(), new ModelBackpackL() };

    final float[][] backpackColors = new float[][] { { 0.6F, 0.3F, 0.1F }, // Leather
            { 0.1F, 0.1F, 0.1F }, // Black
            { 0.6F, 0.2F, 0.2F }, // Orange
            { 0.4F, 0.5F, 0.2F }, // Green
            { 0.4F, 0.3F, 0.2F }, // Brown
            { 0.2F, 0.3F, 0.7F }, // Blue dark
            { 0.5F, 0.25F, 0.7F }, // Purple
            { 0.3F, 0.5F, 0.6F }, // Cyan
            { 0.6F, 0.6F, 0.6F }, // Gray L
            { 0.3F, 0.3F, 0.3F }, // Gray?
            { 0.95F, 0.5F, 0.65F }, // Pink
            { 0.5F, 0.8F, 0.1F }, // Green lime
            { 0.9F, 0.9F, 0.2F }, // Yellow
            { 0.4F, 0.6F, 0.85F }, // Blue
            { 0.7F, 0.3F, 0.85F }, // Magenta
            { 0.85F, 0.5F, 0.2F }, // Orange
            { 1.0F, 1.0F, 1.0F }, // White
            { 0.1f, 0.2f, 0.2f } // Ender
    };

    public void renderAt(EntityPlayer player, int meta) {
        if (meta == -1) {
            return;
        }

        ModelBiped backModel = getModel(meta);
        if (backModel == null) {
            return;
        }

        GL11.glPushMatrix();
        // Get color from damage

        float[] colors = getColor(meta);
        GL11.glColor4f(colors[0], colors[1], colors[2], 1);

        Minecraft.getMinecraft().renderEngine.bindTexture(Constants.modelTexture);

        backModel.render(player, 0F, 0F, 0F, 0F, 0F, 0.0625F);

        GL11.glPopMatrix();
    }

    public float[] getColor(int meta) {

        // Ender color
        if (isEnder(meta)) {
            return backpackColors[17];
        }

        // Leather color
        if (isCrafting(meta)) {
            return backpackColors[0];
        }

        int colorIndex = meta % 100;
        // Something wrong

        if (colorIndex < 0 || colorIndex > backpackColors.length - 1) {
            // Leather color
            return backpackColors[0];
        }

        return backpackColors[colorIndex];
    }

    public ModelBiped getModel(int meta) {

        // Small model
        if (isEnder(meta)) {
            return backModels[0];
        }

        int index = getModelIndex(meta);
        return backModels[index];
    }

    boolean isEnder(int meta) {
        return meta == ItemsBackpack.ENDERBACKPACK;
    }

    boolean isCrafting(int meta) {
        return meta == 17 || meta == 217;
    }

    public int getModelIndex(int meta) {
        int index = meta / 100;

        if (index < 0 || index > backModels.length - 1) {
            return 0;
        }
        return index;
    }
}
