package net.arejaybee.focus;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.arejaybee.focus.dto.User;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class Settings extends AppCompatActivity implements MessageDialogFragent.NoticeDialogListener {
    private EditText timer;
    private User user;

    private boolean messagesExpanded;

    //stuff we preserve between the activities
    private int seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    /**
     * Initialize variables. Grabs the objects from the layout
     **/
    private void init(){
        seconds = getIntent().getIntExtra("seconds", 0);
        messagesExpanded = true;
        timer = findViewById(R.id.aset_timer);
        user = User.ReadObjectFromFile(this);
        if(user == null){
            user = new User();
            user.WriteObjectToFile(this);
        }
        timer.setText(user.secondsBetweenNotifications+"");
        showLayout((LinearLayout)findViewById(R.id.messageListLayout));
        setSupportActionBar((Toolbar) findViewById(R.id.settingsToolbar));
        getSupportActionBar().setTitle("Settings");
    }

    /**
     * If the user hits their back button, close the settings page and return to main one
     **/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * When the user clicks Update, save all changes made hre and then send them to the main page
     * @param v - the Update button
     **/
    public void onClickSave(View v){
        if(timer.getText().toString().isEmpty()){
            Toast.makeText(this, "Notification Time cannot be blank.", Toast.LENGTH_LONG).show();
        }
        else{
            user.secondsBetweenNotifications = Integer.parseInt(timer.getText().toString());
            user.WriteObjectToFile(this);
            super.onBackPressed();
            finish();
        }
    }

    /**
     * When the user clicks the add button, open a fragment to get a new message from them
     * @param v - the Add button
     **/
    public void onClickAdd(View v){
        RelativeLayout layout = (RelativeLayout)v.getParent();
        MessageDialogFragent nDialog = new MessageDialogFragent();
        Bundle b = new Bundle();
        nDialog.setArguments(b);
        nDialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    /**
     * Called when the user clicks cancel from the message fragment.
     * Should just close the fragment
     * @param df - the message fragment
     */
    public void onDialogNegativeClick(MessageDialogFragent df){
        df.getDialog().cancel();
    }

    /**
     * Called when the user clicks confirm from the message fragment.
     * Should close the fragment and add their new message.
     * @param df - the message fragment
     */
    public void onDialogPositiveClick(MessageDialogFragent df) {
        user.addMessage(df.getText());
        df.getDialog().cancel();
        if(messagesExpanded){
            onClickExpand(findViewById(R.id.messageExpand));
            onClickExpand(findViewById(R.id.messageExpand));
        }
    }

    /**
     * When the user clicks the arrow button, it should either show all messages in their list, or hide them all.
     * @param v - the expand button
     */
    public void onClickExpand(View v){
        LinearLayout mainLayout = findViewById(R.id.messageListLayout);
            if(messagesExpanded){
                mainLayout.setVisibility(View.INVISIBLE);
                mainLayout.removeAllViews();
            }
            else{
                showLayout(mainLayout);
            }
            messagesExpanded = !messagesExpanded;
        if(v.getRotation() > 0){
            v.setRotation(0);
        }
        else{
            v.setRotation(180);
        }
    }

    /**
     * Shows the list of messages within a linear layout
     * @param layout - the linear layout to show the messages in
     */
    public void showLayout(LinearLayout layout){
        layout.setVisibility(View.VISIBLE);
        for(int i = 0; i < user.getAllMessages().size(); i++){
            layout.addView(generateListItem(user.getAllMessages().get(i)));
        }
    }

    /**
     * Create a View for messages in the list
     * @param message - a message from the user's message list
     * @return - a layout containing all messages
     */
    private RelativeLayout generateListItem(String message){
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50,0,0,20);
        RelativeLayout rl = new RelativeLayout(this);
        rl.setLayoutParams(lp);


        ImageButton ib = new ImageButton(this);
        ib.setImageResource(android.R.drawable.ic_menu_delete);
        ib.setBackgroundColor(Color.TRANSPARENT);
        RelativeLayout.LayoutParams blp = new RelativeLayout.LayoutParams(100,100);
        blp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ib.setLayoutParams(blp);
        ib.setPadding(18,18,18,18);
        ib.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                LinearLayout ll = (LinearLayout)v.getParent().getParent();
                int index = ll.indexOfChild((View)v.getParent());
                ll.removeView((View)v.getParent());
                user.removeMessages(index);
            }
        });

        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(ContextCompat.getColor(this, R.color.colorSubText));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        RelativeLayout.LayoutParams tvlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        tv.setPadding(0,0,blp.width+10,0);
        tvlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        rl.addView(tv);
        rl.addView(ib);

        return rl;
    }
}

