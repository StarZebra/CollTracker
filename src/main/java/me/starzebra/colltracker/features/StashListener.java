package me.starzebra.colltracker.features;

import me.starzebra.colltracker.CollTracker;
import me.starzebra.colltracker.events.PacketEvent;
import me.starzebra.colltracker.events.StashUpdateEvent;
import me.starzebra.colltracker.utils.ItemUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class StashListener {

    private boolean openedStash = false;
    private static int lastItemAmount = 0;
    private static long lastStashOpen = 0;

    public static void updateLastValues(int items) {
        lastItemAmount = items;
        lastStashOpen = System.nanoTime();
    }

    @SubscribeEvent
    public void onGuiOpen(PacketEvent event){
        if(!CollTracker.isSessionActive()) return;
        if(CollTracker.session.isPaused()) return;
        if(event.packet instanceof S2DPacketOpenWindow){
            S2DPacketOpenWindow packet = (S2DPacketOpenWindow) event.packet;
            if(packet.getWindowTitle().getUnformattedText().startsWith("View Stash")){
                openedStash = true;
            }
        }
    }

    public static int getLastItem(){
        return lastItemAmount;
    }

    public static long getLastStash(){
        return lastStashOpen;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;
        if(!CollTracker.isSessionActive()) return;
        if(CollTracker.session.isPaused()) return;
        if(openedStash){
            if(Minecraft.getMinecraft().currentScreen == null) return;
            GuiChest screen = (GuiChest) Minecraft.getMinecraft().currentScreen;
            ContainerChest containerChest = (ContainerChest) screen.inventorySlots;

            String trackedColl = CollTracker.session.getTrackedCollection().toLowerCase();

            for (Slot slot : containerChest.inventorySlots){
                String itemName = ItemUtils.getUnformattedItemName(slot.getStack());
                System.out.println("Tick tech " + itemName);
                if(itemName.toLowerCase().startsWith(trackedColl) && itemName.contains("x")){
                    String strCount = itemName.substring(itemName.indexOf("x") +1);
                    int gainAmount = Integer.parseInt(strCount.replaceAll(",", ""));
                    MinecraftForge.EVENT_BUS.post(new StashUpdateEvent(gainAmount, System.nanoTime()));
                    openedStash = false;
                    break;
                }
            }
        }
    }
}
