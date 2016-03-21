package checkpoint.andela.services;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import checkpoint.andela.helpers.EvigourHelper;

public class EVigourIntentService extends IntentService {

    String title = "eVigour Reminder";
    String message = "Hey! It's time for Push Up";

    public EVigourIntentService() {
        super("EVigourIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        EvigourHelper.buildNotification(this, title, message);
        Toast.makeText(this, "Notification Sent", Toast.LENGTH_LONG).show();
    }

}