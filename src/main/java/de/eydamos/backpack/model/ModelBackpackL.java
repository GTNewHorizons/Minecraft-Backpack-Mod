package de.eydamos.backpack.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBackpackL extends ModelBiped {

    final ModelRenderer bagMain;

    public ModelBackpackL() {
        this(0.0F);
    }

    public ModelBackpackL(float par1) {
        this(par1, 0.0F, 64, 32);
    }

    public ModelBackpackL(float enlargement, float yShift, int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        ModelRenderer bagTop = new ModelRenderer(this, 44, 0);
        bagTop.addBox(-3F, -0.5F + yShift, 2F, 6, 1, 4, enlargement);
        setRotation(bagTop, 0F, 0F, 0F);

        bagMain = new ModelRenderer(this, 42, 6);
        bagMain.addBox(-3.5F, 0F + yShift, 2F, 7, 10, 4, enlargement);
        setRotation(bagMain, 0F, 0F, 0F);

        // Uppers
        ModelRenderer pocketUpperLeft = new ModelRenderer(this, 33, 5);
        pocketUpperLeft.addBox(3.2F, 4F + yShift, 2.5F, 1, 2, 3, enlargement);
        setRotation(pocketUpperLeft, 0F, 0F, 0F);

        ModelRenderer pocketLeft = new ModelRenderer(this, 33, 5);
        pocketLeft.addBox(3.5F, 6.4F + yShift, 2.5F, 1, 3, 3, enlargement);
        setRotation(pocketLeft, 0F, 0F, 0F);

        // Edit
        ModelRenderer pocketUpperRight = new ModelRenderer(this, 33, 13);
        pocketUpperRight.addBox(-4.2F, 4F + yShift, 2.5F, 1, 2, 3, enlargement);
        setRotation(pocketUpperRight, 0F, 0F, 0F);

        ModelRenderer pocketRight = new ModelRenderer(this, 33, 13);
        pocketRight.addBox(-4.5F, 6.4F + yShift, 2.5F, 1, 3, 3, enlargement);
        setRotation(pocketRight, 0F, 0F, 0F);

        /// EDIT
        ModelRenderer pocketUpperFront = new ModelRenderer(this, 15, 27);
        pocketUpperFront.addBox(-2F, 1.3F + yShift, 6.0F, 4, 3, 1, enlargement);
        setRotation(pocketUpperFront, 0F, 0F, 0F);

        //
        ModelRenderer pocketFront = new ModelRenderer(this, 15, 27);
        pocketFront.addBox(-2F, 4.7F + yShift, 6.2F, 4, 4, 1, enlargement);
        setRotation(pocketFront, 0F, 0F, 0F);

        ModelRenderer ledgeFront1 = new ModelRenderer(this, 0, 23);
        ledgeFront1.addBox(-3F, 1F + yShift, 5.3F, 6, 8, 1, enlargement);
        setRotation(ledgeFront1, 0F, 0F, 0F);
        ModelRenderer ledgeFront2 = new ModelRenderer(this, 1, 20);
        ledgeFront2.addBox(-2F, 0.6F + yShift, 5.3F, 4, 1, 1, enlargement);
        setRotation(ledgeFront2, 0F, 0F, 0F);
        ModelRenderer ledgeFront3 = new ModelRenderer(this, 1, 17);
        ledgeFront3.addBox(-2F, 8.5F + yShift, 5.3F, 4, 1, 1, enlargement);
        setRotation(ledgeFront3, 0F, 0F, 0F);

        bagMain.addChild(bagTop);

        bagMain.addChild(pocketUpperLeft);
        bagMain.addChild(pocketLeft);

        bagMain.addChild(pocketUpperRight);
        bagMain.addChild(pocketRight);

        bagMain.addChild(pocketUpperFront);
        bagMain.addChild(pocketFront);

        bagMain.addChild(ledgeFront1);
        bagMain.addChild(ledgeFront2);
        bagMain.addChild(ledgeFront3);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        bagMain.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        if (entity != null && entity.isSneaking()) {
            bagMain.rotateAngleX = 0.5F;
        } else {
            bagMain.rotateAngleX = 0.0F;
        }
    }
}
