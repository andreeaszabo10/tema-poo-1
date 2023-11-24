package playlistCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import main.Command;
import main.Main;
import main.Playlist;

import java.util.List;

public class AddRemoveCommand extends Command {
    private int playlistId;

    public AddRemoveCommand() {
    }

    @Override
    public final int getPlaylistId() {
        return playlistId;
    }

    @Override
    public final void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }

    /**
     *
     */
    public static int addRemove(final AddRemoveCommand addRemoveCommand,
                                        final List<Playlist> playlists,
                                        final String song, final LibraryInput library) {
        if (addRemoveCommand.getPlaylistId() > playlists.size()) {
            return -1;
        }
        Playlist playlist = Main.findPlaylist(playlists, addRemoveCommand.getUsername(),
                addRemoveCommand.getPlaylistId());
        int ok = 1;
        if (playlist.getSongs() != null) {
            for (String song1 : playlist.getSongs()) {
                if (song1.equals(song)) {
                    ok = 0;
                    break;
                }
            }
        }
        int ok1 = 0;
        for (SongInput music : library.getSongs()) {
            if (music.getName().equals(song)) {
                ok1 = 1;
                break;
            }
        }
        if (ok1 == 0) {
            return 2;
        }
        if (ok == 1) {
            playlist.addSong(song);
            return 1;
        }
        playlist.removeSong(song);
        return 0;
    }

    /**
     *
     * @param addRemove is the command
     * @param x is a variable that indicates the message that should be printed
     */
    public static ObjectNode createAddRemoveOutput(final AddRemoveCommand addRemove, final int x) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "addRemoveInPlaylist");
        output.put("user", addRemove.getUsername());
        output.put("timestamp", addRemove.getTimestamp());

        if (x == 1) {
            output.put("message", "Successfully added to playlist.");
        } else if (x == 0) {
            output.put("message", "Successfully removed from playlist.");
        } else if (x == -1) {
            output.put("message", "The specified playlist does not exist.");
        } else if (x == 2) {
            output.put("message", "The loaded source is not a song.");
        } else {
            output.put("message", "Please load a source "
                        + "before adding to or removing from the playlist.");
        }
        return output;
    }
}
