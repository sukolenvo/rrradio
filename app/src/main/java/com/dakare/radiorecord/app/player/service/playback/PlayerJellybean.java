package com.dakare.radiorecord.app.player.service.playback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.MetadataLoader;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerServiceMessageHandler;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PositionStateMessage;
import com.dakare.radiorecord.app.player.service.playback.record.PlaybackRecordManager;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.HttpDataSource;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerJellybean implements MetadataLoader.MetadataChangeCallback, ExoPlayer.Listener,
                                        ExtractorSampleSource.EventListener, MediaCodecAudioTrackRenderer.EventListener,
                                        Player,
                                        SharedPreferences.OnSharedPreferenceChangeListener {
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
    @Getter
    private PlayerState state = PlayerState.STOP;
    private final ExoPlayer player;
    private long lastErrorMessage;
    private Equalizer equalizer;
    private final PreferenceManager preferenceManager;
    private final PlaybackRecordManager playbackRecordManager;

    public PlayerJellybean(final Context context) {
        this.context = context;
        player = ExoPlayer.Factory.newInstance(1);
        player.addListener(this);
        metadataLoader = new MetadataLoader(this, context);
        playlist = new ArrayList<>();
        playlist.addAll(PreferenceManager.getInstance(context).getLastPlaylist());
        preferenceManager = PreferenceManager.getInstance(context);
        position = preferenceManager.getLastPosition();
        playbackRecordManager = new PlaybackRecordManager(context);
    }

    public void play(final ArrayList<PlaylistItem> playlist, final int position) {
        this.playlist = playlist;
        this.position = position;
        playbackRecordManager.stop();
        startPlayback();
        if (playlist.get(position).isLive()) {
            metadataLoader.start(playlist.get(position).getStation());
        } else {
            metadataLoader.stop();
        }
        updateState();
    }

    public void updateState() {
        playerServiceMessageHandler.handleServiceResponse(
                new PlaybackStatePlayerMessage(playlist.get(position), position, state, metadataLoader.getResponse(),
                                               playbackRecordManager.isRecord()));
    }

    private void startPlayback() {
        if (playlist != null && !playlist.isEmpty()) {
            player.stop();
            player.seekTo(0L);
            player.setPlayWhenReady(true);
            PlaylistItem playlistItem = playlist.get(position);
            preferenceManager.setLastPosition(position);
            Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
            DataSource dataSource = playbackRecordManager.startRecording(playlistItem,
                                                                         DataSourceFactory.createDataSource(
                                                                                 playlistItem.getUrl(),
                                                                                 playlistItem.isLive()));
            if (dataSource != null) {
                ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(playlistItem.getUrl()),
                                                                               dataSource, allocator,
                                                                               BUFFER_SEGMENT_COUNT * BUFFER_SEGMENT_SIZE,
                                                                               ExtractorSampleSource.DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE,
                                                                               uiHandler, this, 0);
                MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource,
                                                                                              MediaCodecSelector.DEFAULT,
                                                                                              null,
                                                                                              true,
                                                                                              uiHandler,
                                                                                              this,
                                                                                              AudioCapabilities.getCapabilities(
                                                                                                      context),
                                                                                              AudioManager.STREAM_MUSIC) {
                    @Override
                    protected void onAudioSessionId(final int audioSessionId) {
                        try {
                            if (preferenceManager.isEqSettingsEnabled()) {
                                equalizer = new Equalizer(0, audioSessionId);
                                EqualizerSettings old = preferenceManager.getEqSettings();
                                old.applyLevels(equalizer);
                                EqualizerSettings newSettings = new EqualizerSettings(equalizer);
                                if (!newSettings.equals(old)) {
                                    preferenceManager.setEqSettings(newSettings);
                                }
                                super.onAudioSessionId(audioSessionId);
                                preferenceManager.registerChangeListener(PlayerJellybean.this);
                            }
                        } catch (UnsatisfiedLinkError e) {
                            Crashlytics.logException(e);
                            preferenceManager.setEqSettings(false);
                            Toast.makeText(context, R.string.audio_effect_error, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    protected void onDisabled() throws ExoPlaybackException {
                        if (equalizer != null) {
                            equalizer.release();
                            equalizer = null;
                        }
                        preferenceManager.unregisterChangeListener(PlayerJellybean.this);
                        super.onDisabled();
                    }
                };
                player.prepare(audioRenderer);
                state = PlayerState.PLAY;
            }
            updateState();
        }
    }

    public void next() {
        if (playlist != null && !playlist.isEmpty()) {
            position = (position + 1) % playlist.size();
            playbackRecordManager.stop();
            startPlayback();
            if (playlist.get(position).isLive()) {
                metadataLoader.start(playlist.get(position).getStation());
            } else {
                metadataLoader.stop();
            }
        }
        updateState();
    }

    public void previous() {
        if (playlist != null && !playlist.isEmpty()) {
            position = (position - 1 + playlist.size()) % playlist.size();
            playbackRecordManager.stop();
            startPlayback();
            if (playlist.get(position).isLive()) {
                metadataLoader.start(playlist.get(position).getStation());
            } else {
                metadataLoader.stop();
            }
        }
        updateState();
    }

    public void stop() {
        context.stopService(new Intent(context, PlayerService.class));
        player.stop();
        player.seekTo(0L);
        metadataLoader.stop();
        state = PlayerState.STOP;
        playbackRecordManager.stop();
        updateState();
    }

    public void pause() {
        if (state == PlayerState.PLAY) {
            state = PlayerState.PAUSE;
            player.setPlayWhenReady(false);
        }
        updateState();
    }

    public void resume() {
        switch (state) {
            case STOP:
                if (playlist.isEmpty()) {
                    Toast.makeText(context, R.string.no_results, Toast.LENGTH_SHORT).show();
                } else {
                    play(playlist, position);
                }
                break;
            default:
                state = PlayerState.PLAY;
                player.setPlayWhenReady(true);
                break;
        }
        updateState();
    }

    public void updatePosition() {
        playerServiceMessageHandler.handleServiceResponse(
                new PositionStateMessage((int) player.getCurrentPosition(), (int) player.getDuration(),
                                         (int) player.getBufferedPosition()));
    }

    @Override
    public void seekTo(final float position) {
        if (player.getDuration() > 0) {
            player.seekTo((long) (player.getDuration() * position));
        }
    }

    @Override
    public void record() {
        playbackRecordManager.setRecord(true);
        startPlayback();
    }

    @Override
    public void onMetadataChanged() {
        updateState();
    }

    @Override
    public void onPlayerStateChanged(final boolean b, final int i) {
        if (i == ExoPlayer.STATE_ENDED && playlist != null && position < playlist.size() - 1) {
            position++;
            startPlayback();
        } else if (i == ExoPlayer.STATE_READY && equalizer != null) {
            try {
                if (!equalizer.getEnabled()) {
                    equalizer.setEnabled(true);
                }
            } catch (IllegalStateException e) {
                Log.e("Exoplayer", "Failed to enable equalizer", e);
            }
        }
    }

    @Override
    public void onPlayWhenReadyCommitted() {

    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {

    }

    @Override
    public void onLoadError(final int i, final IOException e) {
        if (e instanceof HttpDataSource.InvalidResponseCodeException && ((HttpDataSource.InvalidResponseCodeException) e).responseCode == 404) {
            Toast.makeText(context, R.string.error_not_found, Toast.LENGTH_LONG).show();
            stop();
        } else {
            showError();
        }
    }

    private void showError() {
        if (System.currentTimeMillis() - 5000 > lastErrorMessage) {
            lastErrorMessage = System.currentTimeMillis();
            Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
        showError();
    }

    @Override
    public void onAudioTrackWriteError(AudioTrack.WriteException e) {
        showError();
    }

    @Override
    public void onAudioTrackUnderrun(int i, long l, long l1) {

    }

    @Override
    public void onDecoderInitializationError(MediaCodecTrackRenderer.DecoderInitializationException e) {
        showError();
    }

    @Override
    public void onCryptoError(MediaCodec.CryptoException e) {
        showError();
    }

    @Override
    public void onDecoderInitialized(String s, long l, long l1) {

    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (PreferenceManager.EQ_LEVELS_KEY.equals(key) && equalizer != null) {
            preferenceManager.getEqSettings().applyLevels(equalizer);
        }
    }
}
