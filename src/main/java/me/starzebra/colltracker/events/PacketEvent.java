package me.starzebra.colltracker.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketEvent extends Event {

    public Packet<?> packet;

    public PacketEvent(Packet<?> packet){
        this.packet = packet;
    }
}
