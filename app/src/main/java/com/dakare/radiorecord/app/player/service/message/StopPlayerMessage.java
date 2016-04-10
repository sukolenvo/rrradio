package com.dakare.radiorecord.app.player.service.message;

public class StopPlayerMessage extends PlayerMessage
{

    public StopPlayerMessage()
    {
        super(PlayerMessageType.STOP_PLAYBACK);
    }

}
