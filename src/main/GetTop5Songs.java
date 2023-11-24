package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import fileio.input.UserInput;

import java.util.List;
import java.util.Map;

public class GetTop5Songs extends Command {
    public GetTop5Songs() {
    }

    private static final int MAX = 4;

    public static SongInput[] top5Songs(final LibraryInput library,
                                        final Map<String, List<String>> likedSongs) {
        int index = -1;
        SongInput[] songs = new SongInput[library.getSongs().size()];
        int[] likes = new int[library.getSongs().size()];
        for (SongInput song : library.getSongs()) {
            index++;
            songs[index] = song;
            int count = 0;
            for (UserInput user : library.getUsers()) {
                List<String> liked = LikeCommand
                        .getLikedSongs(user.getUsername(), likedSongs);
                for (String is : liked) {
                    if (is.equals(song.getName())) {
                        count++;
                    }
                }
            }
            likes[index] = count;
        }
        for (int i = 0; i < songs.length - 1; i++) {
            for (int j = 0; j < songs.length - i - 1; j++) {
                if (likes[j] < likes[j + 1]) {
                    int aux = likes[j];
                    likes[j] = likes[j + 1];
                    likes[j + 1] = aux;
                    SongInput aux1 = songs[j];
                    songs[j] = songs[j + 1];
                    songs[j + 1] = aux1;
                }
            }
        }
        return songs;
    }

    /**
     *
     */
    public static ObjectNode createTop5Output(final GetTop5Songs getTop5Songs,
                                              final SongInput[] songs) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "getTop5Songs");
        output.put("timestamp", getTop5Songs.getTimestamp());
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        int count = 0;
        for (SongInput song : songs) {
            if (count <= MAX) {
                resultArray.add(song.getName());
            }
            count++;
        }
        output.set("result", resultArray);
        return output;
    }
}
