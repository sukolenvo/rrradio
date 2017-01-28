package com.dakare.radiorecord.app.player.listener.remote;

import android.app.Notification;
import android.os.Build;

public final class NotificationRemoteFactory {

    private NotificationRemoteFactory() {
        throw new AssertionError("Do not create instances of this class");
    }

    public static NotificationRemote create(final String packageName, final Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return new NotificationRemoteJelly(packageName, notification);
        }
        return new NotificationRemoteImpl(packageName, notification);
    }
}
