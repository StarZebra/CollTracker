package me.starzebra.colltracker.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import me.starzebra.colltracker.hud.CollectionHUD;

public class SimpleConfig extends Config {

    public SimpleConfig() {
        super(new Mod("CollTracker", ModType.SKYBLOCK), "config.json");
        initialize();

        save();
    }

    @Switch(
            name = "Drill Fix",
            size = OptionSize.DUAL,
            category = "Mining"
    )
    public static boolean drillfix = false;

    @Switch(
            name = "Save stats",
            description = "Enable this if you want to save fun stats like total collections gained, total time spent mining, skymall procs etc.",
            category = "Misc"
    )
    public static boolean saveStats = false;

    @Switch(
            name = "Debug messages",
            category = "Misc"
    )
    public static boolean debugMsgs = false;

    @Checkbox(
            name = "Collection tracker",
            category = "Mining"
    )
    public static boolean colltracker = false;

    @Dropdown(
            name = "Collection to Track",
            //FUCK YOU OneConfig
            options = {
                    "Lapis Lazuli", "Redstone Dust",
                    "Umber", "Coal",
                    "Mycelium", "End Stone",
                    "Nether Quartz", "Sand",
                    "Iron Ingot", "Tungsten",
                    "Obsidian", "Diamond",
                    "Cobblestone", "Glowstone Dust",
                    "Gold Ingot", "Flint",
                    "Hard Stone", "Mithril",
                    "Emerald", "Red Sand",
                    "Ice", "Glacite",
                    "Sulphur", "Netherrack",
                    "Precursor Parts"},

            category = "Mining"
    )
    public static int collection = 0;

    @Checkbox(
            name = "Show rate split",
            description = "Whether to show the (stash/sacks) split in the HUD",
            category = "HUD"
    )
    public static boolean shouldShowRateSplit = false;

    @DualOption(
            name = "Rate split display",
            description = "Whether to display the rate split in percent or strict numbers",
            left = "Percent",
            right = "Numbers",
            category = "HUD"
    )
    public static boolean rateSplitOption = false;

    @Switch(
            name = "Show average sack",
            description = "Whether to display your average sack gain on the HUD.",
            category = "HUD"
    )
    public static boolean showAverageSack = false;

    @Switch(
            name = "Show efficiency",
            description = "Whether to display your efficiency on the HUD.",
            category = "HUD"
    )
    public static boolean showEfficiency = false;

    @HUD(
            name = "Collection Tracker HUD",
            category = "HUD"
    )
    public CollectionHUD collectionHUD = new CollectionHUD();



}
