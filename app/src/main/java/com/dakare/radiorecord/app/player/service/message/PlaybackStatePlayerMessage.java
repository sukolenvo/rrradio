package com.dakare.radiorecord.app.player.service.message;

import android.os.Bundle;
import android.os.Message;
import com.dakare.radiorecord.app.Station;
import lombok.Getter;

public class PlaybackStatePlayerMessage extends PlayerMessage
{
    private static final String STATION_KEY = "station";
    private static final String PLAYING_KEY = "playing";
    @Getter
    private final Station station;
    @Getter
    private final boolean playing;

    public PlaybackStatePlayerMessage(Station station, final boolean playing)
    {
        super(PlayerMessageType.PLAYBACK_STATE);
        this.station = station;
        this.playing = playing;
    }

    public static PlaybackStatePlayerMessage fromMessage(Bundle args)
    {
        return new PlaybackStatePlayerMessage(args.getString(STATION_KEY) == null ? null : Station.valueOf(args.getString(STATION_KEY)), args.getBoolean(PLAYING_KEY));
    }

    @Override
    public Message toMessage()
    {
        Message message = super.toMessage();
        Bundle args = new Bundle();
        args.putString(STATION_KEY, station == null ? null : station.name());
        args.putBoolean(PLAYING_KEY, playing);
        message.setData(args);
        return message;
    }
}
