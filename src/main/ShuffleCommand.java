package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ShuffleCommand extends Command {
    private int seed;
    public ShuffleCommand() {
    }

    @Getter
    private String[] songsShuffled;
    @Getter
    private String[] songsNoShuffle;
    @Getter
    private boolean loaded;

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public final void setSongsShuffled(final String[] songsShuffled) {
        this.songsShuffled = songsShuffled;
    }

    public final void setSongsNoShuffle(final String[] songsNoShuffle) {
        this.songsNoShuffle = songsNoShuffle;
    }

    @Override
    public final int getSeed() {
        return seed;
    }

    @Override
    public final void setSeed(final int seed) {
        this.seed = seed;
    }

    public static void shuffle(final ShuffleCommand shuffleCommand, final PlayerStatus playerStatus,
                               final String selectedTrack, final List<Playlist> playlists) {
        int seed = shuffleCommand.getSeed();
        String lastSong = null;
        if (shuffleCommand.getSongsShuffled() != null && shuffleCommand.getSongsShuffled().length != 0) {
            lastSong = shuffleCommand.getSongsShuffled()[shuffleCommand.getSongsShuffled().length - 1];
        }
        if (shuffleCommand.isLoaded() && playerStatus.getType().equals("playlist")) {
            playerStatus.setShuffleMode(!playerStatus.isShuffleMode());
        }
        if (lastSong != null && lastSong.equals(playerStatus.getCurrentTrack())
                && (playerStatus.getRemainedTime()
                - shuffleCommand.getTimestamp() + playerStatus.getLastTime()) < 0) {
            playerStatus.setShuffleMode(!playerStatus.isShuffleMode());
            shuffleCommand.setLoaded(false);
            playerStatus.setRemainedTime(0);
            playerStatus.setCurrentTrack("");
            playerStatus.setPaused(true);
            playerStatus.setRepeatMode(0);
            playerStatus.setShuffleMode(false);
        }
        assert playerStatus.getType() != null;
        if (playerStatus.getType().equals("playlist") && playerStatus.isShuffleMode()) {
            Playlist playlist = Main.getPlaylistDetails(playlists, selectedTrack);
            if (playlist != null) {
                shuffleCommand.setSongsNoShuffle(playlist.getSongs());
            }
        }
        if (playerStatus.getType().equals("playlist")) {
            Playlist playlist = Main.getPlaylistDetails(playlists, selectedTrack);
            if (playlist != null) {
                String[] songsBefore = playlist.getSongs();
                String[] songs = new String[songsBefore.length];
                if (playerStatus.isShuffleMode()) {
                    List<Integer> idx = new java.util.ArrayList<>(songsBefore.length);
                    List<Integer> shuffled = new java.util.ArrayList<>(songsBefore.length);
                    for (int i = 0; i < songsBefore.length; i++) {
                        idx.add(i);
                        shuffled.add(i);
                    }
                    Collections.shuffle(shuffled, new Random(seed));
                    for (int i = 0; i < songsBefore.length; i++) {
                        for (int j = 0; j < songsBefore.length; j++) {
                            if (Objects.equals(idx.get(i), shuffled.get(j))) {
                                songs[j] = songsBefore[i];
                            }
                        }
                    }
                    shuffleCommand.setSongsShuffled(songs);
                } else {
                    songs = shuffleCommand.getSongsNoShuffle();
                    shuffleCommand.setSongsShuffled(null);
                }
                playlist.setSongs(songs);
            }
        }
    }

    /**
     *
     */
    public static ObjectNode createShuffleOutput(final ShuffleCommand shuffleCommand,
                                                 final PlayerStatus player, final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "shuffle");
        output.put("user", shuffleCommand.getUsername());
        output.put("timestamp", shuffleCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please load a source before using the shuffle function.");
        } else if (!player.getType().equals("playlist")) {
            output.put("message", "The loaded source is not a playlist.");
        } else if (player.isShuffleMode()) {
            output.put("message", "Shuffle function activated successfully.");
        } else {
            output.put("message", "Shuffle function deactivated successfully.");
        }
        return output;
    }
}
