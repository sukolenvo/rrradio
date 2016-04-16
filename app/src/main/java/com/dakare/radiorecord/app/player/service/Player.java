package com.dakare.radiorecord.app.player.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.quality.Quality;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

public class Player implements PlayerCallback
{
    private static final String PREFIX = "http://air2.radiorecord.ru:805/";

    @Getter
    private final Context context;
    @Getter
    private Station station;
    @Setter
    private PlayerServiceMessageHandler playerServiceMessageHandler;
    private final MultiPlayer player = new MultiPlayer(this);
    private final Handler uiHandler = new Handler();

    public Player(final Context context)
    {
        this.context = context;
    }

    public void play(final Station station, final Quality quality)
    {
        Toast.makeText(context, R.string.connecting, Toast.LENGTH_LONG).show();
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(station.getIcon())
                .setContentTitle(station.getName())
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, PlayerActivity.class), 0))
                .setOngoing(true).getNotification();
        ((Service) context).startForeground(1, notification);
        synchronized (Player.class)
        {
            this.station = station;
            player.stop();
            player.playAsync(PREFIX + station.getCode() + "_" + quality.getSuffix(), quality.getBitrate());
        }
        playerServiceMessageHandler.handleServiceResponse(new PlaybackStatePlayerMessage(station, station != null));
    }

    public void stop()
    {
        context.stopService(new Intent(context, PlayerService.class));
        ((Service) context).stopForeground(true);
        synchronized (Player.class)
        {
            station = null;
            player.stop();
        }
    }

    @Override
    public void playerStarted()
    {
        Log.i("Player", "Started");
    }

    @Override
    public void playerPCMFeedBuffer(boolean b, int i, int i1)
    {
        //Nothing to do
    }

    @Override
    public void playerStopped(int i)
    {
        Log.i("Player", "Stopped");
        stop();
    }

    @Override
    public void playerException(final Throwable throwable)
    {
        uiHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (throwable.getMessage().contains("404"))
                {
                    Toast.makeText(context, R.string.error_stream_not_found, Toast.LENGTH_LONG).show();
                } else
                {
                    Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void playerMetadata(String s, String s1)
    {
        Log.i("Player", "Metadata: " + s + " " + s1);
    }

    @Override
    public void playerAudioTrackCreated(AudioTrack audioTrack)
    {
        Log.i("Player", "AudioTrack: " + audioTrack);
    }
}
