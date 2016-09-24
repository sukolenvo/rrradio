package com.dakare.radiorecord.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.PlayerActivity;
import com.dakare.radiorecord.app.player.listener.NotificationListener;
import com.dakare.radiorecord.app.player.service.PlayerService;

public class WidgetReceiver extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i=0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            //TODO: remove code duplicate
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent player = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget_container, player);
            Intent stopIntent = new Intent(context, PlayerService.class);
            stopIntent.setAction(NotificationListener.ACTION_STOP);
            PendingIntent stopPending = PendingIntent.getService(context, 0, stopIntent, 0);
            views.setOnClickPendingIntent(R.id.button_media_stop, stopPending);
            Intent pauseIntent = new Intent(context, PlayerService.class);
            pauseIntent.setAction(NotificationListener.ACTION_PAUSE);
            PendingIntent pausePending = PendingIntent.getService(context, 0, pauseIntent, 0);
            views.setOnClickPendingIntent(R.id.button_media_pause, pausePending);
            Intent resumeIntent = new Intent(context, PlayerService.class);
            resumeIntent.setAction(NotificationListener.ACTION_RESUME);
            PendingIntent resumePending = PendingIntent.getService(context, 0, resumeIntent, 0);
            views.setOnClickPendingIntent(R.id.button_media_play, resumePending);
            Intent nextIntent = new Intent(context, PlayerService.class);
            nextIntent.setAction(NotificationListener.ACTION_NEXT);
            PendingIntent nextPending = PendingIntent.getService(context, 0, nextIntent, 0);
            views.setOnClickPendingIntent(R.id.button_media_next, nextPending);
            Intent previousIntent = new Intent(context, PlayerService.class);
            previousIntent.setAction(NotificationListener.ACTION_PREVIOUS);
            PendingIntent previousPending = PendingIntent.getService(context, 0, previousIntent, 0);
            views.setOnClickPendingIntent(R.id.button_media_previous, previousPending);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
