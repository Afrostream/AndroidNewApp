package tv.afrostream.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by bahri on 09/02/2017.
 */


public class AfrostreamServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("AfrostreamBootService", "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, AfrostreamBootWakefulService.class));;
    }
}