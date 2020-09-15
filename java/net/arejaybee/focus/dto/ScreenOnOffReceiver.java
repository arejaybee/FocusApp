package net.arejaybee.focus.dto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.arejaybee.focus.Focus;

public class ScreenOnOffReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
            Focus.updatePhoneState(false);
        }
        else{
            Focus.updatePhoneState(true);
        }
    }
}
