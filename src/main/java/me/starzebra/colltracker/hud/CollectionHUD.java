package me.starzebra.colltracker.hud;

import cc.polyfrost.oneconfig.hud.TextHud;
import me.starzebra.colltracker.CollTracker;
import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.features.SackChatListener;

import java.text.DecimalFormat;
import java.util.List;

public class CollectionHUD extends TextHud {

    public static String displayedCPH = "";
    public static String displayedGain = "";
    public static String timeElapsedStr = "";
    public static String efficiencyStr = "";
    public static String medianStr = "";

    public CollectionHUD() {
        super(false);
    }

    @Override
    protected void getLines(List<String> lines, boolean example) {
        if(example && !CollTracker.isSessionActive()){
            lines.add("§bCollection Tracker");
            lines.add("§bDUMMYCOLL: 2,000,000,000");
            lines.add("§bRates: 50,000,000/h");
            lines.add("§bEfficiency: 99.78%");
            lines.add("§bTime Elapsed: 144,000s");
            lines.add("§bAverage Sack: 333");
        }
        if(SimpleConfig.colltracker && CollTracker.isSessionActive()){
            lines.add("§bCollection Tracker");
            lines.add("§b"+SackChatListener.supportedCollections.getOrDefault(SimpleConfig.collection, "NaC") + ": " + displayedGain);
            lines.add("§bRates: " + displayedCPH);
            lines.add("§bEfficiency: " + efficiencyStr);
            lines.add("§bTime Elapsed: " + timeElapsedStr);
            lines.add("§bAverage Sack: " + medianStr);
            if(CollTracker.session.isPaused()){
                lines.add("§c§lPAUSED");
            }
        }
    }

    public static void updateLines(){
        displayedCPH = String.format("%,d", CollTracker.session.getCollectionPerHour()) +"/h";
        displayedGain = String.format("%,d", CollTracker.session.getTotalItemsGained());
        efficiencyStr = new DecimalFormat("#.##").format(CollTracker.session.getEfficiency() * 100) + "%";
        medianStr = String.format("%,d", CollTracker.session.getMedianItems());
    }

    public static void clearLines(){
        displayedCPH = "";
        displayedGain = "";
        timeElapsedStr = "";
        efficiencyStr = "";
        medianStr = "";
    }

}
