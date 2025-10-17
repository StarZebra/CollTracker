package me.starzebra.colltracker;

import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.hud.CollectionHUD;
import me.starzebra.colltracker.statistics.StatsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TrackerSession {

    String trackedCollection;
    boolean isActive;
    boolean isPaused;
    long sessionStartNanos;
    long lastSackMessageNanos;
    int totalItemsGained;
    int trackedSeconds;
    List<Integer> sackUpdatesList;

    long pausedNanos;
    long resumedNanos;

    public TrackerSession(String trackedColl) {
        this.trackedCollection = trackedColl.toLowerCase();
        this.isActive = false;
        this.sessionStartNanos = 0L;
        this.lastSackMessageNanos = 0L;
        this.totalItemsGained = 0;
        this.trackedSeconds = 0;
        this.sackUpdatesList = new ArrayList<>();
    }

    public void start(){
        this.isActive = true;
        long now = System.nanoTime();
        this.sessionStartNanos = now;
        this.lastSackMessageNanos = now;
        this.totalItemsGained = 0;
        this.trackedSeconds = 0;
    }

    public void start(int addedSeconds){
        this.isActive = true;
        long now = System.nanoTime();
        this.sessionStartNanos = now - TimeUnit.NANOSECONDS.convert(addedSeconds, TimeUnit.SECONDS);
        this.lastSackMessageNanos = now;
        this.totalItemsGained = 0;
        this.trackedSeconds = 0;
    }

    public void pause(){
        this.isPaused = true;
        this.pausedNanos = System.nanoTime();
    }

    public void resume(){
        this.isPaused = false;
        this.resumedNanos = System.nanoTime();
        long nanoDiff = this.resumedNanos-this.pausedNanos;
        this.sessionStartNanos = sessionStartNanos+nanoDiff;
        this.lastSackMessageNanos = lastSackMessageNanos+nanoDiff;
    }

    public void stop(){
        if(SimpleConfig.saveStats){
            saveSessionStats(false);
        }
        this.isActive = false;
        CollTracker.session = null;
        CollectionHUD.clearLines();
    }

    public void stopWithContext(String context){
        switch (context){
            case "timeout":
                if(SimpleConfig.saveStats){
                    saveSessionStats(true);
                }
                break;
            case "dontsave":
                break;
            default:
                CollTracker.LOGGER.info("Stop context '{}' not handled, contact developer to fix.", context);
        }
        this.isActive = false;
        CollTracker.session = null;
        CollectionHUD.clearLines();
    }

    private void saveSessionStats(boolean timeout){
        StatsManager.incrementGrouped("collections", this.trackedCollection, this.totalItemsGained);
        StatsManager.incrementBy("timeSpentMining", trackedSeconds);
        if(timeout){
            StatsManager.incrementBy("totalSessionTime", (int) (getElapsedSeconds() - 60));
        }else{
            StatsManager.incrementBy("totalSessionTime", (int) getElapsedSeconds());
        }

    }

    public void increaseTotalItems(int gain){
        this.totalItemsGained += gain;
    }

    public void increaseTrackedSeconds(int gain){
        this.trackedSeconds += gain;
    }

    public void updateSackMessage(){
        this.lastSackMessageNanos = System.nanoTime();
    }

    public String getTrackedCollection(){
        return trackedCollection;
    }

    public boolean isActive(){
        return isActive;
    }

    public boolean isPaused(){
        return isPaused;
    }

    public long getStartNanos(){
        return sessionStartNanos;
    }

    public long getLastSackMessageNanos(){
        return lastSackMessageNanos;
    }

    public int getTotalItemsGained() {
        return totalItemsGained;
    }

    public int getTrackedSeconds(){
        return trackedSeconds;
    }

    public long getElapsedSeconds(){
        if(!isActive || sessionStartNanos == 0L) return 0L;
        long now = System.nanoTime();
        return TimeUnit.NANOSECONDS.toSeconds(now - sessionStartNanos);
    }

    public float getEfficiency(){
        return (float) this.trackedSeconds / this.getElapsedSeconds();
    }

    public int getCollectionPerHour(){
        double hours = getElapsedSeconds() / 3600f;
        if(hours == 0) return 0;
        return (int) (getTotalItemsGained() / hours);
    }

    public int getMedianItems(){

        //NEEDS TESTING
        System.out.println("Unsorted: " + sackUpdatesList);
        List<Integer> sortedList = sackUpdatesList.stream().sorted().collect(Collectors.toList());
        System.out.println("Sorted: " + sortedList);

        int middle = sortedList.size() / 2;

        return sortedList.get(middle);

    }
}
