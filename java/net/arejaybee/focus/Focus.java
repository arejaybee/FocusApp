package net.arejaybee.focus;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.arejaybee.focus.dto.Messages;
import net.arejaybee.focus.dto.User;

import java.util.Date;
import java.util.Locale;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Focus extends AppCompatActivity {

    public static boolean phoneOn = true;
    public static boolean phoneWasOff = false;
    private TextView tvTimer;
    private Button bFocus;
    private LinearLayout buttonLayout;
    private User user;
    private int seconds = 0;
    private int offlineSeconds = 0;
    private boolean running = false; //if the timer is ticking up
    private boolean wasRunning = false; //if the timer was running before the app was paused
    private boolean inApp = false; //gets set to true when we go to another activity, which pauses this one
    private Date lastActive;

    Messages messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        init();
        runTimer();
    }

    /**
     * Called when the app is paused.
     * They should begin getting notifications after this is called, unless the screen was just shut off.
     **/
    protected void onPause(){
        super.onPause();
        if(!inApp) {
            lastActive = new Date();
            wasRunning = running;
            running = false;
        }
    }

    /**
     * Called when the app was paused, but has resumed. Should stop the barrage of notifications that hit the user while they left the app.
     **/
    protected void onResume(){
        super.onResume();
        if(wasRunning && phoneOn){
            running = true;
            wasRunning = false;
            if(phoneWasOff) {
                Date d = new Date();
                int addedSeconds = (int) (d.getTime() - lastActive.getTime()) / 1000;
                seconds += addedSeconds;
                phoneWasOff = false;
            }
        }
        if(inApp){
            user = User.ReadObjectFromFile(this);
        }
        inApp = false; //Set this to false, so that in any cases we pause, we can set it later.\
    }

    /**
     * Tracks if the screen is off or on.
     * @param on - true if the screen is on
     **/
    public static void updatePhoneState(boolean on){
        if(phoneOn != on) {
            phoneOn = on;
            if (phoneOn) {
                phoneWasOff = true;
            } else {
                phoneWasOff = false;
            }
        }
    }

    /**
     * Start the timer. While timer is going, user will need to remain in this app, or get bombarded with notifications telling them to remain focused.
     * @param v - the Focus button
     **/
    public void onFocusClick(View v){
        Button focus = (Button)v;
        if(focus.getVisibility() == View.VISIBLE){
            focus.setVisibility(View.INVISIBLE);
            buttonLayout.setVisibility(LinearLayout.VISIBLE);
            running = true;
        }
    }

    /**
     * Restarts the timer. Completely restarts the users timer, but keeps them in a focused state.
     * @param v - the restart button
     **/
    public void onRestartClick(View v){
        running = false;
        seconds = 0;
        tvTimer.setText("00:00:00");
        bFocus.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * When the user clicks pause, stop the timer.
     * User should not get notifications while in a paused state
     *
     * When the user clicks resume, start timer again.
     * @param v - the Pause/Resume button
     **/
    public void onPauseClick(View v){
        Button pause = (Button)v;
        if(pause.getText().toString().equals("PAUSE")){
            running = false;
            pause.setText("RESUME");
        }
        else{
            running = true;
            pause.setText("PAUSE");
        }
    }

    /**
     * Creates the menu in the toolbar
     * @param menu - the menu to create
     * @return - always returns true for now. May want to add error handling in the future for a false case
     **/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.header_buttons, menu);
        return true;
    }

    /**
     * Captures heading menu clicks.
     * @param item - the button in the toolbar that was clicked
     * @return - returns true if a custom button was clicked, otherwise calls the base class function
     **/
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_settings:
                onSettingsClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Initialize variables. Grabs the objects from the layout
     **/
    private void init(){
        if(User.ReadObjectFromFile(this) == null){
            user = new User();
            user.WriteObjectToFile(this);
        }
        else {
            user = User.ReadObjectFromFile(this);
        }

        setSupportActionBar((Toolbar) findViewById(R.id.my_toolbar));
        tvTimer = findViewById(R.id.timer);
        bFocus = findViewById(R.id.focusButton);
        buttonLayout = findViewById(R.id.buttonLayout);

        seconds = getIntent().getIntExtra("seconds", 0);

        if(seconds > 0){
            buttonLayout.setVisibility(LinearLayout.VISIBLE);
            bFocus.setVisibility(View.INVISIBLE);
            running = true;
            wasRunning = false;
        }

        messages = new Messages();
    }

    /**
     * Sends the user to the settings menu
     **/
    private void onSettingsClick(){
        Intent intent = new Intent(this, Settings.class);
        intent.putExtra("seconds", seconds);
        inApp = true;
        startActivity(intent);
    }

    /**
     * Runs the timer on a separate thread. This timer will tick every second, given that 'running' is true.
     * When the app is running and the phone is not the main focus for N seconds, then the user gets a notification
     **/
    private void runTimer(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds / 60)%60;
                int sec = seconds % 60;
                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, sec);
                if (running) {
                    tvTimer.setText(time);
                    seconds++;
                }
                else if(!running && wasRunning && phoneOn){
                    if(offlineSeconds >= user.secondsBetweenNotifications){
                        offlineSeconds = 0;
                        sendNotification(user.getMessage());
                    }
                    else {
                        offlineSeconds++;
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    /**
     * Sends notifications to the user when they get distracted
     * @param message - the message to send ot the user
     **/
    private void sendNotification(String message){

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, Focus.class);
        resultIntent.putExtra("notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_android)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}
