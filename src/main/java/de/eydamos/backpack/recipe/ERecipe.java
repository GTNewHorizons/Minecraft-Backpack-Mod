package de.eydamos.backpack.recipe;

import de.eydamos.backpack.item.EBackpack;
import de.eydamos.backpack.misc.EItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public enum ERecipe {
    STICK_STONE(EItem.STICK_STONE.getItemStack(4), ECategory.SHAPED_OREDICT, "S", "S", 'S', "stone"),
    STICK_IRON(EItem.STICK_IRON.getItemStack(2), ECategory.SHAPED_OREDICT, "S", "S", 'S', "ingotIron"),
    LEATHER_BOUND(EItem.BOUND_LEATHER.getItemStack(2), ECategory.SHAPED, "SSS", "LSL", "SSS", 'S', Items.STRING, 'L', Items.LEATHER),
    LEATHER_TANNED(EItem.TANNED_LEATHER.getItemStack(1), ECategory.FURNACE, EItem.BOUND_LEATHER.getItemStack(1), 0.1f),
    FRAME_WOOD(EItem.FRAME_WOOD.getItemStack(1), ECategory.SHAPED, "WSW", "S S", "WSW", 'W', Items.STICK, 'S', Items.STRING),
    FRAME_WOOD2(EItem.FRAME_WOOD.getItemStack(1), ECategory.SHAPED, "SWS", "W W", "SWS", 'W', Items.STICK, 'S', Items.STRING),
    FRAME_STONE(EItem.FRAME_STONE.getItemStack(1), ECategory.SHAPED_OREDICT, "WSW", "S S", "WSW", 'W', "stickStone", 'S', Items.STRING),
    FRAME_STONE2(EItem.FRAME_STONE.getItemStack(1), ECategory.SHAPED_OREDICT, "SWS", "W W", "SWS", 'W', "stickStone", 'S', Items.STRING),
    FRAME_IRON(EItem.FRAME_IRON.getItemStack(1), ECategory.SHAPED_OREDICT, "WSW", "S S", "WSW", 'W', "stickIron", 'S', Items.STRING),
    FRAME_IRON2(EItem.FRAME_IRON.getItemStack(1), ECategory.SHAPED_OREDICT, "SWS", "W W", "SWS", 'W', "stickIron", 'S', Items.STRING),
    BACKPACK_PICE_TOP(null, ECategory.CUSTOM, new RecipeBackpackPieceTop(EItem.BACKPACK_PICE_TOP.getItemStack(1))),
    BACKPACK_PICE_MIDDLE(null, ECategory.CUSTOM, new RecipeBackpackPieceMiddle(EItem.BACKPACK_PICE_MIDDLE.getItemStack(1))),
    BACKPACK_PICE_BOTTOM(null, ECategory.CUSTOM, new RecipeBackpackPieceBottom(EItem.BACKPACK_PICE_BOTTOM.getItemStack(1))),
    BACKPACK_SMALL(null, ECategory.CUSTOM, new RecipeBackpackSmall(EBackpack.SMALL.getItemStack())),
    BACKPACK_MEDIUM(null, ECategory.CUSTOM, new RecipeBackpackMedium(EBackpack.MEDIUM.getItemStack())),
    BACKPACK_BIG(null, ECategory.CUSTOM, new RecipeBackpackBig(EBackpack.BIG.getItemStack())),
    RECOLOR_BACKPACK(null, ECategory.CUSTOM, new RecipeRecolorBackpack());

    protected ItemStack result;
    protected ECategory category;
    protected Object[] recipeDefinition;

    ERecipe(ItemStack result, ECategory category, Object... recipeDefinition) {
        this.result = result;
        this.category = category;
        this.recipeDefinition = recipeDefinition;
    }

    public void registerRecipe() {
        switch (category) {
            case SHAPED:
                GameRegistry.addShapedRecipe(result, recipeDefinition);
                break;
            case SHAPED_OREDICT:
                GameRegistry.addRecipe(new ShapedOreRecipe(result, recipeDefinition));
                break;
            case SHAPELESS:
                GameRegistry.addShapelessRecipe(result, recipeDefinition);
                break;
            case SHAPELESS_OREDICT:
                GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipeDefinition));
                break;
            case FURNACE:
                GameRegistry.addSmelting((ItemStack) recipeDefinition[0], result, (Float) recipeDefinition[1]);
                break;
            case CUSTOM:
                AbstractRecipe recipe = (AbstractRecipe) recipeDefinition[0];
                GameRegistry.addRecipe(recipe);
                recipe.registerAtRecipeSorter();
                break;
        }
    }

    public static void registerRecipes() {
        for (ERecipe recipe : values()) {
            recipe.registerRecipe();
        }
    }
}
