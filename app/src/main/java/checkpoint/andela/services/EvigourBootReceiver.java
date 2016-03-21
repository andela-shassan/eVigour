package checkpoint.andela.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andela on 18/03/2016.
 */
public class EvigourBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Intent alarmIntent = new Intent(context, EVigourBroadcastReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2324, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }
}
