package checkpoint.andela.evigour;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.concurrent.TimeUnit;

import checkpoint.andela.db.PushUpRecordDB;
import checkpoint.andela.graph.ReportGraph;
import checkpoint.andela.helpers.MyNotificationManager;
import checkpoint.andela.helpers.SettingsActivity;
import checkpoint.andela.model.PushUpRecord;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SensorEventListener {
    private static final String DEFAULT_EVIGOUR_TONE = "content://settings/system/notification_sound";
    private TextView countView;
    private Button start_btn, cancel_btn, done_btn;
    private ImageView gifView;
    private DrawerLayout drawer;
    private Ringtone ringtone;
    private boolean isCounting;
    private String trainingMethod, pushUpDuration, pushUpNumber;
    private Sensor proximity;
    private SensorManager sensorManager;
    private int pushUpCounter, pushUpRemaining, pushMade;
    private CountDownTimer countDownTimer = null;
    private SQLiteDatabase db;
    private PushUpRecordDB recordDB;
    private PushUpRecord pushUpRecord, p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
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

        trainingMethod = loadPreference("training_method_list", "0");
        pushUpDuration = loadPreference("push_up_duration", "5");
        pushUpNumber = loadPreference("push_count", "10");

        ringtone = getTone();

        recordDB = new PushUpRecordDB(this);
        db = recordDB.getReadableDatabase();
        p = new PushUpRecord();
        checkSetting();
    }

    private void checkSetting() {
        trainingMethod = loadPreference("training_method_list", "0");
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_report:
                startActivity(new Intent(this, ReportGraph.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.start_btn:
                pushMade = 0;
                registerSensor();
                startPushUp();
                break;
            case R.id.cancel_btn:
                if (isCounting) {
                    countDownTimer.cancel();
                }
                gifView.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.GONE);
                setHomeView();
                pushUpCounter = 0;
                break;
            case R.id.done_btn:
                ringtone.stop();
                done_btn.setVisibility(View.GONE);
                saveRecord(pushMade);
                setHomeView();
                break;
        }
    }

    private void saveRecord(int pushMade) {
        pushUpRecord = cupboard().withDatabase(db).query(PushUpRecord.class).withSelection("date = ?", p.getDate()).get();
        if (pushUpRecord != null) {
            pushUpRecord.setNumberOfPushUp(pushUpRecord.getNumberOfPushUp() + pushMade);
        } else {
            pushUpRecord = new PushUpRecord();
            pushUpRecord.setNumberOfPushUp(pushMade);
        }
        cupboard().withDatabase(db).put(pushUpRecord);
        MyNotificationManager.buildNotification(this, pushUpRecord.getNumberOfPushUp());
    }

    private void registerSensor() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, proximity, sensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setHomeView() {

        start_btn.setVisibility(View.VISIBLE);
        sensorManager.unregisterListener(this);
        checkSetting();
    }

    private void startPushUp() {
        int duration;
        if (!countView.getText().toString().contains("Push")) {
            String time = loadPreference("push_up_duration", "5");
            setButton(View.GONE, View.VISIBLE);
            duration = Integer.parseInt(time);
            countDown(duration).start();
        } else {
            String number = loadPreference("push_count", "10");
            setButton(View.GONE, View.VISIBLE);
            setCountView(number);
        }
    }

    private void setButton(int gone, int visible) {
        start_btn.setVisibility(gone);
        gifView.setVisibility(visible);
        cancel_btn.setVisibility(visible);
    }

    private void setCountView(String number) {
        if (!isCounting) {
            pushUpRemaining = Integer.parseInt(number);
            pushUpRemaining -= pushUpCounter;
            String k = String.valueOf(pushUpRemaining);
            countView.setText(k);
            if (pushUpRemaining == 0) {
                setButtonsDone();
                ringtone.play();
                sensorManager.unregisterListener(this);
                pushUpCounter = 0;
            }
        }
    }

    private void setButtonsDone() {
        gifView.setVisibility(View.GONE);
        cancel_btn.setVisibility(View.GONE);
        done_btn.setVisibility(View.VISIBLE);
    }

    private CountDownTimer countDown(final int i) {
        isCounting = true;
        countDownTimer = new CountDownTimer(i * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = timeFormatter(millisUntilFinished);
                countView.setText(time);
            }

            @Override
            public void onFinish() {
                setButtonsDone();
                ringtone.play();
                countView.setText("00:00:00");
                sensorManager.unregisterListener(MainActivity.this);
            }
        };
        return countDownTimer;
    }

    private Ringtone getTone() {
        Uri ringtone = Uri.parse(loadPreference("evigour_notifications_tone", DEFAULT_EVIGOUR_TONE));
        return RingtoneManager.getRingtone(this, ringtone);
    }

    public String loadPreference(String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString(key, defaultValue);
    }

    private String timeFormatter(long milliseconds) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) % TimeUnit.MINUTES.toSeconds(1));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 0) {
            pushUpCounter++;
            pushMade = pushUpCounter;
            setCountView(pushUpNumber);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            minimize();
        }
    }

    private void minimize() {
        ringtone.stop();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
