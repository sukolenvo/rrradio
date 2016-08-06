package com.dakare.radiorecord.app.player.service.message;

import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.UpdateResponse;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerState;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PlaybackStatePlayerMessage extends PlayerMessage {
    private static final String PLAYLIST_KEY = "playlist";
    private static final String POSITION_KEY = "position";
    private static final String PLAYING_KEY = "playing";
    private static final String ICON_KEY = "icon";
    private static final String ARTIST_KEY = "artist";
    private static final String SONG_KEY = "song";

    private final String icon;
    private final String artist;
    private final String song;
    private final ArrayList<PlaylistItem> items;
    private final int position;
    private final PlayerState state;

    public PlaybackStatePlayerMessage(final ArrayList<PlaylistItem> items, final int position, final PlayerState state,
                                      final UpdateResponse updateResponse) {
        super(PlayerMessageType.PLAYBACK_STATE);
        this.items = items;
        this.position = position;
        this.state = state;
        this.icon = updateResponse.getImage600();
        this.artist = updateResponse.getArtist();
        this.song = updateResponse.getTitle();
    }

    @SuppressWarnings("unchecked")
    public PlaybackStatePlayerMessage(final Bundle data) {
        super(PlayerMessageType.PLAYBACK_STATE);
        this.icon = data.getString(ICON_KEY);
        this.artist = data.getString(ARTIST_KEY);
        this.song = data.getString(SONG_KEY);
        this.items = (ArrayList) data.getParcelableArrayList(PLAYLIST_KEY);
        this.position = data.getInt(POSITION_KEY);
        this.state = PlayerState.valueOf(data.getString(PLAYING_KEY));
    }

    public static PlaybackStatePlayerMessage fromMessage(final Bundle args) {
        return new PlaybackStatePlayerMessage(args);
    }

    @Override
    public Message toMessage() {
        Message message = super.toMessage();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PLAYLIST_KEY, items);
        args.putInt(POSITION_KEY, position);
        args.putString(PLAYING_KEY, state.name());
        args.putString(ICON_KEY, icon);
        args.putString(ARTIST_KEY, artist);
        args.putString(SONG_KEY, song);
        message.setData(args);
        return message;
    }
}
