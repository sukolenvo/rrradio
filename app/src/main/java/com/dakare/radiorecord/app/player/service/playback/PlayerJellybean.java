package com.dakare.radiorecord.app.player.service.playback;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Handler;
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
import com.google.android.exoplayer.*;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerJellybean implements MetadataLoader.MetadataChangeCallback, ExoPlayer.Listener, ExtractorSampleSource.EventListener, MediaCodecAudioTrackRenderer.EventListener, Player
{
    private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;

    @Getter
    private final Context context;
    @Getter
    private ArrayList<PlaylistItem> playlist;
    @Getter
    private int position;
    @Setter
    private PlayerServiceMessageHandler playerServiceMessageHandler;
    private final Handler uiHandler = new Handler();
    private final MetadataLoader metadataLoader;
    private PlayerState state = PlayerState.STOP;
    private final ExoPlayer player;
    private long lastErrorMessage;

    public PlayerJellybean(final Context context)
    {
        this.context = context;
        player = ExoPlayer.Factory.newInstance(1);
        player.addListener(this);
        metadataLoader = new MetadataLoader(this, context);
        playlist = new ArrayList<>();
        playlist.addAll(PreferenceManager.getInstance(context).getLastPlaylist());
    }

    public void play(final ArrayList<PlaylistItem> playlist, final int position)
    {
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
            player.stop();
            player.seekTo(0L);
            player.setPlayWhenReady(true);
            PlaylistItem playlistItem = playlist.get(position);
            Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
            DataSource dataSource = new PartialHttpDataSource("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36", null);
            ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(playlistItem.getUrl()), dataSource, allocator,
                    BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE, uiHandler, this, 0);
            MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                    MediaCodecSelector.DEFAULT, null, true, uiHandler, this, AudioCapabilities.getCapabilities(context), AudioManager.STREAM_MUSIC);
            player.prepare(audioRenderer);
            state = PlayerState.PLAY;
            updateState();
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
        player.stop();
        player.seekTo(0L);
        metadataLoader.stop();
        state = PlayerState.STOP;
        updateState();
    }

    public void pause()
    {
        if (state == PlayerState.PLAY)
        {
            state = PlayerState.PAUSE;
            player.setPlayWhenReady(false);
        }
        updateState();
    }

    public void resume()
    {
        if (state != PlayerState.STOP)
        {
            state = PlayerState.PLAY;
            player.setPlayWhenReady(true);
        }
        updateState();
    }

    public void updatePosition()
    {
        playerServiceMessageHandler.handleServiceResponse(new PositionStateMessage((int) player.getCurrentPosition(), (int) player.getDuration()));
    }

    @Override
    public void onMetadataChanged()
    {
        updateState();
    }

    @Override
    public void onPlayerStateChanged(final boolean b, final int i)
    {
        if (i == ExoPlayer.STATE_ENDED && playlist != null && position < playlist.size() - 1)
        {
            position++;
            startPlayback();
        }
    }

    @Override
    public void onPlayWhenReadyCommitted()
    {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e)
    {

    }

    @Override
    public void onLoadError(final int i, final IOException e)
    {
        if (e instanceof HttpDataSource.InvalidResponseCodeException && ((HttpDataSource.InvalidResponseCodeException) e).responseCode == 404)
        {
            Toast.makeText(context, R.string.error_not_found, Toast.LENGTH_LONG).show();
            stop();
        } else
        {
            showError();
        }
    }

    private void showError()
    {
        if (System.currentTimeMillis() - 5000 > lastErrorMessage)
        {
            lastErrorMessage = System.currentTimeMillis();
            Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e)
    {
        showError();
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e)
    {
        showError();
    }

    @Override
    public void onAudioTrackUnderrun(int i, long l, long l1)
    {

    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e)
    {
        showError();
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e)
    {
        showError();
    }

    @Override
    public void onDecoderInitialized(String s, long l, long l1)
    {

    }
}
