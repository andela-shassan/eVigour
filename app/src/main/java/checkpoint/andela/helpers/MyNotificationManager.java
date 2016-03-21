package checkpoint.andela.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import checkpoint.andela.evigour.MainActivity;
import checkpoint.andela.evigour.R;

/**
 * Created by andela on 13/03/2016.
 */
public class MyNotificationManager {

    public static void buildNotification(Context context, int counter ){
        Intent intent = new Intent(context, MainActivity.class);
        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pIntent = PendingIntent.getActivity(context, requestID, intent, flags);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setSmallIcon(R.drawable.evigour_icon);
        nBuilder.setContentTitle("eVigour");
        nBuilder.setContentText(counter + " Push ups was completed");
        nBuilder.setContentIntent(pIntent);
        nBuilder.setAutoCancel(true);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(2324, nBuilder.build());
    }

}

