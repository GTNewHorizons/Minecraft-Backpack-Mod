package de.eydamos.backpack.misc;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationBackpack {

    public static Configuration config;

    public static int ENDER_RECIPE;
    public static int BACKPACK_SLOTS_S;
    public static int BACKPACK_SLOTS_L;
    public static int BACKPACK_SLOTS_M;
    public static int MAX_BACKPACK_AMOUNT;
    public static boolean RENDER_BACKPACK_MODEL;
    public static boolean OPEN_ONLY_PERSONAL_BACKPACK;
    public static boolean AIRSHIP_MOD_COMPATIBILITY;
    public static boolean DISABLE_BACKPACKS;
    public static boolean DISABLE_BIG_BACKPACKS;
    public static boolean DISABLE_ENDER_BACKPACKS;
    public static boolean DISABLE_WORKBENCH_BACKPACKS;
    public static boolean BIG_BY_UPGRADE_ONLY;
    public static String[] DISALLOW_ITEM_IDS;
    public static String[] FORBIDDEN_DIMENSIONS;
    public static String[] DEFAULT_IDS = {};
    public static boolean ALLOW_SOULBOUND;

    public static boolean NEISupport = false;
    public static boolean PLAY_OPEN_SOUND = false;

    public static void init(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }

    public static void loadConfiguration() {
        ENDER_RECIPE = config.get(Configuration.CATEGORY_GENERAL, "enderRecipe", 0, getEnderRecipeComment()).getInt();
        if (ENDER_RECIPE < 0 || ENDER_RECIPE > 1) {
            ENDER_RECIPE = 0;
        }
        BACKPACK_SLOTS_S = config
                .get(Configuration.CATEGORY_GENERAL, "backpackSlotsS", 27, getBackpackSlotComment("small")).getInt();
        if (BACKPACK_SLOTS_S < 1 || BACKPACK_SLOTS_S > 128) {
            BACKPACK_SLOTS_S = 27;
        }
        BACKPACK_SLOTS_M = config
                .get(Configuration.CATEGORY_GENERAL, "backpackSlotsM", 36, getBackpackSlotComment("medium")).getInt();
        if (BACKPACK_SLOTS_M < 1 || BACKPACK_SLOTS_M > 128) {
            BACKPACK_SLOTS_M = 36;
        }
        BACKPACK_SLOTS_L = config
                .get(Configuration.CATEGORY_GENERAL, "backpackSlotsL", 54, getBackpackSlotComment("large")).getInt();
        if (BACKPACK_SLOTS_L < 1 || BACKPACK_SLOTS_L > 128) {
            BACKPACK_SLOTS_L = 54;
        }
        MAX_BACKPACK_AMOUNT = config
                .get(Configuration.CATEGORY_GENERAL, "maxBackpackAmount", 0, getMaxBackpackAmountComment()).getInt();
        if (MAX_BACKPACK_AMOUNT < 0 || MAX_BACKPACK_AMOUNT > 36) {
            MAX_BACKPACK_AMOUNT = 0;
        }
        RENDER_BACKPACK_MODEL = config
                .get(Configuration.CATEGORY_GENERAL, "renderBackpackModel", true, getRenderBackpackModelComment())
                .getBoolean(true);
        OPEN_ONLY_PERSONAL_BACKPACK = config.get(
                Configuration.CATEGORY_GENERAL,
                "openOnlyWornBackpacks",
                false,
                getOpenOnlyPersonalBackpacksComment()).getBoolean(false);
        AIRSHIP_MOD_COMPATIBILITY = config.get(
                Configuration.CATEGORY_GENERAL,
                "airshipModCompatibility",
                false,
                getAirshipModCompatibilityComment()).getBoolean(false);
        DISABLE_BACKPACKS = config
                .get(Configuration.CATEGORY_GENERAL, "disableBackpacks", false, getDisableBackpacksComment())
                .getBoolean(false);
        DISABLE_BIG_BACKPACKS = config
                .get(Configuration.CATEGORY_GENERAL, "disableBigBackpacks", false, getDisableBigBackpacksComment())
                .getBoolean(false);
        DISABLE_ENDER_BACKPACKS = config
                .get(Configuration.CATEGORY_GENERAL, "disableEnderBackpack", false, getDisableEnderBackpacksComment())
                .getBoolean(false);
        DISABLE_WORKBENCH_BACKPACKS = config.get(
                Configuration.CATEGORY_GENERAL,
                "disableWorkbenchBackpack",
                false,
                getDisableWorkbenchBackpacksComment()).getBoolean(false);
        BIG_BY_UPGRADE_ONLY = config
                .get(Configuration.CATEGORY_GENERAL, "bigByUpgradeOnly", false, getBigByUpgradeOnlyComment())
                .getBoolean(false);

        DISALLOW_ITEM_IDS = config
                .get(Configuration.CATEGORY_GENERAL, "disallowItems", DEFAULT_IDS, getDisallowItemsComment())
                .getStringList();
        FORBIDDEN_DIMENSIONS = config.get(
                Configuration.CATEGORY_GENERAL,
                "forbiddenDimensions",
                DEFAULT_IDS,
                getForbiddenDimensionsComment()).getStringList();

        ///
        PLAY_OPEN_SOUND = config.get(Configuration.CATEGORY_GENERAL, "playSound", true, getPlaySoundComment())
                .getBoolean(true);

        ALLOW_SOULBOUND = config.get(Configuration.CATEGORY_GENERAL, "allowSoulbound", true, getAllowSoulboundComment())
                .getBoolean(false);

        if (config.hasChanged()) {
            config.save();
        }
    }

    private static String getEnderRecipeComment() {
        return """
                ##############
                Recipe to craft ender backpack
                0 ender chest
                1 eye of the ender
                ##############""";
    }

    private static String getBackpackSlotComment(String type) {
        return "##############\n" + "Number of slots a "
                + type
                + " backpack has\n"
                + "valid: integers 1-128\n"
                + "##############";
    }

    private static String getMaxBackpackAmountComment() {
        return """
                ##############
                Number of backpacks a player can have in his inventory
                valid: integers 0-36
                0 = unlimited
                ##############""";
    }

    private static String getRenderBackpackModelComment() {
        return """
                ##############
                If true the backpack 3D model will be rendered.
                ##############""";
    }

    private static String getOpenOnlyPersonalBackpacksComment() {
        return """
                ##############
                If true you can only open a backpack that you wear in the extra slot
                ##############""";
    }

    private static String getAirshipModCompatibilityComment() {
        return """
                ##############
                If true normal backpack requires a chest in the middle
                ##############""";
    }

    private static String getDisableBackpacksComment() {
        return """
                ##############
                If true small backpacks are not craftable
                ##############""";
    }

    private static String getDisableBigBackpacksComment() {
        return """
                ##############
                If true big backpacks are not craftable
                ##############""";
    }

    private static String getDisableEnderBackpacksComment() {
        return """
                ##############
                If true ender backpacks are not craftable
                ##############""";
    }

    private static String getDisableWorkbenchBackpacksComment() {
        return """
                ##############
                If true workbench backpacks are not craftable
                ##############""";
    }

    private static String getBigByUpgradeOnlyComment() {
        return """
                ##############
                If true big backpacks can only crafted by upgrading a small one
                ##############""";
    }

    private static String getDisallowItemsComment() {
        return """
                ##############
                Example:
                disallowItems: minecraft:dirt

                This will disallow dirt in backpacks.
                ##############""";
    }

    private static String getForbiddenDimensionsComment() {
        return """
                ##############
                Example:
                forbiddenDimensions: 0

                This will disallow backpacks inventory for Overworld (id = 0).
                ##############""";
    }

    private static String getPlaySoundComment() {
        return """
                ##############
                If true backpack will play opening sound effect
                ##############""";
    }

    private static String getAllowSoulboundComment() {
        return """
                ##############
                If true backpack will stay in your backpack slot on death when enchanted with EnderIO soulbound.
                ##############""";
    }
}
