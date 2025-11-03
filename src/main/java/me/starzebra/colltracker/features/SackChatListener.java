package me.starzebra.colltracker.features;

import me.starzebra.colltracker.CollTracker;
import me.starzebra.colltracker.TrackerSession;
import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.events.SecondPassedEvent;
import me.starzebra.colltracker.events.StashUpdateEvent;
import me.starzebra.colltracker.hud.CollectionHUD;
import me.starzebra.colltracker.utils.LocationUtils;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SackChatListener {

    public static Map<Integer, String> supportedCollections = new HashMap<>();


    final int ENCHANTED = 160;
    long TIMEOUT_NANOS = 60000000000L;

    private static final int REMOVED_INDEX = 3;
    private static final int GAINED_INDEX = 0;

    @SubscribeEvent
    public void onSecond(SecondPassedEvent event){
        if(!LocationUtils.isOnHypixel) return;
        if(CollTracker.isSessionActive()){
            if(CollTracker.session.isPaused()) return;

            if(!supportedCollections.get(SimpleConfig.collection).equalsIgnoreCase(CollTracker.session.getTrackedCollection())){
                CollTracker.session.stop();
                return;
            }

            if(System.nanoTime() - CollTracker.session.getLastSackMessageNanos() >= TIMEOUT_NANOS){
                CollTracker.session.stopWithContext("timeout");
                return;
            }

            CollectionHUD.timeElapsedStr =  String.format("%,d", CollTracker.session.getElapsedSeconds()) + "s"; //MAYBE: seconds to mm:ss when s >= 60 and then to hh:mm:ss when m >= 60

        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        if(CollTracker.isSessionActive()){
            CollTracker.session.stop();
        }
    }

    @SubscribeEvent
    public void onStashUpdate(StashUpdateEvent event){
        if(!CollTracker.isSessionActive()) return;
        if(CollTracker.session.isPaused()) return;

        if(event.items == StashListener.getLastItem()) return;
        int gain = event.items - StashListener.getLastItem();
        if(gain > 0){
            StashListener.updateLastValues(event.items);
            if(!CollTracker.session.hasFirstStashUpdate()){
                CollTracker.session.setFirstStashUpdate(true);
                return;
            }
            CollTracker.session.increaseTotalItems(gain);
            CollectionHUD.updateLines();
        }else{
            System.out.println("ILLEGAL GAIN VALUE: "+gain);
        }

    }

    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent event) {
        if(!LocationUtils.isInSkyblock) return;
        byte type = event.type;
        if (type == 2 || type == 1) return;
        IChatComponent message = event.message;
        if(!SimpleConfig.colltracker) return;

        if (isSackMessage(message)) {
            int timeIndex = message.getUnformattedText().indexOf("Last");
            String str = message.getUnformattedText().substring(timeIndex, message.getUnformattedText().lastIndexOf("s"));
            int seconds = Integer.parseInt(str.split("t")[1].trim());

            String trackedColl = supportedCollections.get(SimpleConfig.collection);

            int removedAmount = getAmountFromMessage(message, trackedColl, REMOVED_INDEX);

            int gainAmount = getAmountFromMessage(message, trackedColl, GAINED_INDEX);

            int diff = gainAmount-removedAmount;

            if(diff == 0){
                CollTracker.LOGGER.info("Caught prohibited action, ignoring values.");
                return;
            }else if(diff > 0){
                gainAmount = diff;
            }else{
                CollTracker.LOGGER.warn("Erm wtflip why is it negative?");
                return;
            }

            if(!CollTracker.isSessionActive()){
                CollTracker.session = new TrackerSession(trackedColl);
                CollTracker.session.start(seconds);
            }

            if(CollTracker.session.isPaused()) return;

            CollTracker.session.increaseTrackedSeconds(seconds);
            CollTracker.session.increaseTotalItems(gainAmount);
            CollTracker.session.updateSackMessage();

            CollectionHUD.updateLines();

        }
    }

    private boolean isSackMessage(IChatComponent message){
        return message.getUnformattedTextForChat().equals("[Sacks] ");
    }

    private int getAmountFromMessage(IChatComponent message, String trackedColl, int index) {
        int totalAmount = 0;
        ChatStyle style = message.getSiblings().get(index).getChatStyle();

        // Fix for when there is no removed amount in sack message
        if(style.getChatHoverEvent() == null) return 0;
        // Fix for when only removed amount in sack message
        if(index == 0 && style.getFormattingCode().equals("§c")) return 0;

        List<IChatComponent> siblings = style.getChatHoverEvent().getValue().getSiblings();
        for (int i = 0; i < siblings.size(); i++) {
            IChatComponent sibling = siblings.get(i);
            if (sibling.getUnformattedText().toLowerCase().contains(trackedColl.toLowerCase())) {
                int amount = Integer.parseInt(siblings.get(i - 1).getUnformattedText().trim().replaceAll("[,\\-]", ""));
                String item = sibling.getUnformattedText();
                if (item.startsWith("Enchanted ")) {
                    amount *= ENCHANTED;
                    if(item.endsWith(" Block")){
                        amount *= ENCHANTED;
                    }
                }
                totalAmount += amount;
            }
        }
        return totalAmount;
    }

}
