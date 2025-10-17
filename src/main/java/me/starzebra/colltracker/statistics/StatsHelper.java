package me.starzebra.colltracker.statistics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.starzebra.colltracker.CollTracker;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatsHelper {
    private static final File statsFile = new File(CollTracker.statsDir, "stats.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static class StatsData {
        Map<String, Integer> single = new HashMap<>();
        Map<String, Map<String, Integer>> group = new HashMap<>();
    }

    public static void save(){
        try {
            statsFile.getParentFile().mkdirs();
            StatsData data = new StatsData();
            data.single = StatsManager.getAllSingle();
            data.group = StatsManager.getAllGrouped();
            try (FileWriter writer = new FileWriter(statsFile)){
                gson.toJson(data, writer);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void load() {
        if(!statsFile.exists()) return;
        try (FileReader reader = new FileReader(statsFile)){
            StatsData data = gson.fromJson(reader, StatsData.class);
            if(data != null){
                StatsManager.replaceAll(data.single, data.group);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
