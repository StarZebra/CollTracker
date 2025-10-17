package me.starzebra.colltracker.utils;

import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import me.starzebra.colltracker.CollTracker;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Objects;

public class LocationUtils {

    public static boolean isOnHypixel = false;
    public static boolean isInSkyblock = false;

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event){
        isInSkyblock = false;
        isOnHypixel = false;
    }

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent event){
        if(event.isLocal) return;
        if(CollTracker.mc.thePlayer == null) return;
        isOnHypixel = CollTracker.mc.thePlayer.getClientBrand().toLowerCase().contains("hypixel") || (CollTracker.mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel"));
    }

    @SuppressWarnings("unused")
    @Subscribe(priority = 55)
    public void onPacket(ReceivePacketEvent event){
        if(event.packet instanceof S3FPacketCustomPayload) {
            if(isOnHypixel || !Objects.equals(((S3FPacketCustomPayload) event.packet).getChannelName(), "MC|Brand")) return;
            if(((S3FPacketCustomPayload) event.packet).getBufferData().readStringFromBuffer(Short.MAX_VALUE).toLowerCase().contains("hypixel")) isOnHypixel = true;
        }
        if(event.packet instanceof S3BPacketScoreboardObjective){
            if(isInSkyblock) return;
            isInSkyblock = isOnHypixel && ((S3BPacketScoreboardObjective) event.packet).func_149339_c().equals("SBScoreboard");
        }
    }

}
