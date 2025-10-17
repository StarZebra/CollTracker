package me.starzebra.colltracker.statistics;

import me.starzebra.colltracker.CollTracker;
import me.starzebra.colltracker.config.SimpleConfig;

import java.util.HashMap;
import java.util.Map;

public class StatsManager {

    private static final Map<String, Integer> singleStats = new HashMap<>();
    private static final Map<String, Map<String, Integer>> groupStats = new HashMap<>();

    // Single stats (abilitiesUsed: x)

    public static void incrementByOne(String key) {
        if(SimpleConfig.debugMsgs){
            CollTracker.LOGGER.debug("Adding 1 to {}", key);
        }
        singleStats.put(key, singleStats.getOrDefault(key, 0) + 1);
    }

    public static void incrementBy(String key, int value) {
        if(SimpleConfig.debugMsgs){
            CollTracker.LOGGER.debug("Adding '{}' to '{}'", value, key);
        }
        singleStats.put(key, singleStats.getOrDefault(key, 0) + value);
    }

    public static int get(String key) {
        return singleStats.getOrDefault(key, 0);
    }

    public static void set(String key, int value) {
        singleStats.put(key, value);
    }

    public static Map<String, Integer> getAllSingle() {
        return singleStats;
    }

    // Group stats (collections { diamond: 23232, gold: 1})

    public static void incrementGrouped(String group, String key, int amount) {
        Map<String, Integer> groupMap = groupStats.computeIfAbsent(group, k -> new HashMap<>());
        if(SimpleConfig.debugMsgs){
            CollTracker.LOGGER.debug("Adding '{}' to '{}' in group '{}'", amount, key, group);
        }
        groupMap.put(key, groupMap.getOrDefault(key, 0) + amount);
    }

    public static int getGrouped(String group, String key) {
        Map<String, Integer> groupMap = groupStats.get(group);
        if (groupMap == null) return 0;
        return groupMap.getOrDefault(key, 0);
    }

    public static Map<String, Map<String, Integer>> getAllGrouped() {
        return groupStats;
    }

    public static void replaceAll(Map<String, Integer> newStats, Map<String, Map<String, Integer>> grouped) {
        singleStats.clear();
        singleStats.putAll(newStats);
        groupStats.clear();
        groupStats.putAll(grouped);
    }

}
