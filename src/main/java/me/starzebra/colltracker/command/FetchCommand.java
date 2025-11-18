package me.starzebra.colltracker.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import me.starzebra.colltracker.CollTracker;
import me.starzebra.colltracker.features.SackChatListener;
import me.starzebra.colltracker.utils.APIFetcher;
import net.minecraft.util.ChatComponentText;

import java.util.Map;

@Command(value = "cttryfetchcollections")
public class FetchCommand {

    @Main
    private void Main(){

        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§eFetching collection list from hypixel api."));

        Map<Integer, String> collections = APIFetcher.fetchCollections(CollTracker.COLLECTION_URL);

        if(collections.isEmpty()){
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cCollections returned empty, maybe hypixel api is down. Wait a couple of minutes or contact developer."));
            return;
        }

        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§aSuccessfully fetched collections from api."));

        SackChatListener.supportedCollections = collections;

    }

}
