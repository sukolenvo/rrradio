package com.dakare.radiorecord.app.player.listener;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessageType;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PlayerListenerHandler extends Handler implements Target {

    @Getter
    private final List<IPlayerStateListener> listeners = new ArrayList<>();
    private String lastUrl;
    private int imageScale = 1;

    public PlayerListenerHandler() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager actManager = (ActivityManager) RecordApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            actManager.getMemoryInfo(memInfo);
            long memoryGb = memInfo.totalMem / (1_000_000_000);
            imageScale = (int) Math.max(1, memoryGb);
        }
    }

    @Override
    public void handleMessage(final Message msg) {
        PlayerMessage message = PlayerMessage.fromMessage(msg);
        if (message == null) {
            super.handleMessage(msg);
        } else if (message.getMessageType() == PlayerMessageType.PLAYBACK_STATE) {
            PlaybackStatePlayerMessage stateMessage = (PlaybackStatePlayerMessage) message;
            for (IPlayerStateListener listener : listeners) {
                listener.onPlaybackChange(stateMessage);
            }
            if (stateMessage.getState() == PlayerState.STOP || stateMessage.getIcon() == null) {
                lastUrl = null;
            } else if (PreferenceManager.getInstance(RecordApplication.getInstance()).isMusicImageEnabled()
                    && !stateMessage.getIcon().equals(lastUrl)) {
                lastUrl = stateMessage.getIcon();
                Picasso.with(RecordApplication.getInstance())
                        .load(lastUrl)
                        .resize(128 * imageScale, 128 * imageScale)
                        .into(this);
            }
        }

    }

    public void addListener(IPlayerStateListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        //TODO: verify imageUrl if image is outdated
        for (IPlayerStateListener listener : listeners) {
            listener.onIconChange(bitmap);
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
