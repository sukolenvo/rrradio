package com.dakare.radiorecord.app.download.service;

import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import com.dakare.radiorecord.app.download.service.message.FileMessageType;

public class FileMessageSender {

    private final Messenger messenger;

    public FileMessageSender(final IBinder deviceService) {
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
        Message message = Message.obtain(null, FileMessageType.REGISTER_SERVICE_CLIENT.getMessageId());
        message.replyTo = messageReceiver;
        sendMessage(message);
    }

    public void unregisterServiceResponseReceiver(
            final Messenger messageReceiver) {
        Message message = Message.obtain(null, FileMessageType.UNREGISTER_SERVICE_CLIENT.getMessageId());
        message.replyTo = messageReceiver;
        sendMessage(message);
    }
}
