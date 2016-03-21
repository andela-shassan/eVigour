package checkpoint.andela.evigour;

import android.os.Build;
import android.view.MenuItem;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;

import java.util.Calendar;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by andela on 12/03/2016.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)

public class MainActivityTest {
    private MainActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(MainActivity.class);
    }

    @Test
    public void testOnCreate() throws Exception {
        View view = activity.findViewById(R.id.drawer_layout);
        assertNotNull(view);
    }

    @Test
    public void testOnCreateOptionsMenu() throws Exception {
        MenuItem setting = new RoboMenuItem(R.menu.main);
        assertNotNull(setting);
    }

    @Test
    public void testOnOptionsItemSelected() throws Exception {

    }

    @Test
    public void testOnNavigationItemSelected() throws Exception {
        MenuItem drawer = new RoboMenuItem(R.menu.activity_main_drawer);
        assertNotNull(drawer);

    }

    @Test
    public void testOnClick() throws Exception {

    }

    @Test
    public void testLoadPreference() throws Exception {

    }

    @Test
    public void testOnSensorChanged() throws Exception {

    }

    @Test
    public void testOnAccuracyChanged() throws Exception {

    }

    @Test
    public void testOnBackPressed() throws Exception {

    }

    @Test
    public void testSetCalendar() throws Exception {
        Calendar calendar = activity.setCalendar(10, 30);
        assertTrue(calendar.isSet(Calendar.HOUR));
        assertTrue(calendar.isSet(Calendar.MINUTE));
    }

    @Test
    public void testSetAlarm() throws Exception {
      //  AlarmManager alarmManager = activity.setAlarm();
    }

    @Test
    public void testCancelAlarm() throws Exception {

    }
}