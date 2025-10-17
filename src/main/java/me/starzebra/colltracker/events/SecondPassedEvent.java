package me.starzebra.colltracker.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SecondPassedEvent extends Event {

    private final long totalSeconds;

    public SecondPassedEvent(long totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public long getTotalSeconds() {
        return totalSeconds;
    }

}
