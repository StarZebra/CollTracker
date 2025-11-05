package me.starzebra.colltracker;

import me.starzebra.colltracker.config.SimpleConfig;
import me.starzebra.colltracker.features.StashListener;
import me.starzebra.colltracker.hud.CollectionHUD;
import me.starzebra.colltracker.statistics.StatsManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrackerSession {

    private String trackedCollection;
    private boolean isActive;
    private boolean isPaused;
    private boolean firstStashUpdate;
    private long sessionStartNanos;
    private long lastSackMessageNanos;
    private int startStashItems;
    private int totalItemsGained;
    private int trackedSeconds;
    private int totalStashGain;
    private int totalSackGain;
    private List<Integer> sackUpdatesList;
    private final int MAX_LIST_SIZE = 100;

    private long pausedNanos;
    private long resumedNanos;

    public TrackerSession(String trackedColl) {
        this.trackedCollection = trackedColl.toLowerCase();
        this.isActive = false;
        this.sessionStartNanos = 0L;
        this.lastSackMessageNanos = 0L;
        this.totalItemsGained = 0;
        this.trackedSeconds = 0;
        this.sackUpdatesList = new ArrayList<>();
        this.firstStashUpdate = false;
        this.startStashItems = 0;
        this.totalSackGain = 0;
        this.totalStashGain = 0;
    }

    public void setFirstStashUpdate(boolean bool, int startStash){
        this.firstStashUpdate = bool;
        this.startStashItems = startStash;
    }

    public boolean hasFirstStashUpdate(){
        return firstStashUpdate;
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
        printSessionStats();
        this.isActive = false;
        CollTracker.session = null;
        CollectionHUD.clearLines();
    }

    public void printSessionStats(){
        if(!SimpleConfig.debugMsgs) return;
        System.out.println("Stash started at "+ startStashItems + " items");
        System.out.println("Last known sack " + StashListener.getLastItem() + " items");
        System.out.println("Total gain "+ totalItemsGained + " items");
        System.out.println("Session start nanos " + sessionStartNanos + " nanos");
        System.out.println("Total tracked seconds " + trackedSeconds + " seconds");
        System.out.println("Total time elapsed " + getElapsedSeconds() + " seconds");
        System.out.println("Efficiency " + getEfficiency());
        System.out.println("CPH " + getCollectionPerHour());
        System.out.println("Average sack " + getMedianItems());
    }

    public void stopWithContext(String context){
        switch (context){
            case "timeout":
                if(SimpleConfig.saveStats){
                    saveSessionStats(true);
                }
                printSessionStats();
                break;
            case "dontsave":
                printSessionStats();
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

    public void increaseTotalItems(int gain, boolean stashGain){
        this.totalItemsGained += gain;
        if(stashGain){
            this.totalStashGain += gain;
        } else {
            this.totalSackGain += gain;
        }
        if(gain < 160) return;
        updateSackList(gain/160);
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

    public long getLastSackMessageNanos(){
        return lastSackMessageNanos;
    }

    public int getTotalItemsGained() {
        return totalItemsGained;
    }

    private void updateSackList(int value){
        int index = Collections.binarySearch(sackUpdatesList, value);
        if(index < 0) index = -index - 1;
        this.sackUpdatesList.add(index, value);

        if(sackUpdatesList.size() > MAX_LIST_SIZE){

            if(Math.random() > 0.5){
                sackUpdatesList.remove(sackUpdatesList.size()-1);
            }else{
                sackUpdatesList.remove(0);
            }

        }
    }

    public String getStashSackSplit(){
        StringBuilder sb = new StringBuilder();
        if(SimpleConfig.rateSplitOption){
            sb.append(this.totalStashGain)
                    .append("/")
                    .append(this.totalSackGain);
        }else{
            if(this.totalItemsGained <= 0) return "";
            float stash = (float) this.totalStashGain / this.totalItemsGained;
            float sack = (float) this.totalSackGain / this.totalItemsGained;
            sb.append(String.format("%.0f", (stash)*100))
                    .append("%/")
                    .append(String.format("%.0f", (sack)*100))
                    .append("%");
        }
        return sb.toString();
    }

    public long getElapsedSeconds(){
        if(!isActive || sessionStartNanos == 0L) return 0L;
        long now = System.nanoTime();
        return TimeUnit.NANOSECONDS.toSeconds(now - sessionStartNanos);
    }

    public float getEfficiency(){
        return Math.min((float) this.trackedSeconds / this.getElapsedSeconds(), 1);
    }

    public int getCollectionPerHour(){
        double hours = getElapsedSeconds() / 3600f;
        if(hours == 0) return 0;
        return (int) (getTotalItemsGained() / hours);
    }

    public int getMedianItems(){
        List<Integer> sortedList = sackUpdatesList;

        int middle = sortedList.size() / 2;

        if(sortedList.isEmpty()) return 0;

        return sortedList.get(middle);

    }
}
