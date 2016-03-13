package checkpoint.andela.evigour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SensorEventListener {
    private static final String DEFAULT_EVIGOUR_TONE = "content://settings/system/notification_sound";
    private TextView countView;
    private Button start_btn, cancel_btn, done_btn;
    private ImageView gifView;
    private DrawerLayout drawer;
    private Ringtone ringtone;
    private String trainingMethod, pushUpDuration, pushUpNumber;
    private Sensor proximity;
    private SensorManager sensorManager;
    private int number;
    private CountDownTimer countDownTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gifView = (ImageView) findViewById(R.id.gif_animation);
        gifView.setScaleX(0.75f);
        gifView.setScaleY(0.75f);

        Glide.with(this).load(R.raw.p1).asGif().into(gifView);
        countView = (TextView) findViewById(R.id.count_down);
        start_btn = (Button) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(this);
        cancel_btn = (Button) findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(this);
        done_btn = (Button) findViewById(R.id.done_btn);
        done_btn.setOnClickListener(this);

        ringtone = playTone();

        trainingMethod = loadString("training_method_list", "0");
        pushUpDuration = loadString("push_up_duration", "5");
        pushUpNumber = loadString("push_count", "10");
        Log.d("semiu", " TM " + trainingMethod + " PN " + pushUpNumber + " PD " + pushUpDuration);

        //sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        checkSetting();
        //listSensors();

    }

    private void checkSetting() {
        trainingMethod = loadString("training_method_list", "0");
        if (trainingMethod.contains("1")) {

            countView.setText(pushUpNumber + " Push ups");
        } else {
            int i = Integer.parseInt(pushUpDuration);
            String t = timeFormatter(i * 60 * 1000);
            countView.setText(t);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_report:
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                drawer.closeDrawer(GravityCompat.START);
                return true;
            case R.id.nav_help:
                drawer.closeDrawer(GravityCompat.START);
                return true;
            default:
                drawer.closeDrawer(GravityCompat.START);
                return true;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int duration;
        switch (id) {
            case R.id.start_btn:
                //if (start_btn.getText().toString().startsWith("Start")) {
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                sensorManager.registerListener(this, proximity, sensorManager.SENSOR_DELAY_NORMAL);
                if (!countView.getText().toString().contains("Push")) {
                    String time = loadString("push_up_duration", "5");
                    Toast.makeText(this, time + " Minute(s) Push Ups", Toast.LENGTH_LONG).show();
                    start_btn.setVisibility(View.GONE);
                    gifView.setVisibility(View.VISIBLE);
                    cancel_btn.setVisibility(View.VISIBLE);
                    duration = Integer.parseInt(time);
                    countDown(duration).start();
                    // } else if (start_btn.getText().toString().startsWith("Done")) {
                    //ringtone.stop();
                    //checkSetting();
                    //start_btn.setText("Start");
                } else {
                    String number = loadString("push_count", "10");
                    Toast.makeText(this, number + " Push Ups", Toast.LENGTH_LONG).show();
                    start_btn.setVisibility(View.GONE);
                    gifView.setVisibility(View.VISIBLE);
                    cancel_btn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.cancel_btn:
                countDownTimer.cancel();
                cancel_btn.setVisibility(View.GONE);
                start_btn.setVisibility(View.VISIBLE);
                gifView.setVisibility(View.GONE);
                sensorManager.unregisterListener(this);
                checkSetting();
                break;
            case R.id.done_btn:
                ringtone.stop();
                done_btn.setVisibility(View.GONE);
                sensorManager.unregisterListener(this);
                checkSetting();
                start_btn.setVisibility(View.VISIBLE);
                break;
        }
    }

    private CountDownTimer countDown(final int i) {
        countDownTimer = new CountDownTimer(i * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = timeFormatter(millisUntilFinished);
                countView.setText(time);
            }

            @Override
            public void onFinish() {
                countView.setText("00:00:00");
                gifView.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.GONE);
                done_btn.setVisibility(View.VISIBLE);
                ringtone.play();
                sensorManager.unregisterListener(MainActivity.this);
            }
        };
        return countDownTimer;
    }

    private Ringtone playTone() {
        Uri ringtone = Uri.parse(loadString("evigour_notifications_tone", DEFAULT_EVIGOUR_TONE));
        return RingtoneManager.getRingtone(this, ringtone);
    }

    public String loadString(String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString(key, defaultValue);
    }

    private String timeFormatter(long milliseconds) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void onBackPressed() {
        if (ringtone.isPlaying()) {
            ringtone.stop();
            Log.d("semiu", String.valueOf(ringtone.isPlaying()));
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        sensorManager.registerListener(this, proximity, sensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        //sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0) {
            number++;
            Toast.makeText(this, "Proximity " + number, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

}
