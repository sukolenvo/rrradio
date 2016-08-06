package com.dakare.radiorecord.app.download.service.message;

import android.os.Message;
import android.util.Log;
import lombok.Getter;

public abstract class FileMessage {

    @Getter
    private final FileMessageType messageType;

    protected FileMessage(final FileMessageType messageType) {
        this.messageType = messageType;
    }

    public Message toMessage() {
        Message message = Message.obtain(null, messageType.getMessageId());
        return message;
    }

    public static FileMessage fromMessage(final Message message) {
        FileMessageType fileMessageType = FileMessageType.fromMessage(message);
        if (fileMessageType == null) {
            return null;
        }
        switch (fileMessageType) {
            case UPDATE_ITEM:
                return new UpdateFileMessage(message.getData());
            case REMOVE_RESPONSE:
                return new RemoveFileResponse(message.getData());
            default:
                Log.w("File Message", "Cannot deserialize message " + message.what);
                break;
        }
        return null;
    }
}
