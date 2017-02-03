package com.dakare.radiorecord.app.player.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

import java.io.Closeable;
import java.io.IOException;

public class HeadsetPlugListener implements IPlayerStateListener, Closeable {

    private static final int HEADSET_PLUGGED = 1;
    private static final String HEADSET_STATE_KEY = "state";

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            boolean plugged = intent.getIntExtra(HEADSET_STATE_KEY, -1) == HEADSET_PLUGGED;
            if (state == PlayerState.PAUSE && plugged || state == PlayerState.PLAY && !plugged) {
                Intent pauseIntent = new Intent(context, PlayerService.class);
                pauseIntent.setAction(NotificationListener.ACTION_PLAY_PAUSE);
                context.startService(pauseIntent);
            }
        }
    };
    private final Context context;
    private PlayerState state;

    public HeadsetPlugListener(final Context context) {
        this.context = context;
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        state = message.getState();
    }

    @Override
    public void onIconChange(final Bitmap image) {
        //Nothing to do
    }

    @Override
    public void close() throws IOException {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException e) {
            //Nothing to do. Receiver not registered
        }
    }
}
