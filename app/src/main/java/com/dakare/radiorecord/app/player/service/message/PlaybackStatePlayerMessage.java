package com.dakare.radiorecord.app.player.service.message;

import android.os.Bundle;
import android.os.Message;
import com.dakare.radiorecord.app.player.UpdateResponse;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.PlayerState;
import lombok.Getter;

@Getter
public class PlaybackStatePlayerMessage extends PlayerMessage {

    private static final String PLAYING_KEY = "playing";
    private static final String POSITION_KEY = "position";
    private static final String STATE_KEY = "state";
    private static final String ICON_KEY = "icon";
    private static final String ARTIST_KEY = "artist";
    private static final String SONG_KEY = "song";

    private final String icon;
    private final String artist;
    private final String song;
    private final PlaylistItem playing;
    private final int position;
    private final PlayerState state;

    public PlaybackStatePlayerMessage(final PlaylistItem playing, final int position, final PlayerState state,
                                      final UpdateResponse updateResponse) {
        super(PlayerMessageType.PLAYBACK_STATE);
        this.playing = playing;
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
        this.playing = data.getParcelable(PLAYING_KEY);
        this.position = data.getInt(POSITION_KEY);
        this.state = PlayerState.valueOf(data.getString(STATE_KEY));
    }

    public static PlaybackStatePlayerMessage fromMessage(final Bundle args) {
        return new PlaybackStatePlayerMessage(args);
    }

    @Override
    public Message toMessage() {
        Message message = super.toMessage();
        Bundle args = new Bundle();
        args.putParcelable(PLAYING_KEY, playing);
        args.putInt(POSITION_KEY, position);
        args.putString(STATE_KEY, state.name());
        args.putString(ICON_KEY, icon);
        args.putString(ARTIST_KEY, artist);
        args.putString(SONG_KEY, song);
        message.setData(args);
        return message;
    }
}
