package com.dakare.radiorecord.app.player.service.playback;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.load.loader.BasicCategoryLoader;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import com.dakare.radiorecord.app.player.service.MetadataLoader;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerServiceMessageHandler;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.equalizer.EqualizerSettings;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PositionStateMessage;
import com.dakare.radiorecord.app.player.service.playback.record.PlaybackRecordManager;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.*;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

public class PlayerJellybean implements MetadataLoader.MetadataChangeCallback, AudioRendererEventListener,
        Player, SharedPreferences.OnSharedPreferenceChangeListener, ExoPlayer.EventListener {

    private static final int RETRY_COUNT = 63;

    @Getter
    private final Context context;
    private final MetadataLoader metadataLoader;
    private final SimpleExoPlayer player;
    private final PreferenceManager preferenceManager;
    private final PlaybackRecordManager playbackRecordManager;
    @Getter
    private ArrayList<PlaylistItem> playlist;
    @Getter
    private int position;
    @Setter
    private PlayerServiceMessageHandler playerServiceMessageHandler;
    @Getter
    private PlayerState state = PlayerState.STOP;
    private long lastErrorMessage;
    private Equalizer equalizer;

    public PlayerJellybean(final Context context) {
        this.context = context;
        TrackSelection.Factory videoTrackSelectionFactory = new FixedTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        player.addListener(this);
        player.setAudioDebugListener(this);
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
    }

    public void updateState() {
        playerServiceMessageHandler.handleServiceResponse(
                new PlaybackStatePlayerMessage(getCurrentPlaylistItem(), position, state, metadataLoader.getResponse(),
                        playbackRecordManager.isRecord()));
    }

    private void startPlayback() {
        if (playlist != null && !playlist.isEmpty()) {
            player.stop();
            player.seekTo(0L);
            player.setPlayWhenReady(false);
            PlaylistItem playlistItem = getCurrentPlaylistItem();
            Answers.getInstance().logCustom(new CustomEvent("Playlist item")
                    .putCustomAttribute("live", playlistItem.isLive() + "")
                    .putCustomAttribute("recording", playbackRecordManager.isRecord() + "")
                    .putCustomAttribute("station", playlistItem.getStation().name()));
            preferenceManager.setLastPosition(position);
            DataSource.Factory dataSourceFactory = playbackRecordManager.startRecording(playlistItem, new DefaultDataSourceFactory(RecordApplication.getInstance(),
                    BasicCategoryLoader.USER_AGENT));
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .setExtractorsFactory(extractorsFactory)
                    .setMinLoadableRetryCount(RETRY_COUNT)
                    .createMediaSource(Uri.parse(playlistItem.getUrl()));
            player.prepare(mediaSource);
            state = PlayerState.PLAY;
            metadataLoader.start(playlistItem);
            updateState();
        }
    }

    private PlaylistItem getCurrentPlaylistItem() {
        return playlist.get(Math.min(position, playlist.size() - 1));
    }

    public void next() {
        if (playlist != null && !playlist.isEmpty()) {
            position = (position + 1) % playlist.size();
            playbackRecordManager.stop();
            startPlayback();
            metadataLoader.start(getCurrentPlaylistItem());
        }
        updateState();
    }

    public void previous() {
        if (playlist != null && !playlist.isEmpty()) {
            position = (position - 1 + playlist.size()) % playlist.size();
            playbackRecordManager.stop();
            startPlayback();
            metadataLoader.start(getCurrentPlaylistItem());
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
            metadataLoader.stop();
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
                PlaylistItem currentPlaylistItem = getCurrentPlaylistItem();
                metadataLoader.start(currentPlaylistItem);
                if (currentPlaylistItem.isLive()) {
                    player.seekToDefaultPosition();
                }
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
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED && playlist != null && position < playlist.size() - 1) {
            if (!getCurrentPlaylistItem().isLive()) {
                position++;
            }
            startPlayback();
        } else if (playbackState == ExoPlayer.STATE_READY && equalizer != null) {
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
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        showError();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {

    }

    private void showError() {
        if (System.currentTimeMillis() - 5000 > lastErrorMessage) {
            lastErrorMessage = System.currentTimeMillis();
            Toast.makeText(context, R.string.error_connect, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
        if (PreferenceManager.EQ_LEVELS_KEY.equals(key) && equalizer != null) {
            preferenceManager.getEqSettings().applyLevels(equalizer);
        }
    }

    @Override
    public void onAudioEnabled(DecoderCounters counters) {
    }

    @Override
    public void onAudioSessionId(final int audioSessionId) {
        try {
            if (preferenceManager.isEqSettingsEnabled()) {
                releaseEq();
                equalizer = new Equalizer(0, audioSessionId);
                EqualizerSettings old = preferenceManager.getEqSettings();
                old.applyLevels(equalizer);
                EqualizerSettings newSettings = new EqualizerSettings(equalizer);
                if (!newSettings.equals(old)) {
                    preferenceManager.setEqSettings(newSettings);
                }
                preferenceManager.registerChangeListener(PlayerJellybean.this);
            }
        } catch (UnsatisfiedLinkError | RuntimeException e) {
            Crashlytics.logException(e);
            preferenceManager.setEqSettings(false);
            Toast.makeText(context, R.string.audio_effect_error, Toast.LENGTH_LONG).show();
        }
        player.setPlayWhenReady(true);
    }

    @Override
    public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
    }

    @Override
    public void onAudioInputFormatChanged(Format format) {
    }

    @Override
    public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

    }

    @Override
    public void onAudioDisabled(DecoderCounters counters) {
        releaseEq();
        preferenceManager.unregisterChangeListener(PlayerJellybean.this);
    }

    private void releaseEq() {
        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
    }

    @Override
    public void shutdown() {
        player.release();
        metadataLoader.shutdown();
    }
}
