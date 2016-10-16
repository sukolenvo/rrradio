package com.dakare.radiorecord.app.player.listener.controls;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.session.PlaybackState;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.listener.AbstractPlayerStateListener;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MediaControlsListener extends AbstractPlayerStateListener
        implements AudioManager.OnAudioFocusChangeListener, ImageLoadingListener {

    private final Context context;
    private final MediaSessionCompat mediaSession;
    private final AudioManager audioManager;
    //TODO: extract load logic to separate helper unit
    private String lastUrl;
    private final Bitmap stub;
    private String artist;
    private String song;

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
    protected void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.PLAY) {
            if (AudioManager.AUDIOFOCUS_REQUEST_FAILED ==
                    audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {
                Intent intent = new Intent(context, PlayerService.class);
                intent.setAction(NotificationListener.ACTION_STOP);
                context.startService(intent);
                return;
            }
            mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0)
                    .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PLAY
                            | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                    .build());
            MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
            artist = message.getArtist() == null ? message.getItems().get(message.getPosition()).getTitle() : message.getArtist();
            song = message.getSong() == null ? message.getItems().get(message.getPosition()).getSubtitle() : message.getSong();
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song);
            if (message.getIcon() == null || !PreferenceManager.getInstance(context).isMusicImageEnabled()) {
                lastUrl = null;
                builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, stub);
            } else {
                lastUrl = message.getIcon();
                ImageLoader.getInstance().loadImage(message.getIcon(), new ImageSize(128, 128), this);
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
            audioManager.abandonAudioFocus(this);
        }
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
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

    public void shutdown() {
        mediaSession.release();
    }

    @Override
    public void onLoadingStarted(final String imageUri, final View view) {
        //Nothing to do
    }

    @Override
    public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason) {
        //Nothing to do
    }

    @Override
    public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
        if (lastUrl != null && lastUrl.equals(imageUri)) {
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, loadedImage)
                    .build());
        }
    }

    @Override
    public void onLoadingCancelled(final String imageUri, final View view) {
        //Nothing to do
    }
}
