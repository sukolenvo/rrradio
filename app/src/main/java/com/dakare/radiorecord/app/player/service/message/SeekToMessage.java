package com.dakare.radiorecord.app.player.service.message;

import android.os.Bundle;
import android.os.Message;
import lombok.Getter;

@Getter
public class SeekToMessage extends PlayerMessage
{

    private static final String POSITION_KEY = "position";

    private final float position;

    public SeekToMessage(final float position)
    {
        super(PlayerMessageType.SEEK_TO);
        this.position = position;
    }

    @SuppressWarnings("unchecked")
    public SeekToMessage(final Bundle data)
    {
        super(PlayerMessageType.SEEK_TO);
        this.position = data.getFloat(POSITION_KEY);
    }

    public static SeekToMessage fromMessage(final Bundle args)
    {
        return new SeekToMessage(args);
    }

    @Override
    public Message toMessage()
    {
        Message message = super.toMessage();
        Bundle args = new Bundle();
        args.putFloat(POSITION_KEY, position);
        message.setData(args);
        return message;
    }

}
