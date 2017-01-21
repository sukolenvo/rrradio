package com.dakare.radiorecord.app.player.listener;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessage;
import com.dakare.radiorecord.app.player.service.message.PlayerMessageType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class PlayerListenerHandler extends Handler implements ImageLoadingListener {

    @Getter
    private final List<IPlayerStateListener> listeners = new ArrayList<>();
    private String lastUrl;
    private final DisplayImageOptions displayImageOptions;

    public PlayerListenerHandler() {
        displayImageOptions = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
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
                ImageLoader.getInstance().loadImage(lastUrl, new ImageSize(128, 128), displayImageOptions, this);
            }
        }

    }

    public void addListener(IPlayerStateListener listener) {
        listeners.add(listener);
    }


    @Override
    public void onLoadingStarted(final String imageUri, final View view) {
        //Nothing to do
    }

    @Override
    public void onLoadingFailed(final String imageUri, final View view, final FailReason failReason) {
        //Nothing to do
    }

    @Override
    public void onLoadingComplete(final String imageUri, final View view, final Bitmap loadedImage) {
        if (lastUrl != null && lastUrl.equals(imageUri)) {
            for (IPlayerStateListener listener : listeners) {
                listener.onIconChange(loadedImage);
            }
        }
    }

    @Override
    public void onLoadingCancelled(final String imageUri, final View view) {
        //Nothing to do
    }
}
