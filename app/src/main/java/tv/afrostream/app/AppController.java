package tv.afrostream.app;

/**
 * Created by bahri on 11/01/2017.
 */

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.Locale;

import tv.afrostream.app.utils.OkHttp3Stack;
import tv.afrostream.app.activitys.MainActivity;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();



    private RequestQueue mRequestQueue;

    protected String userAgent;

    private static AppController mInstance;


    private Tracker mTracker;


    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
          //  mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();


            try {
                LoginManager.getInstance().logOut();
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;



        userAgent = Util.getUserAgent(this, "PocPlayer");

      /*  Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
                handleUncaughtException (thread, e);
                e.printStackTrace();
            }
        });*/
    }


    private void handleUncaughtException (Thread thread, Throwable e)
    {

        // The following shows what I'd like, though it won't work like this.
        Intent intent = new Intent (getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Add some code logic if needed based on your requirement
    }
    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }


    public static void setLocaleEn (Context context){
        Locale locale = new Locale("en");
        Locale.setDefault(locale);

        Configuration config = context.getApplicationContext().getResources().getConfiguration();
        config.setLocale(locale);
        context.createConfigurationContext(config);

    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {

     /*VHttpDataSourceFactory source =new VHttpDataSourceFactory(userAgent, bandwidthMeter);
        Map<String, String> map = new HashMap<String, String>();

        JSONObject js=new JSONObject ();
        try {
            js.put("userId", "androiduser");
            js.put("sessionId", "1234567454545");
            js.put("merchant", "afrostream");
        }catch ( Exception ee)
        {
            ee.printStackTrace();
        }


        byte[] data = new byte[0];
        try {
            data = js.toString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String base64 =Base64.encodeBytes(data);




        map.put("x-dt-custom-data",base64);


        source.setHeader(map);
        return source;*/
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    public boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {

            OkHttp3Stack myOkHttpStack = new OkHttp3Stack();




            mRequestQueue = Volley.newRequestQueue(getApplicationContext(),myOkHttpStack);




           // mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}