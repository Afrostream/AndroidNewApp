package tv.afrostream.app.activitys;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.transition.Transition;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;

import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;


import tv.afrostream.app.utils.AnimationUtils;

import tv.afrostream.app.R;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 10/01/2017.
 */

public class SplashActivity extends AppCompatActivity {


    ImageView logo;

    private Toast toast;

    private void createHandler() {
        Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do Work


                    }
                }, 2000);

                Looper.loop();
            }
        };
        thread.start();
    }


    @Override
    protected void onResume() {
        super.onResume();



    }
    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       /* if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }*/

        super.onCreate(savedInstanceState);

        if (StaticVar.DevMode)
        {
            StaticVar.BaseUrl="https://afr-back-end-staging.herokuapp.com"; // dev

            StaticVar.drmLicenseUrl="https://lic.staging.drmtoday.com/license-proxy-widevine/cenc/?specConform=true"; //dev
            StaticVar.StripeKey="pk_test_s9YFHvFFIjo2gdAL5x4k2ISh"; //dev
        }else
        {
            StaticVar.BaseUrl="https://legacy-api.afrostream.tv"; // prod cdn
            // StaticVar.BaseUrl="https://afrostream-backend.herokuapp.com"; // prod sans cdn
            StaticVar.drmLicenseUrl="https://lic.drmtoday.com/license-proxy-widevine/cenc/?specConform=true"; //prod
            StaticVar.StripeKey="pk_live_Qyu5litLIYwE3ks66iBIFbQk"; //prod

        }




        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("tv.afrostream.app")) {

            // do what you want

            // and this for killing app if we dont want to start
            android.os.Process.killProcess(android.os.Process.myPid());

        }







      /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

           try {
               getWindow().setAllowReturnTransitionOverlap(true);
               Transition trans = new Explode();


               Transition returnTrans = new Slide();
               returnTrans.setDuration(2000);
               ((Slide) returnTrans).setSlideEdge(Gravity.TOP);


               getWindow().setEnterTransition(returnTrans);
               getWindow().setReturnTransition(returnTrans);

               getWindow().setExitTransition(null);
           }catch (Exception ee)
           {
               ee.printStackTrace();
           }

        }*/





        setContentView(R.layout.activity_splash);






        logo = (ImageView) this.findViewById(R.id.logo);
        //logo.setVisibility(View.VISIBLE);

        AnimationUtils.showMe(logo,300);

        AnimationUtils.rotateX(logo,500);






       Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {


            @Override
            public void run() {


                try {

                    final Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP);


                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(SplashActivity.this, SplashActivity.this.findViewById(R.id.logo), "logo");


                 //  ActivityCompat.startActivity(SplashActivity.this, intent, options.toBundle());

                   startActivity (intent);

                    SplashActivity.this.overridePendingTransition(0, 0);

                }catch (Exception e)
                {
                    e.printStackTrace();
                    final Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    ActivityCompat.startActivity(SplashActivity.this, intent, null);
                }
                finish();





            }
        }, 2000 );



    }
}
