package me.starzebra.colltracker.features;

import me.starzebra.colltracker.CollTracker;
import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.statistics.StatsManager;
import me.starzebra.colltracker.utils.LocationUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkymallListener {

    Pattern skymallPattern = Pattern.compile("New buff: (.*).");

    @SubscribeEvent
    public void onReceiveChat(ClientChatReceivedEvent event){
        if(!LocationUtils.isInSkyblock) return;
        byte type = event.type;
        if (type == 2 || type == 1) return;
        if(!CollTracker.isSessionActive()) return;
        if(!SimpleConfig.saveStats) return;
        String message = event.message.getUnformattedText();
        Matcher matcher = skymallPattern.matcher(message);
        if(matcher.matches()){
            String perk = matcher.group(1);
            switch (perk){
                case "-20% Pickaxe Ability cooldowns":
                    StatsManager.incrementGrouped("skymall", "cdreduc", 1);
                    break;
                case "Gain +100⸕ Mining Speed":
                    StatsManager.incrementGrouped("skymall", "miningspeed", 1);
                    break;
                case "10x chance to find Golden and Diamond Goblins":
                    StatsManager.incrementGrouped("skymall", "10xgoblin", 1);
                    break;
                case "Gain +15% more Powder while mining":
                    StatsManager.incrementGrouped("skymall", "powderbuff", 1);
                    break;
                case "Gain +50☘ Mining Fortune":
                    StatsManager.incrementGrouped("skymall", "miningfort", 1);
                    break;
                case "Gain 5x Titanium drops":
                    StatsManager.incrementGrouped("skymall", "5xtitanium", 1);
                    break;
                default:
                    CollTracker.LOGGER.info("Unknown skymall event: {}", perk);
            }

        }

    }
}
