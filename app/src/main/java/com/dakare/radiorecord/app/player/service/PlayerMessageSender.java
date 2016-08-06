package com.dakare.radiorecord.app.player.service;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import com.dakare.radiorecord.app.player.service.message.PlayerMessageType;

public class PlayerMessageSender {

    private final Messenger messenger;

    public PlayerMessageSender(final IBinder deviceService) {
        messenger = new Messenger(deviceService);
    }

    public void sendMessage(final Message message) {
        try {
            messenger.send(message);
        } catch (RemoteException e) {
            Log.e("DeviceMessage sender", "Cannot send message", e);
        }
    }

    public void registerServiceResponseReceiver(final Messenger messageReceiver) {
        Message message = Message.obtain(null, PlayerMessageType.REGISTER_SERVICE_CLIENT.getMessageId());
        message.replyTo = messageReceiver;
        sendMessage(message);
    }

    public void unregisterServiceResponseReceiver(
            final Messenger messageReceiver) {
        Message message = Message.obtain(null, PlayerMessageType.UNREGISTER_SERVICE_CLIENT.getMessageId());
        message.replyTo = messageReceiver;
        sendMessage(message);
    }
}
