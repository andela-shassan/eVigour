package checkpoint.andela.evigour;

import android.content.Intent;
import android.content.SharedPreferences;
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
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String DEFAULT_EVIGOUR_TONE = "content://settings/system/notification_sound";
    private TextView counts;
    private Button operate;
    private ImageView gifView, bell;
    private DrawerLayout drawer;
    private Ringtone ringtone;
    private String pushUpDuration, pushUpNumber;

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

        bell = (ImageView) findViewById(R.id.alarm_bell);

        Glide.with(this).load(R.raw.p1).asGif().into(gifView);
        counts = (TextView) findViewById(R.id.count_down);
        operate = (Button) findViewById(R.id.start_btn);
        operate.setOnClickListener(this);

        ringtone = playTone();
        pushUpDuration = loadString("push_up_duration", "5");

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
        if (operate.getText().toString().startsWith("Start")) {
            String s = loadString("push_up_duration", "5");
            Toast.makeText(this, s+ " Minute(s) Push Ups", Toast.LENGTH_LONG).show();
            operate.setVisibility(View.GONE);
            gifView.setVisibility(View.VISIBLE);
            int k = Integer.parseInt(s);
            countDown(k);
        } else if (operate.getText().toString().startsWith("Stop")) {
            //playTone().stop();
            ringtone.stop();
            operate.setText("Start");
        }

    }
    private void countDown(final int i) {
        new CountDownTimer(i * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String time = timeFormatter(millisUntilFinished);
                counts.setText(time);
            }

            @Override
            public void onFinish() {
                counts.setText("DONE!");
                gifView.setVisibility(View.GONE);
                operate.setText("Stop");
                operate.setVisibility(View.VISIBLE);
                ringtone.play();
            }
        }.start();
    }

    private Ringtone playTone() {
        Uri ringtone = Uri.parse(loadString("evigour_notifications_tone", DEFAULT_EVIGOUR_TONE));
        Ringtone r = RingtoneManager.getRingtone(this, ringtone);
        return r;
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
        if(ringtone.isPlaying()){
            ringtone.stop();
            Log.d("semiu", String.valueOf(ringtone.isPlaying()));
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        super.onBackPressed();
    }
}
