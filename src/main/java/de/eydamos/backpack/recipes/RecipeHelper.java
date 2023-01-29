package de.eydamos.backpack.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import cpw.mods.fml.common.registry.GameRegistry;
import de.eydamos.backpack.item.ItemsBackpack;
import de.eydamos.backpack.misc.ConfigurationBackpack;

public class RecipeHelper {

    public static void registerRecipes() {
        ItemStack backpackStack = new ItemStack(ItemsBackpack.backpack, 1, 0);
        ItemStack boundLeatherStack = new ItemStack(ItemsBackpack.boundLeather);

        if (!ConfigurationBackpack.DISABLE_BACKPACKS) {
            // normal backpack without dye
            if (ConfigurationBackpack.AIRSHIP_MOD_COMPATIBILITY) {
                GameRegistry.addRecipe(backpackStack, "LLL", "LCL", "LLL", 'L', Items.leather, 'C', Blocks.chest);
            } else {
                GameRegistry.addRecipe(backpackStack, "LLL", "L L", "LLL", 'L', Items.leather);
            }
        }

        if (!ConfigurationBackpack.DISABLE_BIG_BACKPACKS && !ConfigurationBackpack.BIG_BY_UPGRADE_ONLY) {
            // normal big backpack without dye
            backpackStack = new ItemStack(ItemsBackpack.backpack, 1, 200);
            GameRegistry.addRecipe(backpackStack, "LLL", "L L", "LLL", 'L', ItemsBackpack.tannedLeather);
        }

        if (!ConfigurationBackpack.DISABLE_BIG_BACKPACKS && !ConfigurationBackpack.BIG_BY_UPGRADE_ONLY) {
            // normal middle backpack without dye
            backpackStack = new ItemStack(ItemsBackpack.backpack, 1, 100);
            GameRegistry.addRecipe(backpackStack, "LLL", "LNL", "LLL", 'L', Items.leather, 'N', ItemsBackpack.backpack);
        }

        String[] dyes = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan",
                "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange",
                "dyeWhite" };

        // backpacks and big backpacks from black(0) to white(15) and added middle backpack dyes
        for (int i = 1; i < 17; i++) {
            if (!ConfigurationBackpack.DISABLE_BACKPACKS) {
                // backpacks
                backpackStack = new ItemStack(ItemsBackpack.backpack, 1, i);
                GameRegistry.addRecipe(
                        new ShapedOreRecipe(backpackStack, "LLL", "LDL", "LLL", 'L', Items.leather, 'D', dyes[i - 1]));
            }

            if (!ConfigurationBackpack.DISABLE_BIG_BACKPACKS && !ConfigurationBackpack.BIG_BY_UPGRADE_ONLY) {
                // big backpacks
                backpackStack = new ItemStack(ItemsBackpack.backpack, 1, 200 + i);
                GameRegistry.addRecipe(
                        new ShapedOreRecipe(
                                backpackStack,
                                "LLL",
                                "LDL",
                                "LLL",
                                'L',
                                ItemsBackpack.tannedLeather,
                                'D',
                                dyes[i - 1]));
            }
            if (!ConfigurationBackpack.DISABLE_BIG_BACKPACKS && !ConfigurationBackpack.BIG_BY_UPGRADE_ONLY) {
                // middle backpacks
                backpackStack = new ItemStack(ItemsBackpack.backpack, 1, 100 + i);
                GameRegistry.addRecipe(
                        new ShapedOreRecipe(
                                backpackStack,
                                "   ",
                                "MD ",
                                "   ",
                                'M',
                                new ItemStack(ItemsBackpack.backpack, 1, 100),
                                'D',
                                dyes[i - 1]));
            }
        }

        if (!ConfigurationBackpack.DISABLE_ENDER_BACKPACKS) {
            // ender Backpack
            if (ConfigurationBackpack.ENDER_RECIPE == 0) {
                backpackStack = new ItemStack(ItemsBackpack.backpack, 1, ItemsBackpack.ENDERBACKPACK);
                GameRegistry.addRecipe(backpackStack, "LLL", "LEL", "LLL", 'L', Items.leather, 'E', Blocks.ender_chest);
            } else {
                backpackStack = new ItemStack(ItemsBackpack.backpack, 1, ItemsBackpack.ENDERBACKPACK);
                GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL", 'L', Items.leather, 'D', Items.ender_eye);
            }
        }

        if (!ConfigurationBackpack.DISABLE_WORKBENCH_BACKPACKS) {
            // workbench Backpacks
            backpackStack = new ItemStack(ItemsBackpack.workbenchBackpack, 1, 17);
            GameRegistry.addRecipe(backpackStack, "LLL", "LWL", "LLL", 'L', Items.leather, 'W', Blocks.crafting_table);

            backpackStack = new ItemStack(ItemsBackpack.workbenchBackpack, 1, 217);
            GameRegistry.addRecipe(
                    backpackStack,
                    "LLL",
                    "LWL",
                    "LLL",
                    'L',
                    ItemsBackpack.tannedLeather,
                    'W',
                    Blocks.crafting_table);
        }

        // bound leather
        GameRegistry.addRecipe(boundLeatherStack, "SSS", "LSL", "SSS", 'S', Items.string, 'L', Items.leather);

        // tanned leather
        ItemStack tannedLeatherStack = new ItemStack(ItemsBackpack.tannedLeather);
        GameRegistry.addSmelting(ItemsBackpack.boundLeather, tannedLeatherStack, 0.1f);

        if (!ConfigurationBackpack.DISABLE_BIG_BACKPACKS) {
            // enhance backpack to big backpack
            GameRegistry.addRecipe(new RecipeEnhanceBackpack());
        }

        // recolor backpack
        GameRegistry.addRecipe(new RecipeRecolorBackpack());

        // intelligent workbench backpack
        GameRegistry.addRecipe(new RecipeIntelligentWorkbenchBackpack());
    }
}
