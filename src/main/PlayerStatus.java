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

    public final void setCurrentTrack(final String currentTrack) {
        this.currentTrack = currentTrack;
    }

    public final void setRemainedTime(final int remainedTime) {
        this.remainedTime = remainedTime;
    }

    public final void setRepeatMode(final String repeatMode) {
        this.repeatMode = repeatMode;
    }

    public void setShuffleMode(final boolean shuffleMode) {
        this.shuffleMode = shuffleMode;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
}
