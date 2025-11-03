package me.starzebra.colltracker.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class StashUpdateEvent extends Event {

    public int items;
    public long nanoTime;

    public StashUpdateEvent(int gain, long nanos){
        this.items = gain;
        this.nanoTime = nanos;
    }
}
