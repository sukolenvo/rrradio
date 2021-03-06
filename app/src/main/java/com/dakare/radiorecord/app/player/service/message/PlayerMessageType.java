package com.dakare.radiorecord.app.player.service.message;

import android.os.Message;
import android.util.Log;
import lombok.Getter;

public enum PlayerMessageType {
    REGISTER_SERVICE_CLIENT(1000),
    UNREGISTER_SERVICE_CLIENT(1001),
    UPDATE_STATE(1002),
    STOP_PLAYBACK(1004),
    PLAYBACK_STATE(1003),
    UPDATE_POSITION(1005),
    POSITION_STATE(1006),
    METADATA_UPDATE(1007),
    SEEK_TO(1008),
    RECORD(1009);

    @Getter
    private final int messageId;

    PlayerMessageType(final int messageId) {
        this.messageId = messageId;
    }

    public static PlayerMessageType fromMessage(Message message) {
        for (PlayerMessageType serviceMessageType : values()) {
            if (message.what == serviceMessageType.messageId) {
                return serviceMessageType;
            }
        }
        Log.w("PlayerMessageType", "Unknown message type " + message.what);
        return null;
    }
}
