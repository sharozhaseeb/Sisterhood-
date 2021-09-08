package com.sisterhood.mapsproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String ourCode = "*435763#";
        String dialedNumber = getResultData();

        if (dialedNumber.equals(ourCode)) {

            // My app will bring up, so cancel the dialer broadcast
            setResultData(null);

            //Intent to launch MainActivity
            Intent intent_to_mainActivity = new Intent(context, SplashActivity.class);
            context.startActivity(intent_to_mainActivity);

        }
    }
}
