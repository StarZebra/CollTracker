package me.starzebra.colltracker;

import cc.polyfrost.oneconfig.events.EventManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.starzebra.colltracker.command.FetchCommand;
import me.starzebra.colltracker.command.SessionCommand;
import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.events.SecondPassedEvent;
import me.starzebra.colltracker.events.SecondTimer;
import me.starzebra.colltracker.features.SackChatListener;
import me.starzebra.colltracker.features.SkymallListener;
import me.starzebra.colltracker.features.StashListener;
import me.starzebra.colltracker.statistics.StatsHelper;
import me.starzebra.colltracker.utils.APIFetcher;
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

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Mod(modid = "colltracker", useMetadata = true)
public class CollTracker {

    public static File statsDir = new File("config/colltracker");
    public static File collectionsFile = new File(statsDir, "collections.json");
    public static final Logger LOGGER = LogManager.getLogger("CollTracker");
    public static final String COLLECTION_URL = "https://api.hypixel.net/v2/resources/skyblock/collections";

    public static SimpleConfig config;
    public static Minecraft mc;
    public static TrackerSession session;

    private static boolean shouldSaveCollections = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        StatsHelper.load();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        mc = Minecraft.getMinecraft();

        if (!collectionsFile.exists() && shouldSaveCollections) {
            mc.addScheduledTask(() -> SackChatListener.supportedCollections = APIFetcher.fetchCollections(COLLECTION_URL));

            try {
                if (collectionsFile.createNewFile()) {
                    trySaveCollectionsFile();
                }
            } catch (IOException e) {
                LOGGER.error("Failed to create file {} ts cooked", collectionsFile);
            }
        } else {
            tryLoadCollectionsFile();
        }

        config = new SimpleConfig();

        MinecraftForge.EVENT_BUS.register(new SackChatListener());
        MinecraftForge.EVENT_BUS.register(new LocationUtils());
        MinecraftForge.EVENT_BUS.register(new SecondTimer());
        MinecraftForge.EVENT_BUS.register(new SkymallListener());
        MinecraftForge.EVENT_BUS.register(new StashListener());

        MinecraftForge.EVENT_BUS.register(this);

        //OneConfig events
        EventManager.INSTANCE.register(new LocationUtils());

        //Commands
        CommandManager.register(new SessionCommand());
        CommandManager.register(new FetchCommand());

        //Save stats file on quit
        Runtime.getRuntime().addShutdownHook(new Thread(StatsHelper::save));
    }

    @SubscribeEvent
    public void onSecond(SecondPassedEvent event) {
        if (event.getTotalSeconds() % 60 == 0) {
            StatsHelper.save();
        }
        if (shouldSaveCollections && event.getTotalSeconds() % 20 == 0) {
            trySaveCollectionsFile();
        }
    }

    private void trySaveCollectionsFile() {
        Map<Integer, String> collections = SackChatListener.supportedCollections;
        if (collections.isEmpty()) return;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(collectionsFile)) {
            gson.toJson(collections, writer);
            LOGGER.info("Successfully wrote to file {}", collectionsFile);
            shouldSaveCollections = false;
        } catch (IOException e) {
            LOGGER.error("Failed to write to file {}", collectionsFile);
            LOGGER.error(e.getStackTrace());
        }
    }

    private void tryLoadCollectionsFile() {
        if (!collectionsFile.exists()) return;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileReader reader = new FileReader(collectionsFile)) {
            Map<String, String> data = gson.fromJson(reader, Map.class);
            Map<Integer, String> realData = new HashMap<>();
            if (data == null) return;
            LOGGER.info("Successfully read from file {}", collectionsFile);
            data.forEach((k, v) -> realData.put(Integer.parseInt(String.valueOf(k)), v));
            SackChatListener.supportedCollections = realData;
            shouldSaveCollections = false;
        } catch (IOException e) {
            LOGGER.error("Failed to read file {}", collectionsFile);
            LOGGER.error(e.getStackTrace());
        }
    }

    public static boolean isSessionActive() {
        return session != null && session.isActive();
    }


}