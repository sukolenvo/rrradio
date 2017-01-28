package com.dakare.radiorecord.app.player.listener.remote;

import android.app.NotificationManager;
import android.app.Service;
import android.graphics.Bitmap;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

public interface NotificationRemote {

    void setImage(final Bitmap image);

    void setImage(final int resId);

    void notify(final NotificationManager notificationManager, final int id);

    void updateTitle(final PlaybackStatePlayerMessage message);

    void setPlaying(final boolean playing);

    void setupIntents(final Service service);
}
