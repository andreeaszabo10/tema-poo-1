package time;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.SongInput;
import main.Command;
import main.Main;
import main.PlayerStatus;

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

    private static final int VAR = 3;
    /**
     * if it is a song and repeat is 1, add the song duration once, if it is 2
     * add it until the remaining time is positive
     * if it is a playlist and repeat is one start again after the last song
     * if repeat is 2, make an infinite loop
     */
    public static void repeat(final ArrayNode outputs, final PlayerStatus playerStatus,
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
            repeat = VAR;
            outputs.add(RepeatCommand.createRepeatOutput(repeatCommand,
                    repeat, playerStatus));
            playerStatus.setRepeatMode(0);
        }
        repeatCommand.setRepeat(repeat);
    }


    /**
     * create the output for the command
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
        if (repeat == VAR) {
            repeatOutput.put("message", "Please load a source before setting the repeat status.");
        }
        return repeatOutput;
    }
}
