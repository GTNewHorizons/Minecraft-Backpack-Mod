package de.eydamos.backpack.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelBackpackS extends ModelBiped {

    final ModelRenderer bagMain;

    public ModelBackpackS() {
        this(0.0F);
    }

    public ModelBackpackS(float par1) {
        this(par1, 0.0F, 64, 32);
    }

    public ModelBackpackS(float enlargement, float yShift, int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        ModelRenderer bagTop = new ModelRenderer(this, 44, 0);
        bagTop.addBox(-3F, -0.5F + yShift, 2F, 6, 1, 4, enlargement);
        setRotation(bagTop, 0F, 0F, 0F);

        bagMain = new ModelRenderer(this, 42, 6);
        bagMain.addBox(-3.5F, 0F + yShift, 2F, 7, 8, 4, enlargement);
        setRotation(bagMain, 0F, 0F, 0F);

        ModelRenderer pocketLeft = new ModelRenderer(this, 33, 5);
        pocketLeft.addBox(3.5F, 4.4F + yShift, 2.5F, 1, 3, 3, enlargement);
        setRotation(pocketLeft, 0F, 0F, 0F);

        ModelRenderer pocketRight = new ModelRenderer(this, 33, 13);
        pocketRight.addBox(-4.5F, 4.4F + yShift, 2.5F, 1, 3, 3, enlargement);
        setRotation(pocketRight, 0F, 0F, 0F);

        //
        ModelRenderer pocketFront = new ModelRenderer(this, 15, 27);
        pocketFront.addBox(-2F, 4F + yShift, 6.2F, 4, 3, 1, enlargement);
        setRotation(pocketFront, 0F, 0F, 0F);

        ModelRenderer ledgeFront1 = new ModelRenderer(this, 0, 23);
        ledgeFront1.addBox(-3F, 1F + yShift, 5.3F, 6, 6, 1, enlargement);
        setRotation(ledgeFront1, 0F, 0F, 0F);

        ModelRenderer ledgeFront2 = new ModelRenderer(this, 1, 20);
        ledgeFront2.addBox(-2F, 0.6F + yShift, 5.3F, 4, 1, 1, enlargement);
        setRotation(ledgeFront2, 0F, 0F, 0F);

        ModelRenderer ledgeFront3 = new ModelRenderer(this, 1, 17);
        ledgeFront3.addBox(-2F, 6.5F + yShift, 5.3F, 4, 1, 1, enlargement);
        setRotation(ledgeFront3, 0F, 0F, 0F);

        bagMain.addChild(bagTop);

        bagMain.addChild(pocketLeft);

        bagMain.addChild(pocketRight);

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
