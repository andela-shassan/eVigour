package checkpoint.andela.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

public class EVigourBroadcastReceiver extends BroadcastReceiver {

    public EVigourBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        String prefTime = sharedPreference.getString("daily_reminder_time", "07:00");
        Calendar calendar = Calendar.getInstance();

        String[] hhmm = prefTime.split(":");
        int hour = Integer.parseInt(hhmm[0]);
        int minute = Integer.parseInt(hhmm[1]);

        int calendarHour = calendar.get(Calendar.HOUR);
        if (calendar.get(Calendar.AM_PM) == Calendar.PM){
            calendarHour += 12;
        }

        if (calendarHour == hour && calendar.get(Calendar.MINUTE) == minute) {
            context.startService(new Intent(context, EVigourIntentService.class));
        }

    }
}