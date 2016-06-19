package com.dakare.radiorecord.app.player.service.message;

import android.os.Bundle;
import android.os.Message;
import lombok.Getter;

@Getter
public class PositionStateMessage extends PlayerMessage
{

    private static final String POSITION_KEY = "position";
    private static final String DURATION_KEY = "duration";
    private static final String BUFFERED_KEY = "buffered";

    private final int position;
    private final int duration;
    private final int buffered;

    public PositionStateMessage(final int position, final int duration, final int buffered)
    {
        super(PlayerMessageType.POSITION_STATE);
        this.position = position;
        this.duration = duration;
        this.buffered = buffered;
    }

    @SuppressWarnings("unchecked")
    public PositionStateMessage(final Bundle data)
    {
        super(PlayerMessageType.POSITION_STATE);
        this.position = data.getInt(POSITION_KEY);
        this.duration = data.getInt(DURATION_KEY);
        this.buffered = data.getInt(BUFFERED_KEY);
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
        args.putInt(BUFFERED_KEY, buffered);
        message.setData(args);
        return message;
    }

}
