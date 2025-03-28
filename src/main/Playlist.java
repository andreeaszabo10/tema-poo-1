package main;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Playlist {
    @Getter
    private String owner;
    private String playlistName;
    @Getter
    private String[] songs;
    @Getter
    private String[] songsNoShuffle;
    @Getter
    private String visibility = "public";
    @Getter
    private List<String> followers;

    public final void setVisibility(final String visibility) {
        this.visibility = visibility;
    }

    public final void setFollowers(final List<String> followers) {
        this.followers = followers;
    }

    public Playlist() {
        this.songs = new String[0];
        this.followers = new ArrayList<>();
    }

    public final void setSongsNoShuffle(final String[] songsNoShuffle) {
        this.songsNoShuffle = songsNoShuffle;
    }

    public final void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public final void setUsername(final String owner) {
        this.owner = owner;
    }

    public final void setOwner(final String owner) {
        this.owner = owner;
    }

    public final void setSongs(final String[] songs) {
        this.songs = songs;
    }

    /**
     * add song in the playlist
     */
    public final void addSong(final String song) {
        String[] newSongs = new String[songs.length + 1];
        System.arraycopy(songs, 0, newSongs, 0, songs.length);
        newSongs[songs.length] = song;
        songs = newSongs;
    }

    /**
     * remove song from playlist
     */
    public final void removeSong(final String song) {
        String[] newSongs = new String[songs.length - 1];
        int index = 0;
        for (String s : songs) {
            if (!s.equals(song)) {
                newSongs[index++] = s;
            }
        }
        songs = newSongs;
    }

    /**
     * add a folower
     */
    public final void addFollower(final String follower) {
        followers.add(follower);
    }

    /**
     * remove a follower
     */
    public final void removeFollower(final String follower) {
        followers.remove(follower);
    }

    /**
     * get number of followers for playlist
     */
    public final int getFollowersNumber() {
        if (followers == null) {
            return 0;
        }
        return followers.size();
    }

    /**
     * create new playlist if it is not already created
     */
    public static int createPlaylist(final CreatePlaylistCommand createPlaylist,
                                     final List<Playlist> playlists) {
        Playlist playlist = new Playlist();
        playlist.setPlaylistName(createPlaylist.getPlaylistName());
        playlist.setUsername(createPlaylist.getUsername());
        playlist.setOwner(createPlaylist.getUsername());
        int var = 0;
        for (Playlist play : playlists) {
            if (play.getPlaylistName().equals(playlist.getPlaylistName())) {
                var = 1;
                break;
            }
        }
        if (var == 0) {
            playlists.add(playlist);
        }
        return var;
    }
}
