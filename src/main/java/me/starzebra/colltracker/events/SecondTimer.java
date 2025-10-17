package me.starzebra.colltracker.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class SecondTimer {
    private int tickCounter = 0;
    private long totalSeconds = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if(tickCounter >= 20) {
            tickCounter = 0;
            totalSeconds++;
            MinecraftForge.EVENT_BUS.post(new SecondPassedEvent(totalSeconds));
        }
    }

}
