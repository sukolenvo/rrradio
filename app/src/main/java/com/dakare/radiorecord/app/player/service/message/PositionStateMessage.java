package com.dakare.radiorecord.app.player.service.message;

import android.os.Bundle;
import android.os.Message;
import lombok.Getter;

@Getter
public class PositionStateMessage extends PlayerMessage
{

    private static final String POSITION_KEY = "position";
    private static final String DURATION_KEY = "duration";

    private final int position;
    private final int duration;

    public PositionStateMessage(final int position, final int duration)
    {
        super(PlayerMessageType.POSITION_STATE);
        this.position = position;
        this.duration = duration;
    }

    @SuppressWarnings("unchecked")
    public PositionStateMessage(final Bundle data)
    {
        super(PlayerMessageType.POSITION_STATE);
        this.position = data.getInt(POSITION_KEY);
        this.duration = data.getInt(DURATION_KEY);
    }

    public static PositionStateMessage fromMessage(final Bundle args)
    {
        return new PositionStateMessage(args);
    }

    @Override
    public Message toMessage()
    {
        Message message = super.toMessage();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        args.putInt(DURATION_KEY, duration);
        message.setData(args);
        return message;
    }

}
