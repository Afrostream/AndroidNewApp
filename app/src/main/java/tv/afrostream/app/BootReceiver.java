package tv.afrostream.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import tv.afrostream.app.services.AfrostreamBootWakefulService;

/**
 * Created by bahri on 08/02/2017.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


            //Intent myIntent = new Intent(context, AfrostreamBootWakefulService.class);
            //startWakefulService(context, myIntent);

        context.startService(new Intent(context, AfrostreamBootWakefulService.class));;


    }
}
