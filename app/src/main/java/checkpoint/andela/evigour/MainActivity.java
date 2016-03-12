package checkpoint.andela.evigour;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private boolean counnting;
    private TextView counts;
    private Button operate;
    private ImageView gifView, bell;
    private DrawerLayout drawer;

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

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        switch (id){
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
        String s = loadString("push_up_duration", "5");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        operate.setVisibility(View.GONE);
        gifView.setVisibility(View.VISIBLE);
        int k = Integer.parseInt(s);
        countDown(k);

    }

    private void countDown(final int i) {
        new CountDownTimer(i*60*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String rem = String.format("%02d:%02d:%02d", ((millisUntilFinished / 1000)/3600)%24, ((millisUntilFinished / 1000)/60)%60, (millisUntilFinished / 1000)/(i%60)%60);
                counts.setText(rem);
            }

            @Override
            public void onFinish() {
                counts.setText("DONE!");
                gifView.setVisibility(View.GONE);
                operate.setVisibility(View.VISIBLE);
                bell.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public String loadString(String key, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(key, defaultValue);
        return savedValue;
    }
}
