package com.ingenuityapps.android.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;

import com.ingenuityapps.android.bakingapp.data.Recipe;
import com.ingenuityapps.android.bakingapp.utilities.JsonUtils;
import com.ingenuityapps.android.bakingapp.utilities.NetworkUtils;

import java.net.URL;
import java.util.List;

import static com.ingenuityapps.android.bakingapp.MainActivity.APP_PREFERENCES;
import static com.ingenuityapps.android.bakingapp.MainActivity.RECIPE_NAME_PREF;

/**
 * Implementation of App Widget functionality.
 */
public class BakingIngredientsWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        updateBakingWidgets(context,appWidgetManager,appWidgetIds);

        super.onUpdate(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = getBakingIngredientsRemoteView(context);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private RemoteViews getBakingIngredientsRemoteView(Context context)
    {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_ingredients_widget);

        SharedPreferences preferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        String prefRecipeName = preferences.getString(RECIPE_NAME_PREF,null);

        views.setTextViewText(R.id.tv_widget_title, (prefRecipeName != null ? prefRecipeName + " - ":"") + "Ingredients");

        Intent intent = new Intent(context,BakingIngredientsWidgetService.class);
        views.setRemoteAdapter(R.id.gv_ingredients, intent);

        views.setEmptyView(R.id.gv_ingredients,R.id.empty_view);

        Intent appIntent = new Intent(context, DetailActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.gv_ingredients, appPendingIntent);

        return views;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final String action = intent.getAction();

        if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        {
            ComponentName componentName = new ComponentName(context, BakingIngredientsWidgetProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(componentName),R.id.gv_ingredients);
            int[] appWidgetIds = mgr.getAppWidgetIds(componentName);
            updateBakingWidgets(context,mgr,appWidgetIds);
        }

        super.onReceive(context, intent);
    }

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, BakingIngredientsWidgetProvider.class));
        context.sendBroadcast(intent);
    }

}

