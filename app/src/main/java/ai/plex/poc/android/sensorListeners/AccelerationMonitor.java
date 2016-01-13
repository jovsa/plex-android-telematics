package ai.plex.poc.android.sensorListeners;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;

import ai.plex.poc.android.R;
import ai.plex.poc.android.database.SnapShotContract;
import ai.plex.poc.android.database.SnapShotDBHelper;

/**
 * Created by terek on 07/01/16.
 */
public class AccelerationMonitor implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLight;
    private Context applicationContext;
    private SQLiteDatabase db;


    public AccelerationMonitor(Context context){
        this.applicationContext = context;
        this.db = new SnapShotDBHelper(applicationContext).getWritableDatabase();

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        try {

            //Check if the person is currently driving
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext);
            boolean isDriving = prefs.getBoolean("isDriving", false);

            if (isDriving) {

                ContentValues values = new ContentValues();
                values.put(SnapShotContract.AccelerationEntry.COLUMN_X, event.values[0]);
                values.put(SnapShotContract.AccelerationEntry.COLUMN_Y, event.values[1]);
                values.put(SnapShotContract.AccelerationEntry.COLUMN_Z, event.values[2]);
                values.put(SnapShotContract.AccelerationEntry.COLUMN_IS_DRIVING, String.valueOf(isDriving));
                values.put(SnapShotContract.AccelerationEntry.COLUMN_TIMESTAMP, new Date().getTime());

                long rowId = db.insert(SnapShotContract.AccelerationEntry.TABLE_NAME, null, values);

                Log.d("Record Written", String.valueOf(rowId));
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}