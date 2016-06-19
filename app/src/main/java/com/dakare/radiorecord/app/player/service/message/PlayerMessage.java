package com.dakare.radiorecord.app.player.service.message;

import android.os.Message;
import android.util.Log;
import lombok.Getter;

public abstract class PlayerMessage
{

    @Getter
    private final PlayerMessageType messageType;

    protected PlayerMessage(final PlayerMessageType messageType)
    {
        this.messageType = messageType;
    }

    public Message toMessage()
    {
        Message message = Message.obtain(null, messageType.getMessageId());
        return message;
    }

    public static PlayerMessage fromMessage(final Message message)
    {
        PlayerMessageType playerMessageType = PlayerMessageType.fromMessage(message);
        if (playerMessageType == null)
        {
            return null;
        }
        switch (playerMessageType)
        {
            case UPDATE_STATE:
                return new UpdateStatePlayerMessage();
            case PLAYBACK_STATE:
                return PlaybackStatePlayerMessage.fromMessage(message.getData());
            case UPDATE_POSITION:
                return new UpdatePositionMessage();
            case POSITION_STATE:
                return PositionStateMessage.fromMessage(message.getData());
            case SEEK_TO:
                return SeekToMessage.fromMessage(message.getData());
            default:
                Log.w("Player Message", "Cannot deserialise message " + message.what);
                break;
        }
        return null;
    }
}
