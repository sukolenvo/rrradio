package com.dakare.radiorecord.app.download.service;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.dakare.radiorecord.app.download.service.message.FileMessage;
import com.dakare.radiorecord.app.download.service.message.FileMessageType;
import com.dakare.radiorecord.app.player.service.ServiceClientsList;

public class FileServiceMessageHandler extends Handler {

    private final ServiceClientsList clients = new ServiceClientsList();

    @Override
    public void handleMessage(final Message msg) {
        FileMessageType fileMessageType = FileMessageType.fromMessage(msg);
        if (fileMessageType == null) {
            super.handleMessage(msg);

        } else {
            switch (fileMessageType) {
                case REGISTER_SERVICE_CLIENT:
                    clients.registerClient(msg.replyTo);
                    break;
                case UNREGISTER_SERVICE_CLIENT:
                    clients.unregisterClient(msg.replyTo);
                    break;
                default:
                    Log.w("PlayerMessageHandler", "Unrecognised message type " + msg.what);
                    super.handleMessage(msg);
            }
        }
    }

    public void handleServiceResponse(final FileMessage response) {
        clients.sendBroadcastMessage(response.toMessage());
    }
}
