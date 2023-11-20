package main;

import lombok.Getter;

@Getter
public class PlayerStatus {
    private String currentTrack;
    private int remainedTime;
    private String repeatMode;
    private boolean shuffleMode;
    private boolean paused;

    public PlayerStatus() {
    }

    public PlayerStatus(String currentTrack, int remainedTime, String repeatMode, boolean shuffleMode, boolean paused) {
        this.currentTrack = currentTrack;
        this.remainedTime = remainedTime;
        this.repeatMode = repeatMode;
        this.shuffleMode = shuffleMode;
        this.paused = paused;
    }

    public void setCurrentTrack(String currentTrack) {
        this.currentTrack = currentTrack;
    }

    public void setRemainedTime(int remainedTime) {
        this.remainedTime = remainedTime;
    }

    public void setRepeatMode(String repeatMode) {
        this.repeatMode = repeatMode;
    }

    public void setShuffleMode(boolean shuffleMode) {
        this.shuffleMode = shuffleMode;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
