package com.dakare.radiorecord.app.player.listener.controls;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.listener.IPlayerStateListener;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

public class MediaControlsListener implements IPlayerStateListener, AudioManager.OnAudioFocusChangeListener,
        Closeable {

    private final Context context;
    private final MediaSessionCompat mediaSession;
    private final AudioManager audioManager;
    private final Bitmap stub;
    private String artist;
    private String song;
    private Bitmap image;
    private AtomicBoolean focusGained = new AtomicBoolean();

    public MediaControlsListener(final Context context) {
        this.context = context;
        ComponentName receiver = new ComponentName(context.getPackageName(), ControlReceiver.class.getName());
        mediaSession = new MediaSessionCompat(context, "MediaControlsListener", receiver, null);
        mediaSession.setCallback(new ControlCallback(context));
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaSession.setActive(true);
        stub = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.PLAY) {
            if (!focusGained.get() && AudioManager.AUDIOFOCUS_REQUEST_FAILED ==
                    audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
                Intent intent = new Intent(context, PlayerService.class);
                intent.setAction(NotificationListener.ACTION_STOP);
                context.startService(intent);
                return;
            }
            focusGained.set(true);
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    .build());
            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
            artist = message.getArtist() == null ? message.getPlaying().getTitle() : message.getArtist();
            song = message.getSong() == null ? message.getPlaying().getSubtitle() : message.getSong();
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song);
            if (message.getIcon() == null || !PreferenceManager.getInstance(context).isMusicImageEnabled() || image == null) {
                image = null;
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, stub);
            } else {
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, image);
            }
            mediaSession.setMetadata(builder.build());
        } else if (message.getState() == PlayerState.PAUSE){
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PAUSED, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    .build());
        } else {
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_NONE, 0, 0)
                    .build());
            focusGained.set(false);
            audioManager.abandonAudioFocus(this);
            image = null;
        }
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        focusGained.set(focusChange == AudioManager.AUDIOFOCUS_GAIN);
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            Intent intent = new Intent(context, PlayerService.class);
            intent.setAction(NotificationListener.ACTION_RESUME);
            context.startService(intent);
        } else {
            Intent intent = new Intent(context, PlayerService.class);
            intent.setAction(NotificationListener.ACTION_PAUSE);
            context.startService(intent);
        }
    }

    @Override
    public void close() {
        mediaSession.release();
    }

    @Override
    public void onIconChange(final Bitmap image) {
        this.image = image;
        mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, image)
                .build());
    }
}
