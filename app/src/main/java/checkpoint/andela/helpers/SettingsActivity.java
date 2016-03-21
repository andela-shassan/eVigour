package checkpoint.andela.helpers;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import checkpoint.andela.dialogs.NumberPickerDialog;
import checkpoint.andela.dialogs.TimePickerDialog;
import checkpoint.andela.evigour.MainActivity;
import checkpoint.andela.evigour.R;


public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {

                if (TextUtils.isEmpty(stringValue)) {
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        preference.setSummary(null);
                    } else {
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else if (preference.getKey().matches("push_up_duration")) {
                preference.setSummary(stringValue + " Minutes");
            } else if (preference.getKey().matches("reminder_interval")) {
                preference.setSummary(stringValue + " Day(s)");
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };
    private Toolbar toolbar;

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout parent = (LinearLayout) findViewById(android.R.id.list)
                .getParent().getParent().getParent();
        toolbar = (Toolbar) LayoutInflater.from(this)
                .inflate(R.layout.toolbar, parent, false);

        toolbar.setNavigationIcon(R.drawable.back_setting);
        parent.addView(toolbar, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitle(getTitle());
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        private ListPreference lPref;
        private NumberPickerDialog nPref;
        private EditTextPreference duration, interval;
        private TimePickerDialog cPref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("push_up_duration"));
            bindPreferenceSummaryToValue(findPreference("training_method_list"));

            lPref = (ListPreference) findPreference("training_method_list");
            nPref = (NumberPickerDialog) findPreference("number_of_push_up");
            nPref.setSummary(nPref.loadString() + " Push Ups");
            duration = (EditTextPreference) findPreference("push_up_duration");
            duration.setSummary(duration.getText() + " Minutes");
            interval = (EditTextPreference) findPreference("reminder_interval");
            interval.setSummary(interval.getText() + " Day(s)");
            cPref = (TimePickerDialog) findPreference("push_daily_reminder");
            cPref.setSummary(cPref.getTime() + " Daily");

            runnable.run();
        }


        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference instanceof ListPreference) {
                if (((ListPreference) preference).getValue().matches("1")) {
                    duration.setEnabled(false);
                    nPref.setEnabled(true);
                } else {
                    nPref.setEnabled(false);
                    duration.setEnabled(true);
                }
            }
            bindPreferenceSummaryToValue(findPreference("reminder_interval"));
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                onPreferenceTreeClick(getPreferenceScreen(), getPreferenceManager().findPreference("training_method_list"));
                handler.postDelayed(this, 500);
            }
        };

        @Override
        public void onPause() {
            handler.removeCallbacks(runnable);
            super.onPause();
        }

        @Override
        public void onStop() {
            handler.removeCallbacks(runnable);
            super.onStop();
        }

        @Override
        public void onDestroy() {
            handler.removeCallbacks(runnable);
            super.onDestroy();
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);
            bindPreferenceSummaryToValue(findPreference("evigour_notifications_tone"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getTitle().toString().startsWith("Setting")) {
            EvigourHelper.launch(this, MainActivity.class);
        } else {
            EvigourHelper.launch(this, SettingsActivity.class);
        }
        finish();
    }
}
