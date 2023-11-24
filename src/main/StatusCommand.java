package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

import java.util.ArrayList;
import java.util.List;

public class StatusCommand extends Command {
    public StatusCommand() {
    }

    private int repeat;

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public static void status(final PlayerStatus playerStatus, final StatusCommand statusCommand,
                              final LibraryInput library, final String selectedTrack,
                              final PlayerStatus back, final List<Playlist> playlists) {
        assert playerStatus.getType() != null;
        if (playerStatus.getType().equals("podcast")) {
            if (playerStatus.getRemainedTime() - statusCommand.getTimestamp()
                    + playerStatus.getLastTime() < 0) {
                PodcastInput podcast = Main.getPodcastDetails(library, selectedTrack);
                if (podcast != null) {
                    ArrayList<EpisodeInput> list = podcast.getEpisodes();
                    playerStatus.setIndex(playerStatus.getIndex() + 1);
                    int index = playerStatus.getIndex();
                    EpisodeInput episode = list.get(index);
                    playerStatus.setCurrentTrack(episode.getName());
                    back.setRemainedTime(episode.getDuration()
                            - (-playerStatus.getRemainedTime()));
                    playerStatus.setRemainedTime(back.getRemainedTime());
                }
            }
        }
        if (!playerStatus.isPaused()) {
            playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                    - statusCommand.getTimestamp() + playerStatus.getLastTime());
            if (playerStatus.getType().equals("podcast")) {
                back.setRemainedTime(playerStatus.getRemainedTime());
            }
        }
        int right = 0;
        if (playerStatus.getType().equals("playlist"))  {
            if (playerStatus.getRemainedTime() < 0) {
                Playlist playlist = Main.getPlaylistDetails(playlists, selectedTrack);
                if (playlist != null) {
                    String[] songs = playlist.getSongs();
                    if (playerStatus.getRepeatMode() == 0) {
                        int var = 0;
                        int flag = 0;
                        SongInput[] alreadyPlayed = new SongInput[songs.length];
                        int count = 0;
                        for (String i : songs) {
                            SongInput song = Main.getSongDetails(library, i);
                            assert song != null;
                            if (song.getName().equals(playerStatus.getCurrentTrack())) {
                                flag = 1;
                                var = 0;
                            }
                            if (var != 0 && flag == 1) {
                                alreadyPlayed[count] = song;
                                if (song.getDuration()
                                        + playerStatus.getRemainedTime() > 0) {
                                    right = song.getDuration();
                                    playerStatus.setCurrentTrack(song.getName());
                                    break;
                                }
                                count++;
                            }
                            var = 1;
                        }
                        if (right != 0) {
                            for (int i = 0; i < count; i++) {
                                playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                                        + alreadyPlayed[i].getDuration());
                            }
                            playerStatus.setRemainedTime(right
                                    - (-playerStatus.getRemainedTime()));
                        }
                    } else if (playerStatus.getRepeatMode() == 2) {
                        SongInput song = Main.getSongDetails(library,
                                playerStatus.getCurrentTrack());
                        if (song != null) {
                            while (playerStatus.getRemainedTime() < 0) {
                                playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                                        + song.getDuration());
                            }
                        }
                    } else if (playerStatus.getRepeatMode() == 1) {
                        int var = 0;
                        while (right == 0 && songs.length != 0) {
                            for (String i : songs) {
                                SongInput song = Main.getSongDetails(library, i);
                                if (var != 0) {
                                    assert song != null;
                                    if (song.getDuration()
                                            + playerStatus.getRemainedTime() > 0) {
                                        right = song.getDuration();
                                        playerStatus.setCurrentTrack(song.getName());
                                        break;
                                    }
                                    playerStatus.setRemainedTime(playerStatus
                                            .getRemainedTime() + song.getDuration());
                                }
                                var = 1;
                            }
                        }
                        playerStatus.setRemainedTime(right
                                - (-playerStatus.getRemainedTime()));
                    }
                }
            }
        }
        if (playerStatus.getType().equals("song")) {
            if (playerStatus.getRemainedTime() < 0) {
                SongInput song = Main.getSongDetails(library, playerStatus.getCurrentTrack());
                if (playerStatus.getRepeatMode() == 1) {
                    assert song != null;
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                            + song.getDuration());
                    playerStatus.setRepeatMode(0);
                    statusCommand.setRepeat(0);
                }
                if (playerStatus.getRepeatMode() == 2) {
                    while (playerStatus.getRemainedTime() <= 0) {
                        assert song != null;
                        playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                                + song.getDuration());
                    }
                }
            }
        }
        playerStatus.setLastTime(statusCommand.getTimestamp());
        if (playerStatus.getType().equals("podcast")
                || playerStatus.getRemainedTime() < 0) {
            if (statusCommand.getTimestamp() > playerStatus.getLastTime() + playerStatus.getRemainedTime()) {
                playerStatus.setRemainedTime(0);
                playerStatus.setCurrentTrack("");
                playerStatus.setPaused(true);
                playerStatus.setRepeatMode(0);
                playerStatus.setShuffleMode(false);
            }
        }
        if (statusCommand.getRepeat() == 3) {
            playerStatus.setRemainedTime(0);
            playerStatus.setCurrentTrack("");
            playerStatus.setPaused(true);
            playerStatus.setRepeatMode(0);
            playerStatus.setShuffleMode(false);
        }
    }

    /**
     *
     * @param status is the command
     * @param player is the current state of the player
     */
    public static ObjectNode createStatus(final StatusCommand status, final PlayerStatus player) {
        ObjectNode statusOutput = JsonNodeFactory.instance.objectNode();
        statusOutput.put("command", "status");
        statusOutput.put("user", status.getUsername());
        statusOutput.put("timestamp", status.getTimestamp());
        String repeat = null;
        if (player.getRepeatMode() == 0) {
            repeat = "No Repeat";
        }
        if (player.getRepeatMode() == 1 && (player.getType().equals("podcast")
                || player.getType().equals("playlist"))) {
            repeat = "Repeat All";
        }
        if (player.getRepeatMode() == 1 && player.getType().equals("song")) {
            repeat = "Repeat Once";
        }
        if (player.getRepeatMode() == 2 && player.getType().equals("song")) {
            repeat = "Repeat Infinite";
        }
        if (player.getRepeatMode() == 2 && (player.getType().equals("podcast")
                || player.getType().equals("playlist"))) {
            repeat = "Repeat Current Song";
        }
        statusOutput.putObject("stats")
                .put("name", player.getCurrentTrack())
                .put("remainedTime", player.getRemainedTime())
                .put("repeat", repeat)
                .put("shuffle", player.isShuffleMode())
                .put("paused", player.isPaused());

        return statusOutput;
    }
}
