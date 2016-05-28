package com.dakare.radiorecord.app.player.service;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.listener.AbstractPlayerStateListener;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessageType;

public class PlayerServiceMessageHandler extends Handler {

    private final ServiceClientsList clients = new ServiceClientsList();
    private final Player player;

    public PlayerServiceMessageHandler(final Player player)
    {
        this.player = player;
        player.setPlayerServiceMessageHandler(this);
    }

    @Override
	public void handleMessage(final Message msg) {
        PlayerMessageType playerMessageType = PlayerMessageType.fromMessage(msg);
        if (playerMessageType == null)
        {
            super.handleMessage(msg);

        } else
        {
            switch (playerMessageType)
            {
                case REGISTER_SERVICE_CLIENT:
                    clients.registerClient(msg.replyTo);
                    break;
                case UNREGISTER_SERVICE_CLIENT:
                    clients.unregisterClient(msg.replyTo);
                    break;
                case UPDATE_STATE:
                    player.updateState();
                    break;
                case STOP_PLAYBACK:
                    player.stop();
                    break;
                case UPDATE_POSITION:
                    player.updatePosition();
                    break;
                default:
                    Log.w("PlayerMessageHandler", "Unrecognised message type " + msg.what);
                    super.handleMessage(msg);
            }
        }
	}

    public void addPlayerStateListener(final AbstractPlayerStateListener listener)
    {
        clients.registerClient(new Messenger(listener));
    }

	public void handleServiceResponse(final PlayerMessage response) {
		clients.sendBroadcastMessage(response.toMessage());
	}
}
