package checkpoint.andela.dialogs;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Build;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by andela on 10/03/2016.
 */
public class TimePickerDialog extends DialogPreference implements Preference.OnPreferenceChangeListener {

    private TimePicker timePicker;
    private static final String DEFAULT_TIME = "07:00";
    private int hour, minute;
    private String time;
    private SharedPreferences sharedPreferences;
    private String savedValue;

    public TimePickerDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setSummary(newValue.toString());
        saveTime("daily_reminder_time", newValue.toString());
        return false;
    }

    @Override
    protected View onCreateDialogView() {
        timePicker = new TimePicker(getContext());
        timePicker.setIs24HourView(true);
        String[] tim = getTime().split(":");
        int h = Integer.parseInt(tim[0]);
        int m = Integer.parseInt(tim[1]);
        timePicker.setCurrentHour(h);
        timePicker.setCurrentMinute(m);
        return timePicker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        if (restorePersistedValue) {
            setSummary(getTime());
            if (defaultValue == null) {
                time = getPersistedString(DEFAULT_TIME);
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
            setSummary(time);
            persistString(defaultValue.toString());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            hour = timePicker.getCurrentHour();
            minute = timePicker.getCurrentMinute();

            time = String.format("%02d:%02d", hour, minute);
            if (callChangeListener(time)) {
                saveTime("daily_reminder_time", time);
            }
        }
        setSummary(getTime());
    }

    public void saveTime(String key, String value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getTime() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        savedValue = sharedPreferences.getString("daily_reminder_time", DEFAULT_TIME);
        return savedValue;
    }
}
