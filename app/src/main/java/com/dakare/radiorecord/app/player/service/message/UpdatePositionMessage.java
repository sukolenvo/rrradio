package com.dakare.radiorecord.app.player.service.message;

public class UpdatePositionMessage extends PlayerMessage
{
    public UpdatePositionMessage()
    {
        super(PlayerMessageType.UPDATE_POSITION);
    }
}
