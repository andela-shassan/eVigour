package checkpoint.andela.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import checkpoint.andela.evigour.R;

/**
 * Created by andela on 10/03/2016.
 */
public class NumberPickerDialog extends DialogPreference implements Preference.OnPreferenceChangeListener, NumberPicker.OnValueChangeListener {
    private static final String NUMBER_PICKER_DEFAULT = "10";
    private NumberPicker numberPicker;
    private SharedPreferences sharedPreferences;
    private String savedValue;

    public NumberPickerDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.number_picker);
        setOnPreferenceChangeListener(this);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        int pno = numberPicker.getValue();
        if (positiveResult) {
            String push_number = String.valueOf(pno);
            if (callChangeListener(push_number)) {
                saveString("push_count", push_number);
            }
        }
        setSummary(pno + " Push Ups");
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            setSummary(loadString() + " Push Ups");
        } else {
            setSummary(defaultValue.toString() + " Push Ups");
            persistString(defaultValue.toString());
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        setSummary(newValue.toString() + " Push Ups");
        saveString("push_count", newValue.toString());
        return false;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }

    protected Object onGetDefaultValue(TypedArray array, int index) {
        return array.getString(index);
    }

    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();
        numberPicker = (NumberPicker) view.findViewById(R.id.number_picker);
        numberPicker.setMaxValue(300);
        numberPicker.setMinValue(5);
        numberPicker.setValue(Integer.parseInt(loadString()));
        return view;
    }

    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    public void saveString(String key, String value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String loadString() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        savedValue = sharedPreferences.getString("push_count", NUMBER_PICKER_DEFAULT);
        return savedValue;
    }
}
