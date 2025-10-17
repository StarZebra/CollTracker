package me.starzebra.colltracker;

import cc.polyfrost.oneconfig.events.EventManager;
import me.starzebra.colltracker.command.SessionCommand;
import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.events.SecondPassedEvent;
import me.starzebra.colltracker.events.SecondTimer;
import me.starzebra.colltracker.features.SackChatListener;
import me.starzebra.colltracker.features.SkymallListener;
import me.starzebra.colltracker.statistics.StatsHelper;
import me.starzebra.colltracker.utils.LocationUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Mod(modid = "colltracker", useMetadata=true)
public class CollTracker {

    public static File statsDir = new File("config/colltracker");
    public static final Logger LOGGER = LogManager.getLogger("CollTracker");

    public static SimpleConfig config;
    public static Minecraft mc;
    public static TrackerSession session;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        StatsHelper.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        mc = Minecraft.getMinecraft();
        config = new SimpleConfig();
        MinecraftForge.EVENT_BUS.register(new SackChatListener());
        MinecraftForge.EVENT_BUS.register(new LocationUtils());
        MinecraftForge.EVENT_BUS.register(new SecondTimer());
        MinecraftForge.EVENT_BUS.register(new SkymallListener());
        MinecraftForge.EVENT_BUS.register(this);
        //OneConfig events
        EventManager.INSTANCE.register(new LocationUtils());

        //Commands
        CommandManager.register(new SessionCommand());

        //Save stats file on quit
        Runtime.getRuntime().addShutdownHook(new Thread(StatsHelper::save));
    }

    @SubscribeEvent
    public void onSecond(SecondPassedEvent event){
        if(event.getTotalSeconds() % 60 == 0){
            StatsHelper.save();
        }
    }

    public static boolean isSessionActive(){
        return session != null && session.isActive();
    }

}