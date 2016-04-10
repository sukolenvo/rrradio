package com.dakare.radiorecord.app.player.service;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import com.dakare.radiorecord.app.player.service.message.PlayerMessage;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerServiceClient extends Handler
{

	private final Messenger responseReceiver;
	private final AtomicBoolean connected;
	private PlayerMessageSender messageSender;
    @Setter
    private PlayerMessageHandler playerMessageHandler;

	public PlayerServiceClient() {
		responseReceiver = new Messenger(this);
		connected = new AtomicBoolean();
	}

	public void stopReceivingServiceResponses() {
		connected.set(false);
		if (messageSender != null) {
			messageSender.unregisterServiceResponseReceiver(responseReceiver);
		}
	}

	public void startMessagingSession(final IBinder service) {
		messageSender = new PlayerMessageSender(service);
		messageSender.registerServiceResponseReceiver(responseReceiver);
		connected.set(true);
	}

	public boolean isMessagingSessionStarted() {
		return messageSender != null;
	}

	public void stopMessagingSession() {
		messageSender = null;
	}

	public void execute(final PlayerMessage command) {
		sendRequestToService(command);
	}

	private void sendRequestToService(final PlayerMessage command) {
		checkMessagingSession();
		messageSender.sendMessage(command.toMessage());
	}

	private void checkMessagingSession() {
		if (messageSender == null) {
			throw new IllegalStateException(
					"You should call startMessagingSession() before request handling.");
		}
	}

    @Override
    public void handleMessage(final Message msg)
    {
        PlayerMessage message = PlayerMessage.fromMessage(msg);
        if (message == null)
        {
            super.handleMessage(msg);
        } else if (playerMessageHandler != null)
        {
            playerMessageHandler.onMessage(message);
        }

    }

    public interface PlayerMessageHandler
    {
        void onMessage(PlayerMessage playerMessage);
    }
}
