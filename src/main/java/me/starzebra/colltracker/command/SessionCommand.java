package me.starzebra.colltracker.command;

import cc.polyfrost.oneconfig.utils.commands.annotations.Command;
import cc.polyfrost.oneconfig.utils.commands.annotations.Greedy;
import cc.polyfrost.oneconfig.utils.commands.annotations.Main;
import cc.polyfrost.oneconfig.utils.commands.annotations.SubCommand;
import me.starzebra.colltracker.CollTracker;
import me.starzebra.colltracker.TrackerSession;
import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.features.SackChatListener;
import net.minecraft.util.ChatComponentText;

import java.util.Map;

@SuppressWarnings("unused")
@Command(value = "tsession", aliases = {"ts"})
public class SessionCommand {

    @Main
    private void Main(){
        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§e/tsession start"));
        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§e/tsession stop"));
        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§e/tsession pause"));
        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§e/tsession resume"));
        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§e/tsession stopdontsave"));
        CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§e/tsession startset [collection]"));
    }

    @SubCommand
    private void start(){
        if(!CollTracker.isSessionActive()){
            CollTracker.session = new TrackerSession(SackChatListener.supportedCollections.get(SimpleConfig.collection));
            CollTracker.session.start();
        }else{
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cA session is already active, to stop the active session run '/ts stop'"));
        }
    }

    @SubCommand
    private void startset(@Greedy String arg){
        if(!CollTracker.isSessionActive()){
            Map<Integer, String> colls = SackChatListener.supportedCollections;
            if(colls.isEmpty()) {
                CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cCollection list is empty, do '/cttryfetchcollections' and try again."));
                return;
            }
            int i = 0;
            for (String coll : colls.values()){
                if(coll.toLowerCase().startsWith(arg.toLowerCase())){
                    CollTracker.session = new TrackerSession(colls.get(i));
                    SimpleConfig.collection = i;
                    CollTracker.session.start();
                    CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§eStarted new session tracking '"+coll+"'"));
                    return;
                }
                i++;
            }
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cUnsupported collection '" + arg + "'"));

        }else{
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cA session is already active, to stop the active session run '/ts stop'"));
        }
    }

    @SubCommand
    private void stop(){
        if(CollTracker.isSessionActive()){
            CollTracker.session.stop();
        }else{
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cThere is currently no active session, to start one run '/ts start'"));
        }
    }

    @SubCommand
    private void stopdontsave(){
        if(CollTracker.isSessionActive()){
            CollTracker.session.stopWithContext("dontsave");
        }else{
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cThere is currently no active session, to start one run '/ts start'"));
        }
    }

    @SubCommand
    private void pause(){
        if(CollTracker.isSessionActive() && !CollTracker.session.isPaused()){
            CollTracker.session.pause();
        }else{
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cThere is currently no active session or the session is already paused!"));
        }
    }

    @SubCommand
    private void resume(){
        if(CollTracker.isSessionActive() && CollTracker.session.isPaused()){
            CollTracker.session.resume();
        }else{
            CollTracker.mc.thePlayer.addChatMessage(new ChatComponentText("§cThe current session is not paused, to pause run '/ts pause'"));
        }
    }



}
