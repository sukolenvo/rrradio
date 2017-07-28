package com.dakare.radiorecord.app.player.listener.controls;

import android.content.Context;
import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ControlCallback extends MediaSessionCompat.Callback {

    private final Context context;

    @Override
    public void onPlay() {
        Intent service = new Intent(context, PlayerService.class);
        service.setAction(NotificationListener.ACTION_RESUME);
        context.startService(service);
    }

    @Override
    public void onPause() {
        Intent service = new Intent(context, PlayerService.class);
        service.setAction(NotificationListener.ACTION_PAUSE);
        context.startService(service);
    }

    @Override
    public void onSkipToPrevious() {
        Intent service = new Intent(context, PlayerService.class);
        service.setAction(NotificationListener.ACTION_PREVIOUS);
        context.startService(service);
    }

    @Override
    public void onSkipToNext() {
        Intent service = new Intent(context, PlayerService.class);
        service.setAction(NotificationListener.ACTION_NEXT);
        context.startService(service);
    }

    @Override
    public void onStop() {
        Intent service = new Intent(context, PlayerService.class);
        service.setAction(NotificationListener.ACTION_STOP);
        context.startService(service);
    }
}