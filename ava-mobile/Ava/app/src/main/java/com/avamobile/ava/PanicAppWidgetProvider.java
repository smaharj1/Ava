package com.avamobile.ava;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Random;

/**
 * Created by zcv0lhb on 1/28/2017.
 */
public class PanicAppWidgetProvider extends AppWidgetProvider {

    private static final String PANIC_CLICKED = "automaticWidgetPanicButtonClick";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews;
        ComponentName watchWidget;
        System.out.print("PRESSED");
        remoteViews = new RemoteViews(context.getPackageName(), R.layout.panic_appwidget);
        watchWidget = new ComponentName(context, PanicAppWidgetProvider.class);

        remoteViews.setOnClickPendingIntent(R.id.panicButton, getPendingSelfIntent(context, PANIC_CLICKED));
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);

        System.out.println("PRINT SOMETHING");
        if (PANIC_CLICKED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            System.out.print("PANIC PRESSED");

            Intent in = new Intent (context, PanicActivity.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.panic_appwidget);
            watchWidget = new ComponentName(context, PanicAppWidgetProvider.class);

            remoteViews.setTextViewText(R.id.count_down, "TESTING");

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}
