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
public class EvigourHelper {

    public static void buildNotification(Context context, String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("notification", 1);
        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        PendingIntent pIntent = PendingIntent.getActivity(context, requestID, intent, flags);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context);
        nBuilder.setSmallIcon(R.drawable.evigour_icon);
        nBuilder.setContentTitle(title);
        nBuilder.setContentText(message);
        nBuilder.setContentIntent(pIntent);
        nBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
        nBuilder.setAutoCancel(true);
        nManager.notify(2324, nBuilder.build());
    }

}

