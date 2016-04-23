package com.dakare.radiorecord.app.player.listener;

import android.os.Handler;
import android.os.Message;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessageType;

public abstract class AbstractPlayerStateListener extends Handler
{
    @Override
    public void handleMessage(final Message msg)
    {
        PlayerMessage message = PlayerMessage.fromMessage(msg);
        if (message == null)
        {
            super.handleMessage(msg);
        } else if (message.getMessageType() == PlayerMessageType.PLAYBACK_STATE)
        {
            onPlaybackChange((PlaybackStatePlayerMessage) message);
        }

    }

    protected abstract void onPlaybackChange(final PlaybackStatePlayerMessage message);
}
