package com.dakare.radiorecord.app.player.listener;

import android.graphics.Bitmap;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

public interface IPlayerStateListener {

    void onPlaybackChange(final PlaybackStatePlayerMessage message);

    void onIconChange(final Bitmap image);
}
