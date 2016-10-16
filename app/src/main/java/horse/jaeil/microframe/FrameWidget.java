package horse.jaeil.microframe;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link FrameWidgetConfigureActivity FrameWidgetConfigureActivity}
 */
public class FrameWidget extends AppWidgetProvider {
    private static final String TAG = "FrameWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String uriString = FrameWidgetConfigureActivity.loadImgRef(context, appWidgetId);
        Log.d(TAG, "Widget updating with uriString=" + uriString);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.frame_widget);
        // Depending on the URI string, set the widget image
        if (!uriString.equals("")) {
            Uri uri = Uri.parse(uriString);
            views
                    .setImageViewUri(R.id.frameImage, uri);
        } else {
            views
                    .setImageViewResource(R.id.frameImage, R.drawable.frame_default);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            FrameWidgetConfigureActivity.deleteImgRef(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

