package timeCommands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import main.Command;
import main.Main;
import main.PlayerStatus;
import main.Playlist;

public class RepeatCommand extends Command {
    public RepeatCommand() {
    }
    private int repeat;

    public final int getRepeat() {
        return repeat;
    }

    public final void setRepeat(final int repeat) {
        this.repeat = repeat;
    }

    public static void repeat(ArrayNode outputs, final PlayerStatus playerStatus,
                                    final LibraryInput library,
                                    final RepeatCommand repeatCommand,
                                    final boolean loaded) {
        int repeat = repeatCommand.getRepeat();
        if (repeat == 2 && playerStatus.getType().equals("song")) {
            SongInput song = Main.getSongDetails(library, playerStatus.getCurrentTrack());
            playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                    - repeatCommand.getTimestamp() + playerStatus.getLastTime());
            playerStatus.setLastTime(repeatCommand.getTimestamp());
            while (playerStatus.getRemainedTime() <= 0) {
                assert song != null;
                playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                        + song.getDuration());
            }
        }
        if (loaded) {
            repeat++;
            if (repeat > 2) {
                repeat = 0;
            }
            playerStatus.setRepeatMode(repeat);
            if (playerStatus.getType() != null) {
                outputs.add(RepeatCommand.createRepeatOutput(repeatCommand,
                        repeat, playerStatus));
            }
        } else {
            repeat = 3;
            outputs.add(RepeatCommand.createRepeatOutput(repeatCommand,
                    repeat, playerStatus));
            playerStatus.setRepeatMode(0);
        }
        repeatCommand.setRepeat(repeat);
    }

    /**
     *
     */
    public static ObjectNode createRepeatOutput(final RepeatCommand repeatCommand,
                                                final int repeat, final PlayerStatus player) {
        ObjectNode repeatOutput = JsonNodeFactory.instance.objectNode();
        repeatOutput.put("command", "repeat");
        repeatOutput.put("user", repeatCommand.getUsername());
        repeatOutput.put("timestamp", repeatCommand.getTimestamp());
        if (repeat == 0) {
            repeatOutput.put("message", "Repeat mode changed to no repeat.");
        }
        if (repeat == 1 && (player.getType().equals("podcast")
                || player.getType().equals("playlist"))) {
            repeatOutput.put("message", "Repeat mode changed to repeat all.");
        }
        if (repeat == 1 && player.getType().equals("song")) {
            repeatOutput.put("message", "Repeat mode changed to repeat once.");
        }
        if (repeat == 2 && player.getType().equals("song")) {
            repeatOutput.put("message", "Repeat mode changed to repeat infinite.");
        }
        if (repeat == 2 && (player.getType().equals("podcast")
                || player.getType().equals("playlist"))) {
            repeatOutput.put("message", "Repeat mode changed to repeat current song.");
        }
        if (repeat == 3) {
            repeatOutput.put("message", "Please load a source before setting the repeat status.");
        }
        return repeatOutput;
    }
}
