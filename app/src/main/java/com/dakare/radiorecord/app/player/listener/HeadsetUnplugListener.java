package com.dakare.radiorecord.app.player.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.dakare.radiorecord.app.player.service.PlayerService;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

import java.util.concurrent.atomic.AtomicBoolean;

public class HeadsetUnplugListener extends AbstractPlayerStateListener
{

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(final Context context, final Intent intent)
        {
            boolean unplug = intent.getIntExtra("state", -1) == 0;
            if (!headsetUnplug.getAndSet(unplug) && unplug)
            {
                Intent pauseIntent = new Intent(context, PlayerService.class);
                pauseIntent.setAction(NotificationListener.ACTION_PAUSE);
                context.startService(pauseIntent);
            }
        }
    };
    private final Context context;
    private final AtomicBoolean headsetUnplug = new AtomicBoolean();

    public HeadsetUnplugListener(final Context context)
    {
        this.context = context;
    }

    @Override
    protected void onPlaybackChange(final PlaybackStatePlayerMessage message)
    {
        if (message.getState() == PlayerState.PLAY)
        {
            headsetUnplug.set(true);
            context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        } else
        {
            try
            {
                context.unregisterReceiver(receiver);
            } catch (IllegalArgumentException e)
            {
                //Nothing to do. Receiver not registrated
            }
        }
    }
}
