package com.dakare.radiorecord.app.player.listener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import com.dakare.radiorecord.app.PreferenceManager;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.Station;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.listener.remote.NotificationRemote;
import com.dakare.radiorecord.app.player.listener.remote.NotificationRemoteFactory;
import com.dakare.radiorecord.app.player.service.PlayerState;
import com.dakare.radiorecord.app.player.service.message.PlaybackStatePlayerMessage;

public class NotificationListener implements IPlayerStateListener {

    public static final String ACTION_STOP = "stop";
    public static final String ACTION_PREVIOUS = "previous";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_RESUME = "resume";
    public static final String ACTION_PLAY_PAUSE = "play_pause";
    public static final int CONTENT_CODE = 0;
    public static final int STOP_CODE = 1;
    public static final int PAUSE_CODE = 2;
    public static final int RESUME_CODE = 3;
    public static final int NEXT_CODE = 4;
    public static final int PREVIOUS_CODE = 5;

    private final Service service;
    private Notification notification;
    private final NotificationManager notificationManager;
    private boolean foreground;

    public NotificationListener(final Service service) {
        this.service = service;
        notificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(RecordApplication.getInstance(), PlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notification = new NotificationCompat.Builder(service)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(PendingIntent.getActivity(service, CONTENT_CODE, intent, 0))
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    @Override
    public void onPlaybackChange(final PlaybackStatePlayerMessage message) {
        if (message.getState() == PlayerState.STOP) {
            service.stopForeground(true);
            foreground = false;
        } else {
            NotificationRemote notificationRemote = NotificationRemoteFactory.create(service.getPackageName(), notification);
            notificationRemote.updateTitle(message);
            if (message.getIcon() == null || !PreferenceManager.getInstance(service).isMusicImageEnabled()) {
                notificationRemote.setImage(getStationIcon(message.getPlaying().getStation()));
            }
            notificationRemote.setPlaying(message.getState() == PlayerState.PLAY);
            notificationRemote.setupIntents(service);
            if (foreground) {
                notificationRemote.notify(notificationManager, 1);
            } else {
                service.startForeground(1, notification);
                foreground = true;
            }
        }
    }

    public static int getStationIcon(Station station) {
        switch (station) {
            case RADIO_RECORD:
                return R.drawable.icon_alt_rr;
            case FUTURE_HOUSE:
                return R.drawable.icon_alt_fhouse;
            case RECORD_CLUB:
                return R.drawable.icon_alt_edm;
            case MEGAMIX:
                return R.drawable.icon_alt_megamix;
            case GOLD:
                return R.drawable.icon_alt_gold;
            case TRANCEMISSION:
                return R.drawable.icon_alt_trans;
            case PIRATE_STATION:
                return R.drawable.icon_alt_pirate;
            case RECORD_DEEP:
                return R.drawable.icon_alt_deep;
            case VIP_HOUSE:
                return R.drawable.icon_alt_vip;
            case MINIMAL_TECH:
                return R.drawable.icon_alt_mtech;
            case TROPICAL:
                return R.drawable.icon_alt_tropical;
            case RECORD_CHILL_OUT:
                return R.drawable.icon_alt_chil_out;
            case RUSSIAN_MIX:
                return R.drawable.icon_alt_rmix;
            case SUPERDISCO_90:
                return R.drawable.icon_alt_sdisco;
            case MAYATNIK_FUKO:
                return R.drawable.icon_alt_mf;
            case FUTURE_BASS:
                return R.drawable.icon_alt_fbass;
            case REMIX:
                return R.drawable.icon_alt_remix;
            case GASTARBAITER:
                return R.drawable.icon_alt_gastarbaiter;
            case HARD_BASS:
                return R.drawable.icon_alt_hbass;
            case ANSHLAG:
                return R.drawable.icon_alt_anshlag;
            case IBIZA:
                return R.drawable.icon_alt_ibiza;
            case GOA:
                return R.drawable.icon_alt_goa;
            case YO_FM:
                return R.drawable.icon_alt_black;
            case RECORD_BREAKS:
                return R.drawable.icon_alt_breaks;
            case PUMP:
                return R.drawable.icon_alt_old_school;
            case TECHNO:
                return R.drawable.icon_alt_techno;
            case RECORD_TRAP:
                return R.drawable.icon_alt_trap;
            case RECORD_BUDSTEP:
                return R.drawable.icon_alt_dubstep;
            case RAVE_FM:
                return R.drawable.icon_alt_rave;
            case RECORD_DANCECORE:
                return R.drawable.icon_alt_dancecore;
            case NAFTALIN:
                return R.drawable.icon_alt_naftalin;
            case RECORD_ROCK:
                return R.drawable.icon_alt_rock;
            case SLOW_DANCE_FM:
                return R.drawable.icon_alt_medlyak;
            case GOP_FM:
                return R.drawable.icon_alt_gop;
            case RECORD_HARDSTYLE:
                return R.drawable.icon_alt_hstyle;
            default:
                return R.drawable.icon_alt_rr;
        }
    }

    @Override
    public void onIconChange(final Bitmap image) {
        if (image != null) {
            NotificationRemote notificationRemote = NotificationRemoteFactory.create(service.getPackageName(), notification);
            notificationRemote.setImage(image);
            notificationRemote.notify(notificationManager, 1);
        }
    }

}
