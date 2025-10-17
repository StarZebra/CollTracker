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
            options = {"Diamond","Gold","Emerald","Lapis","Redstone","Coal"},
            category = "Mining"
    )
    public static int collection = 0;

    @HUD(
            name = "Collection Tracker HUD",
            category = "HUD"
    )
    public CollectionHUD collectionHUD = new CollectionHUD();

}
