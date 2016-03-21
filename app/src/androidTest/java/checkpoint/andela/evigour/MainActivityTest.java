package checkpoint.andela.evigour;

import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

/**
 * Created by andela on 19/03/2016.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2{

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    @Test
    public void testOnCreate() throws Exception {
        onView(withText("START")).check(matches(isDisplayed()));
        onView(withText("00:05:00")).check(matches(isDisplayed()));
    }

    @Test
    public void testOnClick() throws Exception {
        onView(withText("START")).perform(click());
        onView(withText(" CANCEL")).check(matches(isDisplayed()));
        onView(withText(" CANCEL")).perform(click());
        onView(withText("START")).check(matches(isDisplayed()));
        onView(withText("00:05:00")).check(matches(isDisplayed()));
    }

    @Test
    public void testDisplayAnimation() throws Exception {
        onView(withId(R.id.gif_animation)).check(matches(not (isDisplayed())));
        onView(withText("START")).perform(click());
        onView(withId(R.id.gif_animation)).check(matches(isDisplayed()));
        onView(withText("START")).check(matches(not(isDisplayed())));
        onView(withText(" CANCEL")).check(matches(isDisplayed()));
        onView(withText(" CANCEL")).perform(click());
        onView(withText("00:05:00")).check(matches(isDisplayed()));
        onView(withId(R.id.gif_animation)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testGraphPage() throws Exception {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Settings")).check(matches(isDisplayed()));
        onView(withText("Reports")).check(matches(isDisplayed()));
        onView(withText("Reports")).perform(click());
        onView(withText("Report Graph")).check(matches(isDisplayed()));
        pressBack();
        onView(withText("START")).check(matches(isDisplayed()));
    }

    @Test
    public void testSettingsPage() throws Exception {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Settings")).check(matches(isDisplayed()));
        onView(withText("Settings")).perform(click());
        onView(withText("General")).check(matches(isDisplayed()));
        onView(withText("General")).perform(click());
        onView(withText("Training Method")).check(matches(isDisplayed()));
        onView(withText("Training Method")).perform(click());
        onView(withText("Number of Push Ups")).perform(click());
        pressBack();
        onView(withText("General")).check(matches(isDisplayed()));
        onView(withText("Notifications Tone")).check(matches(isDisplayed()));
        pressBack();
        onView(withText("10 Push ups")).check(matches(isDisplayed()));
    }

    @Test
    public void testNotificationSettingsPage() throws Exception {
        onView(withContentDescription("Open navigation drawer")).perform(click());
        onView(withText("Settings")).check(matches(isDisplayed()));
        onView(withText("Settings")).perform(click());
        onView(withText("Notifications Tone")).check(matches(isDisplayed()));
        onView(withText("Notifications Tone")).perform(click());
        onView(withText("eVigour Notification")).check(matches(isDisplayed()));
        onView(withText("eVigour Ringtone")).check(matches(isDisplayed()));

    }


}