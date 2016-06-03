package com.dakare.radiorecord.app.player.service.playback;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.widget.Toast;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.MetadataLoader;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerServiceMessageHandler;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PositionStateMessage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerImpl implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MetadataLoader.MetadataChangeCallback, Player
{

    @Getter
    private final Context context;
    @Getter
    private ArrayList<PlaylistItem> playlist;
    @Getter
    private int position;
    @Setter
    private PlayerServiceMessageHandler playerServiceMessageHandler;
    private final MediaPlayer mediaPlayer;
    private final Handler uiHandler = new Handler();
    private final MetadataLoader metadataLoader;
    private PlayerState state = PlayerState.STOP;

    public PlayerImpl(final Context context)
    {
        this.context = context;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);
        mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        metadataLoader = new MetadataLoader(this, context);
        playlist = new ArrayList<>();
        playlist.addAll(PreferenceManager.getInstance(context).getLastPlaylist());
    }

    public void play(final ArrayList<PlaylistItem> playlist, final int position)
    {
        Toast.makeText(context, R.string.connecting, Toast.LENGTH_LONG).show();
        this.playlist = playlist;
        this.position = position;
        startPlayback();
        if (playlist.get(position).isLive())
        {
            metadataLoader.start(playlist.get(position).getStation());
        } else
        {
            metadataLoader.stop();
        }
        updateState();
    }

    public void updateState()
    {
        playerServiceMessageHandler.handleServiceResponse(new PlaybackStatePlayerMessage(playlist, position, state, metadataLoader.getResponse()));
    }

    private void startPlayback()
    {
        if (playlist != null && !playlist.isEmpty())
        {
            mediaPlayer.reset();
            try
            {
                PlaylistItem playlistItem = playlist.get(position);
                mediaPlayer.setDataSource(playlistItem.getUrl());
                mediaPlayer.prepareAsync();
                state = PlayerState.PLAY;
            } catch (IOException e)
            {
                Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void next()
    {
        if (playlist != null && !playlist.isEmpty())
        {
            position = (position + 1) % playlist.size();
            startPlayback();
            if (playlist.get(position).isLive())
            {
                metadataLoader.start(playlist.get(position).getStation());
            } else
            {
                metadataLoader.stop();
            }
        }
        updateState();
    }

    public void previous()
    {
        if (playlist != null && !playlist.isEmpty())
        {
            position = (position - 1 + playlist.size()) % playlist.size();
            startPlayback();
            if (playlist.get(position).isLive())
            {
                metadataLoader.start(playlist.get(position).getStation());
            } else
            {
                metadataLoader.stop();
            }
        }
        updateState();
    }

    public void stop()
    {
        context.stopService(new Intent(context, PlayerService.class));
        mediaPlayer.stop();
        mediaPlayer.reset();
        metadataLoader.stop();
        state = PlayerState.STOP;
        updateState();
    }

    public void pause()
    {
        if (state == PlayerState.PLAY)
        {
            state = PlayerState.PAUSE;
            mediaPlayer.pause();
        }
        updateState();
    }

    public void resume()
    {
        if (state != PlayerState.STOP)
        {
            state = PlayerState.PLAY;
            mediaPlayer.start();
        }
        updateState();
    }

    public void updatePosition()
    {
        if (mediaPlayer.isPlaying())
        {
            playerServiceMessageHandler.handleServiceResponse(new PositionStateMessage(mediaPlayer.getCurrentPosition(), mediaPlayer.getDuration()));
        } else
        {
            playerServiceMessageHandler.handleServiceResponse(new PositionStateMessage(0, 0));
        }
    }

    @Override
    public void onPrepared(final MediaPlayer mp)
    {
        mp.start();
        updateState();
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra)
    {
        uiHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show();
            }
        });
        stop();
        return true;
    }

    @Override
    public void onCompletion(final MediaPlayer mp)
    {
        if (playlist != null && position < playlist.size() - 1)
        {
            position++;
            startPlayback();
        }
    }

    @Override
    public void onMetadataChanged()
    {
        updateState();
    }
}
