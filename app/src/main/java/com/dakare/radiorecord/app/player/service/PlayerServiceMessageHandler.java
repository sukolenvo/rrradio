package com.dakare.radiorecord.app.player.service;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.dakare.radiorecord.app.player.listener.IPlayerStateListener;
import com.dakare.radiorecord.app.player.listener.PlayerListenerHandler;
import com.dakare.radiorecord.app.player.service.message.PlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessageType;
import com.dakare.radiorecord.app.player.service.message.SeekToMessage;
import com.dakare.radiorecord.app.player.service.playback.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PlayerServiceMessageHandler extends Handler {

    private final ServiceClientsList clients = new ServiceClientsList();
    private final Player player;
    private final PlayerListenerHandler playerListenerHandler;

    public PlayerServiceMessageHandler(final Player player) {
        this.player = player;
        playerListenerHandler = new PlayerListenerHandler();
        clients.registerClient(new Messenger(playerListenerHandler));
        player.setPlayerServiceMessageHandler(this);
    }

    @Override
    public void handleMessage(final Message msg) {
        PlayerMessageType playerMessageType = PlayerMessageType.fromMessage(msg);
        if (playerMessageType == null) {
            super.handleMessage(msg);

        } else {
            switch (playerMessageType) {
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
                case SEEK_TO:
                    player.seekTo(SeekToMessage.fromMessage(msg.getData()).getPosition());
                    break;
                case RECORD:
                    player.record();
                    player.updateState();
                    break;
                default:
                    Log.w("PlayerMessageHandler", "Unrecognised message type " + msg.what);
                    super.handleMessage(msg);
            }
        }
    }

    public void addPlayerStateListener(final IPlayerStateListener listener) {
        playerListenerHandler.addListener(listener);
    }

    public void handleServiceResponse(final PlayerMessage response) {
        clients.sendBroadcastMessage(response.toMessage());
    }

    public List<IPlayerStateListener> getListeners() {
        return playerListenerHandler.getListeners();
    }
}
