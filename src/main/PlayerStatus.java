package main;

import lombok.Getter;

@Getter
public class PlayerStatus {
    private String currentTrack;
    private int remainedTime;
    private int repeatMode;
    private boolean shuffleMode;
    private boolean paused;
    @Getter
    private String type;
    @Getter
    private int index;
    @Getter
    private Playlist playlist;

    public final void setPlaylist(final Playlist playlist) {
        this.playlist = playlist;
    }

    public final void setIndex(final int index) {
        this.index = index;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public PlayerStatus() {
    }

    public final void setCurrentTrack(final String currentTrack) {
        this.currentTrack = currentTrack;
    }

    public final void setRemainedTime(final int remainedTime) {
        this.remainedTime = remainedTime;
    }

    public final void setRepeatMode(final int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public void setShuffleMode(final boolean shuffleMode) {
        this.shuffleMode = shuffleMode;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }
}
