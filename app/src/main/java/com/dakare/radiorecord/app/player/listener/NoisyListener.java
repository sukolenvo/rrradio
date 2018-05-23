package com.dakare.radiorecord.app.player.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

import java.io.Closeable;

public class NoisyListener implements IPlayerStateListener, Closeable {


    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            Intent pauseIntent = new Intent(context, PlayerService.class);
            pauseIntent.setAction(NotificationListener.ACTION_PAUSE);
            context.startService(pauseIntent);
        }
    };
    private final Context context;

    public NoisyListener(final Context context) {
        this.context = context;
        context.registerReceiver(receiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
    }

    @Override
    public void onIconChange(final Bitmap image) {
        //Nothing to do
    }

    @Override
    public void close() {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            //Nothing to do. Receiver not registered
        }
    }
}
