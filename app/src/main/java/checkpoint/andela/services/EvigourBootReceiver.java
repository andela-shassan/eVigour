package checkpoint.andela.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.Calendar;

/**
 * Created by andela on 18/03/2016.
 */
public class EvigourBootReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreference;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setReminder(context);
        }
    }

    public void setReminder(Context context) {
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isReminder = sharedPreference.getBoolean("daily_reminder_switch", false);

        if (isReminder) {
            setAlarm(context);
        }
    }

    @NonNull
    protected Calendar setCalendar(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    private void setAlarm(Context context) {
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String interval = sharedPreference.getString("reminder_interval", "1");
        String time = sharedPreference.getString("daily_reminder_time", "07:00");

        String[] hhmm = time.split(":");
        int hour = Integer.parseInt(hhmm[0]);
        int minute = Integer.parseInt(hhmm[1]);
        int inter = Integer.parseInt(interval);

        Calendar calendar = setCalendar(hour, minute);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            return;
        }
        AlarmManager reminder = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, EVigourBroadcastReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 2324, intent, 0);
        reminder.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                (AlarmManager.INTERVAL_DAY * inter), pIntent);
    }
}
