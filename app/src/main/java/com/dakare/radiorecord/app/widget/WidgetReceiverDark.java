package com.dakare.radiorecord.app.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import com.dakare.radiorecord.app.R;

public class WidgetReceiverDark extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        RemoteViews views = WidgetListener.buildWidget(context, R.layout.widget_dark);
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
