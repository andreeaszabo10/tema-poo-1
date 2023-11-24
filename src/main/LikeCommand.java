package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LikeCommand extends Command {
    public LikeCommand() {
    }


    public static int like(final PlayerStatus playerStatus, final LikeCommand likeCommand,
                            final String selectedTrack,
                            final Map<String, List<String>> likedSongs) {
        int flag = 0;
        if (playerStatus.getType().equals("song")) {
            List<String> songs = getLikedSongs(likeCommand.getUsername(), likedSongs);
            for (String song : songs) {
                assert selectedTrack != null;
                if (song.equals(selectedTrack)) {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                addLikedSong(likeCommand.getUsername(), selectedTrack, likedSongs);
            } else {
                removeLikedSong(likeCommand.getUsername(), selectedTrack, likedSongs);
            }
        }
        if (playerStatus.getType().equals("playlist")) {
            List<String> songs = getLikedSongs(likeCommand.getUsername(), likedSongs);
            for (String song : songs) {
                if (playerStatus.getCurrentTrack().equals(song)) ;
                {
                    flag = 1;
                    break;
                }
            }
            if (flag == 0) {
                addLikedSong(likeCommand.getUsername(), playerStatus.getCurrentTrack(), likedSongs);
            } else {
                removeLikedSong(likeCommand.getUsername(), playerStatus.getCurrentTrack(), likedSongs);
            }
        }
        return flag;
    }

    public static void addLikedSong(final String username,
                                    final String songName,
                                    final Map<String, List<String>> likedSongs) {
        List<String> liked = likedSongs.getOrDefault(username, new ArrayList<>());
        liked.add(songName);
        likedSongs.put(username, liked);
    }

    public static List<String> getLikedSongs(final String username,
                                             final Map<String, List<String>> likedSongs) {
        return likedSongs.getOrDefault(username, new ArrayList<>());
    }

    public static void removeLikedSong(final String username,
                                       final String songName,
                                       final Map<String, List<String>> likedSongs) {
        List<String> liked = getLikedSongs(username, likedSongs);
        liked.remove(songName);
        likedSongs.put(username, liked);
    }

    /**
     *
     * @param likeCommand is the command
     * @param flag is a variable that indicates the message that should be printed
     * @param loaded is used to see if we loaded a song or not
     */
    public static ObjectNode createLikeOutput(final LikeCommand likeCommand,
                                              final int flag, final boolean loaded) {
        ObjectNode likeOutput = JsonNodeFactory.instance.objectNode();
        likeOutput.put("command", "like");
        likeOutput.put("user", likeCommand.getUsername());
        likeOutput.put("timestamp", likeCommand.getTimestamp());
        if (!loaded) {
            likeOutput.put("message", "Please load a source before liking or unliking.");
        } else if (flag == 0) {
            likeOutput.put("message", "Like registered successfully.");
        } else {
            likeOutput.put("message", "Unlike registered successfully.");
        }
        return likeOutput;
    }
}
