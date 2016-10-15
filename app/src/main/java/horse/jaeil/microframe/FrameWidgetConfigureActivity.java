package horse.jaeil.microframe;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * The configuration screen for the {@link FrameWidget FrameWidget} AppWidget.
 */
public class FrameWidgetConfigureActivity extends Activity {

//    private static final String PREFS_NAME = "horse.jaeil.microframe.FrameWidget";
//    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String TAG = "FrameWidgetConfigure";
    private static final int REQUEST_IMAGE_GET = 1;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    View.OnClickListener mSelectClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = FrameWidgetConfigureActivity.this;

            // Request an image from any image provider
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("image/*");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_GET);
                Log.d(TAG, "Started selector activity");
            }

//            // When the button is clicked, store the string locally
//            String widgetText = mAppWidgetText.getText().toString();
//            saveTitlePref(context, mAppWidgetId, widgetText);
//
//            // It is the responsibility of the configuration activity to update the app widget
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//            FrameWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
        }
    };
    View.OnClickListener mFinishClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public FrameWidgetConfigureActivity() {
        super();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "Received Activity result");

        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
//            Bitmap thumbnail = data.getParcelableExtra("data");
            Uri fullPhotoUri = data.getData();

            Log.i(TAG, fullPhotoUri.toString());

            Drawable drawable;
            try {
                InputStream inputStream = getContentResolver().openInputStream(fullPhotoUri);
                drawable = Drawable.createFromStream(inputStream, fullPhotoUri.toString());
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Selected image was not found! Now it's going to crash...");
                drawable = null;
            }

            ImageView preview = (ImageView) findViewById(R.id.previewImageView);
//            Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
//            drawable = Drawable.createFromPath(fullPhotoUri.getPath());
            preview.setImageDrawable(drawable);

            Log.d(TAG, "Set preview image");
        }
    }

//    // Write the prefix to the SharedPreferences object for this widget
//    static void saveTitlePref(Context context, int appWidgetId, String text) {
//        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
//        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
//        prefs.apply();
//    }

//    // Read the prefix from the SharedPreferences object for this widget.
//    // If there is no preference saved, get the default from a resource
//    static String loadTitlePref(Context context, int appWidgetId) {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
//        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
//        if (titleValue != null) {
//            return titleValue;
//        } else {
//            return context.getString(R.string.appwidget_text);
//        }
//    }

//    static void deleteTitlePref(Context context, int appWidgetId) {
//        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
//        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
//        prefs.apply();
//    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.frame_widget_configure);
        findViewById(R.id.select_image_button).setOnClickListener(mSelectClickListener);
        findViewById(R.id.finish_button).setOnClickListener(mFinishClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

//        mAppWidgetText.setText(loadTitlePref(FrameWidgetConfigureActivity.this, mAppWidgetId));
    }
}

