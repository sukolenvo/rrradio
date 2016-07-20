package com.dakare.radiorecord.app.download.service.message;

import android.os.Message;
import android.util.Log;
import lombok.Getter;

public enum FileMessageType
{
    REGISTER_SERVICE_CLIENT(1000),
    UNREGISTER_SERVICE_CLIENT(1001),
    UPDATE_ITEM(10001),
    REMOVE_RESPONSE(10003);

    @Getter
    private final int messageId;

    FileMessageType(final int messageId)
    {
        this.messageId = messageId;
    }

    public static FileMessageType fromMessage(Message message)
    {
        for (FileMessageType serviceMessageType : values())
        {
            if (message.what == serviceMessageType.messageId)
            {
                return serviceMessageType;
            }
        }
        Log.w("FileMessageType", "Unknown message type " + message.what);
        return null;
    }
}
