package horse.jaeil.microframe;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * The configuration screen for the {@link FrameWidget FrameWidget} AppWidget.
 */
public class FrameWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "horse.jaeil.microframe.FrameWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private static final String TAG = "FrameWidgetConfigure";
    private static final int REQUEST_IMAGE_GET = 1;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    View.OnClickListener mSelectClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = FrameWidgetConfigureActivity.this;

            // Request an image loaded on the device from any image provider
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .putExtra(Intent.EXTRA_LOCAL_ONLY, true)
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setType("image/*");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_IMAGE_GET);
            }
        }
    };
    View.OnClickListener mFinishClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = FrameWidgetConfigureActivity.this;

            // Get the URI of the preview image
            TextView textView = (TextView) findViewById(R.id.previewUri);
            String uriString = textView.getText().toString();

            // Send an update to the widget so it can load the image
            saveImgRef(context, mAppWidgetId, uriString);
            AppWidgetManager awm = AppWidgetManager.getInstance(context);
            FrameWidget.updateAppWidget(context, awm, mAppWidgetId);

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
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) {
            // Parse the URI into a drawable
            Uri uri = data.getData();
            Drawable drawable;
            TextView previewText = (TextView) findViewById(R.id.previewUri);
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                drawable = Drawable.createFromStream(inputStream, uri.toString());
                // If the Drawable was successfully loaded, then store the URI for the widget
                previewText.setText(uri.toString());
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Selected image not found!");
                drawable = getResources().getDrawable(R.drawable.frame_default, null);
                previewText.setText("");
            }

            ImageView preview = (ImageView) findViewById(R.id.previewImageView);
            preview.setImageDrawable(drawable);
        }
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveImgRef(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadImgRef(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return "";//context.getString(R.string.appwidget_text);
        }
    }

    static void deleteImgRef(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

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
//            return;
        }
    }
}

