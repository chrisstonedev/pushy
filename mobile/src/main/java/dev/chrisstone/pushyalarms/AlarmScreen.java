package dev.chrisstone.pushyalarms;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AlarmScreen extends AppCompatActivity {

    private static final int WAKELOCK_TIMEOUT = 60 * 1000;
    public final String TAG = this.getClass().getSimpleName();
    private WakeLock mWakeLock;
    private MediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup layout
        this.setContentView(R.layout.activity_alarm_screen);

        String name = getIntent().getStringExtra(AlarmManagerHelper.NAME);
        int timeHour = getIntent().getIntExtra(AlarmManagerHelper.TIME_HOUR, 0);
        int timeMinute = getIntent().getIntExtra(AlarmManagerHelper.TIME_MINUTE, 0);
        String tone = getIntent().getStringExtra(AlarmManagerHelper.TONE);

        TextView tvName = findViewById(R.id.alarm_screen_name);
        tvName.setText(name);

        TextView tvTime = findViewById(R.id.alarm_screen_time);
        tvTime.setText(String.format(Locale.getDefault(), "%02d : %02d", timeHour, timeMinute));

        Button dismissButton = findViewById(R.id.alarm_screen_button);
        dismissButton.setOnClickListener(view -> {
            mPlayer.stop();
            finish();
        });

        //Play alarm tone
        mPlayer = new MediaPlayer();
        try {
            if (tone != null && !tone.equals("")) {
                Uri toneUri = Uri.parse(tone);
                mPlayer.setDataSource(this, toneUri);
                mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mPlayer.setLooping(true);
                mPlayer.prepare();
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Ensure wakelock release
        Runnable releaseWakelock = () -> {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

            if (mWakeLock != null && mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        };

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
            Log.i(TAG, "Wakelock acquired!!");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }
}