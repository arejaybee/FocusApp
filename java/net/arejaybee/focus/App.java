package net.arejaybee.focus;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;

import net.arejaybee.focus.dto.ScreenOnOffReceiver;

public class App extends Application {
    public static final String CHANNEL_ID = "exampleServiceChannel";

    public void onCreate(){
        super.onCreate();
        createNotificationChannel();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new ScreenOnOffReceiver(), filter);
    }

    /**
     * Set up a channel for notifications to use.
     * Required to stream notifications to the user.
     **/
    private void createNotificationChannel(){
        //must be Oreo or higher
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
