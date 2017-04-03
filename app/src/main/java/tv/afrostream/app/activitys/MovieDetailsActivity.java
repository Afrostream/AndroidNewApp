package tv.afrostream.app.activitys;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.IntroductoryOverlay;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import tv.afrostream.app.services.DownloadFileService;
import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.models.SerieItemModel;
import tv.afrostream.app.adapters.SerieSaisonListAdapter;
import tv.afrostream.app.models.SerieSaisonModel;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by bahri on 17/01/2017.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    String ImageUrlBig="";
    String coverImageUrl="";
    public  String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;


        if(size < sizeMo)
            return df.format(size / sizeKb)+ " Kb";
        else if(size < sizeGo)
            return df.format(size / sizeMo) + " Mo";
        else if(size < sizeTerra)
            return df.format(size / sizeGo) + " Go";

        return "";
    }




    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MovieDetailsActivity.this);
            pDialog.setMessage("Downloading file. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;

            InputStream input=null;
            OutputStream output=null;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                 input = new BufferedInputStream(url.openStream(),
                        8192);


                final String filename="afro_local_trailer_"+movieID;

                String pathVideo = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;

                File dir = new File(pathVideo);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                pathVideo +=filename+ ".afv";


                String pathout =pathVideo;

                // Output stream
                 output = new FileOutputStream(pathout);





                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error Download Url: ", e.getMessage());
            }finally {

                try{
                    output.close();
                    input.close();
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            final String filename="afro_local_trailer_"+movieID;

            String pathVideo = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;

            File dir = new File(pathVideo);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            pathVideo +=filename+ ".afv";

            pDialog.dismiss();
            readVideoFromFile (pathVideo,false);


        }

    }


    public void readVideoFromFile(String fpathOut,Boolean isWebLink)
    {
        String extension = "mp4";
        String name=titleMovie;
        UUID drmSchemeUuid= null;
        try {

                drmSchemeUuid = getDrmUuid("");
        } catch (ParserException e) {
            e.printStackTrace();
        }


        String drmLicenseUrl="";



        String[] drmKeyRequestProperties=null;

        ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();

        drmKeyRequestProperties = drmKeyRequestPropertiesList.toArray(new String[0]);
        String uri="";
        if (isWebLink==false)

         uri="file:///"+fpathOut;
        else
             uri=fpathOut;

        playerPosition=0;



        Sample smp=new UriSample( name,videoID,  drmSchemeUuid,  drmLicenseUrl,   drmKeyRequestProperties,  false,uri,videoSmoothUrl,ImageUrl,extension,playerPosition,movieType) ;
        Intent nb=smp.buildIntent(getApplicationContext());
        startActivity(nb);
    }

    private UUID getDrmUuid(String typeString) throws ParserException {
        switch (typeString.toLowerCase()) {
            case "widevine":
                return C.WIDEVINE_UUID;
            case "playready":
                return C.PLAYREADY_UUID;
            default:
                try {
                    return UUID.fromString(typeString);
                } catch (RuntimeException e) {
                    throw new ParserException("Unsupported drm type: " + typeString);
                }
        }
    }

    private abstract static class Sample {

        public final String name;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;

        public Sample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
            this.name = name;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;
        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
            if (drmSchemeUuid != null) {
                intent.putExtra(PlayerActivity.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
                intent.putExtra(PlayerActivity.DRM_LICENSE_URL, drmLicenseUrl);
                intent.putExtra(PlayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
                intent.putExtra(PlayerActivity.VIDEO_NAME, name);
            }
            return intent;
        }

    }

    private static final class UriSample extends Sample {

        public final String uri;
        public final String uriSmooth;
        public final String imageurl;
        public final String extension;
        public final String Video_ID;
        public final int playerPosition;
        public final String video_type;


        public UriSample(String name,String Video_ID,UUID drmSchemeUuid, String drmLicenseUrl,
                         String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,String uriSmooth,String imageurl,
                         String extension,int playerPosition,String video_type) {
            super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
            this.uri = uri;
            this.extension = extension;
            this.uriSmooth=uriSmooth;
            this.imageurl=imageurl;
            this.Video_ID=Video_ID;
            this.playerPosition=playerPosition;
            this.video_type=video_type;
        }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra("uriSmooth",uriSmooth)
                    .putExtra("imageUrl",imageurl)
                    .putExtra("Video_ID",Video_ID)
                    .putExtra("playerPosition",playerPosition)
                    .putExtra("video_type",video_type)
                    .putExtra(PlayerActivity.EXTENSION_EXTRA, extension)
                    .setAction(PlayerActivity.ACTION_VIEW);
        }

    }

    String ImageUrl="";
    String titleMovie="";
    String labelMovie="";
    String movieDescription="";
    String movieDuration="";
    String movieSchedule="";
    String movieCat="";
    String movieID="";
    String movieType="";
    String videoID="";

    String videoDashUrl="";
    String videoHlsUrl="";
    String videoSmoothUrl="";
    String videoMp4DownloadUrl="";
    String videoMp4Trailer="";
    int videoDuration=0;

    Boolean isFavoris=false;
    Boolean videoDRM=false;
ProgressBar loading_spinner;

    ImageView bntPlay;
    TextView txtsubscribe;
    TextView favButton;
    ImageView imgFavButton;
    ImageView bntDownload;
    public ImageView imgMovie;

    JSONObject movieInfo=null;

    public int playerPosition=0;


    public String  TAG=MovieDetailsActivity.class.getSimpleName();
    private Toast toast;

    private CastContext mCastContext;
    private IntroductoryOverlay mIntroductoryOverlay;
    private CastStateListener mCastStateListener;
    private MenuItem mediaRouteMenuItem;
    RelativeLayout layoutTrailer;

    String sourceMp4Size="";
    String sourceMp4DecipheredSize="";

    public SharedPreferences sharedpreferences;
    protected AppController app;

    private CastSession mCastSession;
    private SessionManagerListener<CastSession> mSessionManagerListener;

ProgressBar positionvideo;
    ImageView imgbntTrailer;
    TextView bntTrailer;

    Timer timerRefreshToken;
    TimerTask timerTask;

    final Handler handlerRefreshToken = new Handler();




    public void startTimer() {

        try {
            stoptimertask();
            timerRefreshToken = new Timer();


            initializeTimerTask();


            timerRefreshToken.schedule(timerTask, 50, 10000); //
        }catch (Exception ee)
        {
            ee.getStackTrace();
        }
    }

    public void stoptimertask() {
        try {

            if (timerRefreshToken != null) {
                timerRefreshToken.cancel();
                timerRefreshToken = null;
            }
        }catch (Exception ee)
        {
            ee.getStackTrace();
        }
    }

    public Boolean IfTokenExpire()
    {

        if (!StaticVar.date_token.equals("") && !StaticVar.expires_in.equals("")) {
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                Date date_t = sdf.parse(StaticVar.date_token);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_t);
                int second = Integer.parseInt(StaticVar.expires_in);
                // calendar.add(Calendar.SECOND, second);
                Date dt = calendar.getTime();
                Date date_Now = new Date();
                long second_diff = TimeUnit.MILLISECONDS.toSeconds((date_Now.getTime() - dt.getTime()) );

                second_diff+=7200;
                if (second_diff > second) {
                    return true;
                } else {
                    return false;
                }


            } catch (Exception ee) {
                ee.getStackTrace();
                return true;
            }
        }else {


            return true;

        }
    }

    public void RefreshToken()
    {
        if (IfTokenExpire()) {

            String urlJsonObj = StaticVar.BaseUrl + "/auth/oauth2/token";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("grant_type", "refresh_token");
            params.put("refresh_token", StaticVar.refresh_token);
            params.put("client_id", StaticVar.clientApiID);
            params.put("client_secret", StaticVar.clientSecret);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    // Log.d(TAG, response.toString());

                    try {




                        String access_token = "";
                        access_token = response.getString("access_token");
                        String refresh_token = "";
                        refresh_token = response.getString("refresh_token");
                        String expires_in = "";
                        expires_in = response.getString("expires_in");


                        StaticVar.access_token = access_token;
                        StaticVar.refresh_token = refresh_token;
                        StaticVar.expires_in = expires_in;

                        synchronized (this) {

                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            String currentDateandTime = "";

                            try {


                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                currentDateandTime = sdf.format(new Date());
                            } catch (Exception ee) {
                                ee.getStackTrace();
                            }
                            StaticVar.date_token=currentDateandTime;


                            editor.putString("access_token", access_token);
                            editor.putString("refresh_token", refresh_token);
                            editor.putString("expires_in", expires_in);
                            editor.putString("date_token", currentDateandTime);

                            editor.commit();
                        }


                    } catch (Exception e) {


                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                    try {

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson = error2.getMessage();
                            JSONObject errorJ = new JSONObject(errorJson);
                            String MessageError = errorJ.getString("error");
                            //FirebaseCrash.log("APIAuth Error :" + MessageError);
                            //showToast("Error: " + MessageError);

                        }

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }


                }


            }) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {


                handlerRefreshToken.post(new Runnable() {
                    public void run() {


                        RefreshToken();


                    }
                });
            }
        };
    }


    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showToast(String message,Boolean LongT) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        if (LongT==false)
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        else
            toast = Toast.makeText(this, message, Toast.LENGTH_LONG);

        toast.show();
    }



    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {

                onApplicationDisconnected();
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {
                mCastSession = castSession;






            }

            private void onApplicationDisconnected() {
               // closeCustomMessageChannel();
                mCastSession = null;
            }
        };
    }



    private void makeGetVideoInformationPosition(final String access_token, String videoID) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }



        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/videos/"+videoID;

        loading_spinner.setVisibility(View.VISIBLE);


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,urlJsonObj,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        loading_spinner.setVisibility(View.GONE);
                        try {

                            try{
                           int  playerPositionJ = response.getInt("playerPosition");
                            playerPosition=playerPositionJ*1000;
                            positionvideo.setVisibility(VISIBLE);
                            positionvideo.setMax(videoDuration*1000);


                            positionvideo.setProgress(playerPosition);
                            }catch (Exception ee)
                            {

                            }
                            AnimationUtils.showMe(bntPlay,100);
                            AnimationUtils.rotateX(bntPlay,300);
                            loading_spinner.setVisibility(GONE);



                        }catch (Exception ee)
                        {
                            loading_spinner.setVisibility(GONE);
                            AnimationUtils.showMe(bntPlay,100);
                            AnimationUtils.rotateX(bntPlay,300);
                            ee.printStackTrace();
                        }


                        if (StaticVar.subscription)
                            txtsubscribe.setVisibility(GONE);
                        else
                            txtsubscribe.setVisibility(VISIBLE);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                  loading_spinner.setVisibility(View.GONE);
                positionvideo.setVisibility(GONE);
                AnimationUtils.showMe(bntPlay,100);
                AnimationUtils.rotateX(bntPlay,300);
                if (StaticVar.subscription)
                    txtsubscribe.setVisibility(GONE);
                else
                    txtsubscribe.setVisibility(VISIBLE);
                playerPosition=0;
                try {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson = error2.getMessage();
                        JSONObject errorJ = new JSONObject(errorJson);
                        String MessageError = errorJ.getString("error");
                        //showToast("Error videos: " + MessageError);


                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }
                error.printStackTrace();
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());



            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+access_token);
                // headers.put("key", "Value");
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(req);






    }


    private void makeDeleteMovieToFavoris(final String access_token, String MovieId) {


        if (access_token.equals("") )
        {


            return;
        }



        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/favoritesMovies/"+MovieId;


        loading_spinner.setVisibility(VISIBLE);



        JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE,urlJsonObj,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isFavoris=false;
                        loading_spinner.setVisibility(GONE);
                        favButton.setText(getText(R.string.add_favoris));

                        imgFavButton.setBackgroundResource(R.drawable.favbutton);

                        try {

                            if (StaticVar.mainAct!=null) {


                                if (StaticVar.mainAct.mFirebaseAnalytics != null) {



                                    try {

                                    }catch (Exception ee){
                                        ee.printStackTrace();
                                    }


                                    Bundle params = new Bundle();
                                    params.putString("type", movieType);
                                    params.putString("movie_title", titleMovie);



                                    StaticVar.mainAct.mFirebaseAnalytics.logEvent("delete_video_from_wishlist", params);
                                }
                            }
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }






                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
                loading_spinner.setVisibility(GONE);

                error.printStackTrace();
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());
                try {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson=error2.getMessage();
                        JSONObject errorJ=new JSONObject(errorJson);
                        String MessageError=errorJ.getString("error");
                        showToast("Error Delete from fav: " + MessageError);

                    }
                }catch (Exception ee) {
                    ee.printStackTrace();
                }


            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+access_token);
                // headers.put("key", "Value");
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(req);






    }

    private void makeAddMovieToFavoris(final String access_token, String MovieId) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }





        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/favoritesMovies/";


        JSONObject params=new JSONObject();
        try {
            params.put("_id", MovieId);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        loading_spinner.setVisibility(VISIBLE);
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,urlJsonObj,params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        isFavoris=true;
                        loading_spinner.setVisibility(GONE);
                        AnimationUtils.rotateX(imgFavButton,100);


                        favButton.setText(getText(R.string.delete_favoris));
                        imgFavButton.setBackgroundResource(R.drawable.deletefav);

                        try {

                            if (StaticVar.mainAct!=null) {


                                if (StaticVar.mainAct.mFirebaseAnalytics != null) {



                                    try {

                                    }catch (Exception ee){
                                        ee.printStackTrace();
                                    }


                                    Bundle params = new Bundle();
                                    params.putString("type", movieType);
                                    params.putString("movie_title", titleMovie);



                                    StaticVar.mainAct.mFirebaseAnalytics.logEvent("add_video_to_wishlist", params);
                                }
                            }
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
                loading_spinner.setVisibility(GONE);
                error.printStackTrace();
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());
               try {
                   if(error.networkResponse != null && error.networkResponse.data != null){
                       VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                       String errorJson=error2.getMessage();
                       JSONObject errorJ=new JSONObject(errorJson);
                       String MessageError=errorJ.getString("error");
                       showToast("Error: " + MessageError);

                   }

               }catch (Exception ee) {
                           ee.printStackTrace();
                    }


            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+access_token);
                // headers.put("key", "Value");
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(req);






    }

  public void  DoResponseGetSerieSaisons (JSONObject response,RecyclerView catMoviesRecyclerView) {
        Log.d(TAG, response.toString());

        ArrayList<SerieSaisonModel> CatMoviesList = new ArrayList<SerieSaisonModel>();

        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();


            JSONObject Movie = (JSONObject) response;

            String label=Movie.getString("genre");


            JSONArray seasons=Movie.getJSONArray("seasons");

            for(int i=0;i<seasons.length();i++)

            {

                JSONObject saison=seasons.getJSONObject(i);


                String title = saison.getString("title");
                int _id = saison.getInt("_id");
                JSONArray episodes = saison.getJSONArray("episodes");


                ArrayList<SerieItemModel> episodesList = new ArrayList<SerieItemModel>();
                for (int x = 0; x < episodes.length(); x++) {

                    try {
                        JSONObject ep = (JSONObject) episodes.get(x);
                        String id_video = ep.getJSONObject("video").getString("_id");

                        if (movieType.equals("serie")) {
                            if (x == 0)
                                makeGetVideoInfo(StaticVar.access_token, id_video);
                        }
                        String titleMovie = ep.getString("title");
                        String episodeNumber = ep.getString("episodeNumber");
                        JSONObject posterMovie = ep.getJSONObject("poster");
                        String urlImageMovie = posterMovie.getString("imgix") + "?&crop=entropy&fit=min&w=250&h=200&q=80&fm=jpg&auto=format&dpr="+StaticVar.densityPixel;
                        episodesList.add(new SerieItemModel(titleMovie, episodeNumber, urlImageMovie, ep, Movie));
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }


                CatMoviesList.add(new SerieSaisonModel(title, episodesList));


                //Create an adapter for the listView and add the ArrayList to the adapter.


                              /*  mn.add(""+i);
                                MenuItem mi = mn.getItem(i);

                                mi.setTitle(label);*/

                //   mi.getActionView().setTag(_id);


            }




            SerieSaisonListAdapter lstHomeCatAdapter = new SerieSaisonListAdapter(CatMoviesList);

            catMoviesRecyclerView.setAdapter(lstHomeCatAdapter);
            loading_spinner.setVisibility(GONE);

        } catch (Exception e) {
            e.printStackTrace();
            loading_spinner.setVisibility(GONE);
           // showToast("Error: " + e.getMessage());
        }



    }
    private void makeGetSerieSaisons(final String access_token, final RecyclerView catMoviesRecyclerView, String idMovie) {



        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }

        loading_spinner.setVisibility(VISIBLE);
       // loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/movies/"+idMovie+StaticVar.ApiUrlParams;



        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONObject response=new JSONObject(data) ;

                DoResponseGetSerieSaisons(response,catMoviesRecyclerView);


            } catch (Exception e) {
                e.printStackTrace();
                loading_spinner.setVisibility(GONE);
            }
        }
         else{


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    DoResponseGetSerieSaisons(response,catMoviesRecyclerView);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    loading_spinner.setVisibility(GONE);

                    VolleyLog.d(TAG, "Error GetSerie: " + error.getMessage());
                   try {
                        String errorStr=error.getMessage();
                        if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                        showToast("Error GetSerieSaisons" + errorStr );
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }
            }) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer "+access_token);
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);
          }


    }


    public void  DoResponseVideoInfo (JSONObject response) {
        Log.d(TAG, response.toString());



        loading_spinner.setVisibility(View.GONE);

        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();


            videoDRM=response.getBoolean("drm");
            try{
            videoDuration=response.getInt("duration");
            }catch (Exception ee)
            {

            }

            try {
                videoMp4DownloadUrl = response.getString("sourceMp4");
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

            try {
                sourceMp4Size = response.getString("sourceMp4Size");
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

            try {
                sourceMp4DecipheredSize = response.getString("sourceMp4DecipheredSize");
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }






            try {
                videoMp4Trailer = response.getString("sourceMp4Deciphered");
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

            if (!videoMp4Trailer.equals("") && !videoMp4Trailer.equals("null"))
                layoutTrailer.setVisibility(VISIBLE);
            else
                layoutTrailer.setVisibility(GONE);


          //  videoID=response.getString("_id");

            JSONArray js= response.getJSONArray("sources");

            for (int i=0;i<js.length();i++)
            {
                JSONObject sr=js.getJSONObject(i);

                String type=sr.getString("type");
                String src=sr.getString("src");



                if (type.equals("application/dash+xml"))
                    videoDashUrl=src;
                else    if (type.equals("application/vnd.apple.mpegurl"))
                    videoHlsUrl=src;
                else  if (type.equals("application/vnd.ms-sstr+xml"))
                    videoSmoothUrl=src;

            }

            if (!movieType.equals("serie")) {
                if (!videoMp4DownloadUrl.equals("") && !videoMp4DownloadUrl.equals("null"))
                    bntDownload.setVisibility(VISIBLE);
                else
                    bntDownload.setVisibility(GONE);
            }else
            {
                bntDownload.setVisibility(GONE);
            }


            makeGetVideoInformationPosition(StaticVar.access_token,videoID);




        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error Video Info: " + e.getMessage());
            loading_spinner.setVisibility(GONE);
        }



    }

    private void makeGetVideoInfo(final String access_token, String idVideo) {



        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/videos/"+idVideo;



        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONObject response=new JSONObject(data) ;

                DoResponseVideoInfo(response);


            } catch (Exception e) {
                e.printStackTrace();
                loading_spinner.setVisibility(GONE);
            }
        }
        else{


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    DoResponseVideoInfo(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    loading_spinner.setVisibility(GONE);
                    try {
                        String errorStr=error.getMessage();
                        if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                        showToast("Error " + errorStr );

                        if(error.networkResponse != null && error.networkResponse.data != null){
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson=error2.getMessage();
                            JSONObject errorJ=new JSONObject(errorJson);
                            String MessageError=errorJ.getString("error");
                            showToast("Error GetVideoInfo: " + MessageError);

                        }

                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }
            }) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer "+access_token);
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }


    }





    private void makeIsfavoris(final String access_token, String idVideo) {



        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


         loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/favoritesMovies/"+idVideo;




            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    loading_spinner.setVisibility(View.GONE);

                    isFavoris=true;

                    favButton.setText(getText(R.string.delete_favoris));
                    imgFavButton.setBackgroundResource(R.drawable.deletefav);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading_spinner.setVisibility(View.GONE);

                    isFavoris=false;

                    favButton.setText(getText(R.string.add_favoris));

                    imgFavButton.setBackgroundResource(R.drawable.favbutton);


                    try {

                        if(error.networkResponse != null && error.networkResponse.data != null){
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson=error2.getMessage();
                            JSONObject errorJ=new JSONObject(errorJson);
                            String MessageError=errorJ.getString("error");


                        }

                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }
            }) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer "+access_token);
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);



    }


    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE );
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(MovieDetailsActivity.this,new String[]{

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE},1
        );
    }
    public void PlayerStart()
    {

        try {

            if (StaticVar.mainAct!=null) {


                if (StaticVar.mainAct.mFirebaseAnalytics != null) {



                    try {

                    }catch (Exception ee){
                        ee.printStackTrace();
                    }


                    Bundle params = new Bundle();
                    params.putString("type", movieType);
                    params.putString("movie_title", titleMovie);



                    StaticVar.mainAct.mFirebaseAnalytics.logEvent("open_video", params);
                }
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        String extension = "mpd";
        String name=titleMovie;
        UUID drmSchemeUuid= null;
        try {
            if (videoDRM)
                drmSchemeUuid = getDrmUuid("widevine");
            else
                drmSchemeUuid = getDrmUuid("");
        } catch (ParserException e) {
            e.printStackTrace();
        }


        String drmLicenseUrl="";
        if (videoDRM)drmLicenseUrl=StaticVar.drmLicenseUrl;


        String[] drmKeyRequestProperties=null;

        ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();

        drmKeyRequestProperties = drmKeyRequestPropertiesList.toArray(new String[0]);



        Sample smp=new UriSample( name,videoID,  drmSchemeUuid,  drmLicenseUrl,   drmKeyRequestProperties,  false,videoDashUrl,videoSmoothUrl,ImageUrl,extension,playerPosition,movieType) ;
        Intent nb=smp.buildIntent(getApplicationContext());
        startActivity(nb);




    }




    public static String SaveMovieInfoJson(JSONObject object,String filename) throws IOException {
        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += filename+".afd";
        File data = new File(path);
        if (!data.createNewFile()) {
            data.delete();
            data.createNewFile();
        }



        BufferedWriter output = new BufferedWriter(new FileWriter(data));
        output.write(object.toString());
        output.close();

        return path;
    }

public void ImageToFile (String filename)
{
    try {
        imgMovie.setDrawingCacheEnabled(true);
        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += filename+".aim";
        File data = new File(path);
        FileOutputStream fos = new FileOutputStream(data);
        Bitmap bitmap = imgMovie.getDrawingCache();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}


    public void DownloadTrailer()
    {


        if (!videoMp4Trailer.equals("") && !videoMp4Trailer.equals("null")) {
            try {


                readVideoFromFile(videoMp4Trailer,true);
                return;

            } catch (Exception ee) {

            }
        }
        /*

        final String filename="afro_local_trailer_"+movieID;

        String pathVideo = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;

        File dir = new File(pathVideo);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        pathVideo +=filename+ ".afv";

        File vi=new File(pathVideo);
        if (vi.exists())
        {

            if (!sourceMp4DecipheredSize.equals("") && !sourceMp4DecipheredSize.equals("null"))
                try {
                    long t= Long.parseLong(sourceMp4DecipheredSize);

                   if (t== vi.length()) {

                       readVideoFromFile(pathVideo,false);
                       return;
                   }
                }catch (Exception ee)
                {

                }


        }
        String Taille="";
        if (!sourceMp4DecipheredSize.equals("") && !sourceMp4DecipheredSize.equals("null"))
            try {
                long t= Long.parseLong(sourceMp4DecipheredSize);

                Taille =getString(R.string.sizeoffile)+" "+ getStringSizeLengthFile(t);
            }catch (Exception ee)
            {

            }

        String txt=getString(R.string.askdownload )+" "+Taille;

        final MaterialDialog dialog = new MaterialDialog.Builder(MovieDetailsActivity.this)
                .title(R.string.download)
                .content(txt)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .show();

        View positive = dialog.getActionButton(DialogAction.POSITIVE);


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {

                    if (StaticVar.mainAct!=null) {


                        if (StaticVar.mainAct.mFirebaseAnalytics != null) {



                            try {

                            }catch (Exception ee){
                                ee.printStackTrace();
                            }


                            Bundle params = new Bundle();
                            params.putString("type", movieType);
                            params.putString("movie_title", titleMovie);



                            StaticVar.mainAct.mFirebaseAnalytics.logEvent("download_trailer", params);
                        }
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }



                    new DownloadFileFromURL().execute(videoMp4Trailer);

                dialog.dismiss();

            }
        });
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        /*if (BuildConfig.DEBUG) {
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




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        StaticVar.MovieDetailsAct=this;
        StaticVar.densityPixel = getResources().getDisplayMetrics().density;

        sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);



        try{
            coverImageUrl = this.getIntent().getStringExtra("coverImageUrl");
        }catch (Exception ee)
        {}

        try {

            String movieInfoString = this.getIntent().getStringExtra("movieInfo");





             movieInfo =new JSONObject(movieInfoString);

            movieID=movieInfo.getString("_id");
             titleMovie=movieInfo.getString("title");
            try {
                labelMovie = movieInfo.getString("genre");
            }catch (Exception ee){
                ee.printStackTrace();
            }
            try {
                movieDuration = movieInfo.getString("duration");
            }catch (Exception ee)
            {

            }
            movieSchedule=movieInfo.getString("schedule");


            movieType=movieInfo.getString("type");

if (movieType.equals("serie"))
{
    videoID="";
}else {
    videoID = movieInfo.getString("videoId");
}


try{
    Boolean islive=movieInfo.getBoolean("live");
    if (islive)
    {
        movieType="live";
    }

}catch (Exception ee)
{
    ee.printStackTrace();
}


            movieDescription=movieInfo.getString("synopsis");
            JSONObject posterMovie  =movieInfo.getJSONObject("poster");



             ImageUrl= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=400&h=300&q=70&fm=jpg&auto=format&dpr="+StaticVar.densityPixel;

            ImageUrlBig= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=800&h=600&q=100&fm=jpg&auto=format&dpr="+StaticVar.densityPixel;



            JSONArray categorys  =movieInfo.getJSONArray("categorys");

            JSONObject categoryItem=(JSONObject)categorys.get(0);
            movieCat=categoryItem.getString("label");




        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


         imgMovie =(ImageView)findViewById(R.id.imgMovie);
        bntPlay =(ImageView)findViewById(R.id.bntplay) ;
        loading_spinner=(ProgressBar) findViewById(R.id.loading_spinner) ;


         favButton =(TextView)findViewById(R.id.FavButton) ;
         imgFavButton =(ImageView)findViewById(R.id.imgFavButton) ;
        bntDownload=(ImageView)findViewById(R.id.bntDownload) ;

        positionvideo=(ProgressBar) findViewById(R.id.positionvideo) ;

        bntPlay.setVisibility(GONE);


        layoutTrailer=(RelativeLayout) findViewById(R.id.layoutTrailer) ;

        imgbntTrailer=(ImageView)findViewById(R.id.imgbntTrailer) ;
        bntTrailer =(TextView)findViewById(R.id.bntTrailer) ;


        layoutTrailer.setVisibility(GONE);



        txtsubscribe=(TextView)  findViewById(R.id.txtsubscribe) ;

        imgbntTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadTrailer();

            }
        });





        bntTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DownloadTrailer();


            }
        });


     try {
         if (coverImageUrl.equals("") && coverImageUrl!=null) {


             Picasso.with(this)
                     .load(ImageUrl)

                     .into(imgMovie);
         } else {


             Picasso.with(this)
                     .load(coverImageUrl)

                     .into(imgMovie);

         }
     }catch (Exception ee)
     {
         Picasso.with(this)
                 .load(ImageUrl)

                 .into(imgMovie);
     }






        AppBarLayout appbar = (AppBarLayout)findViewById(R.id.app_bar_layout);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {



            try {
                ImageView imgdown=(ImageView)this.findViewById(R.id.imgdown);

                AnimationUtils.enterBottom(imgdown, 1000);
                AnimationUtils.rotateX(imgdown, 1300);
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int height = metrics.heightPixels;
                int width = metrics.widthPixels;

                float heightDp = getResources().getDisplayMetrics().heightPixels ;// (float) StaticVar.densityPixel;
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
                RelativeLayout favlayout=(RelativeLayout)this.findViewById(R.id.favlayout);
                int hf=(int)favlayout.getHeight();

                lp.height = (int) heightDp - hf;
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

        }










        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        RecyclerView catMoviesRecyclerView = (RecyclerView)findViewById(R.id.lstHomeMovies);
        catMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        ExpandableTextView expand_text_view = (ExpandableTextView ) findViewById(R.id.expand_text_view);


        if (movieDescription.equals("null"))expand_text_view.setVisibility(GONE);
        else {
            expand_text_view.setVisibility(VISIBLE);
            expand_text_view.setText(movieDescription);
        }

        TextView movielabel = (TextView ) findViewById(R.id.txtMovieLabel);

        if(labelMovie.equals("") || labelMovie.equals("null"))
            movielabel.setVisibility(GONE);
        else {
            movielabel.setVisibility(VISIBLE);
            movielabel.setText(labelMovie);
        }


        try{

        if (!movieDuration.equals("null")) {

            Scanner in = new Scanner(System.in);
            NumberFormat formatter = new DecimalFormat("00");
            int s = Integer.parseInt(movieDuration)  ;

            int sec = s % 60;
            int min = (s / 60)%60;
            int hours = (s/60)/60;

            movieDuration=formatter.format(hours) + ":" +formatter.format( min) + ":" + formatter.format(sec);

            TextView movieduration = (TextView) findViewById(R.id.txtMovieDuration);
            movieduration.setText(movieDuration);
        }else
        {

            FrameLayout FrameLayoutDuration = (FrameLayout ) findViewById(R.id.FrameLayoutDuration);
            FrameLayoutDuration.setVisibility(GONE);

        }
        }catch (Exception ee)
        {

        }

        TextView txtMovieSc= (TextView) findViewById(R.id.txtMovieSc);

        String st="";
        if (!movieSchedule.equals("") && !movieSchedule.equals("null"))st= " - "+movieSchedule;

        txtMovieSc.setText(movieCat + st);



        try {
            setSupportActionBar(toolbar);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        String finalTitleMovie = titleMovie;

        getSupportActionBar().setTitle(finalTitleMovie);

        makeIsfavoris(StaticVar.access_token,movieID);


        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!movieID.equals("")) {
                    if (isFavoris == false)
                        makeAddMovieToFavoris(StaticVar.access_token, movieID);
                    else


                        makeDeleteMovieToFavoris(StaticVar.access_token, movieID);
                }


            }
        });



        imgFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!movieID.equals("")) {
                    if (isFavoris == false)
                        makeAddMovieToFavoris(StaticVar.access_token, movieID);
                    else


                        makeDeleteMovieToFavoris(StaticVar.access_token, movieID);
                }


            }
        });


        bntPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


        if (StaticVar.subscription) {
            PlayerStart();
        }
                else
        {
           // Intent nb=new Intent(MovieDetailsActivity.this,PaymentActivityDemo.class);
            Intent nb=new Intent(MovieDetailsActivity.this,PaymentActivity.class);
            startActivityForResult(nb,5);
        }



            }
        });


        bntDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StaticVar.subscription==false){
                   // Intent nb=new Intent(MovieDetailsActivity.this,PaymentActivityDemo.class);
                    Intent nb=new Intent(MovieDetailsActivity.this,PaymentActivity.class);
                    startActivityForResult(nb,5);
                    return;
                }
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP){
                    // Do something for lollipop and above versions
                    checkPermission();
                    if (!checkPermission()) {
                        requestPermission();
                        return;
                    }
                }



                String Taille="";
                if (!sourceMp4Size.equals("") && !sourceMp4Size.equals("null"))
                    try {
                        long t= Long.parseLong(sourceMp4Size);

                        Taille =getString(R.string.sizeoffile)+" "+ getStringSizeLengthFile(t);
                    }catch (Exception ee)
                    {

                    }

                String txt=getString(R.string.askdownload )+" "+Taille;

                final MaterialDialog dialog = new MaterialDialog.Builder(MovieDetailsActivity.this)
                        .title(R.string.download)
                        .content(txt)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .show();

                View positive = dialog.getActionButton(DialogAction.POSITIVE);


                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {
                            try {
                                if (videoID.equals(""))
                                {
                                    showToast("Error : videoID empty");
                                }else {
                                    String Path=SaveMovieInfoJson(movieInfo, videoID);
                                    ImageToFile(videoID);


                                    String PathVideo=Path.substring(0,Path.lastIndexOf(".")+1 ) +"aiv";
                                    String PathImage=Path.substring(0,Path.lastIndexOf(".")+1 ) +"aim";
                                   
                                    String tmDevice = StaticVar.android_id;


                                    Intent intent = new Intent(Intent.ACTION_SYNC, null,  StaticVar.mainAct, DownloadFileService.class);

                                    intent.putExtra("url",videoMp4DownloadUrl);
                                    intent.putExtra("title",movieInfo.getString("title"));
                                    intent.putExtra("tmDevice",tmDevice);
                                    intent.putExtra("PathVideo",PathVideo);
                                    intent.putExtra("PathImage",PathImage);
                                    intent.putExtra("tmDevice",StaticVar.android_id);



                                    if ( StaticVar.mainAct!=null) {
                                        StaticVar.mainAct.startService(intent);
                                        StaticVar.mainAct.showToast(StaticVar.mainAct.getString(R.string.downloadbegin));
                                    }

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            dialog.dismiss();

                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                    }
                });







            }
        });

        if (!movieType.equals("serie"))
            makeGetVideoInfo(StaticVar.access_token,videoID);

        makeGetSerieSaisons(StaticVar.access_token,catMoviesRecyclerView,movieID);





        Glide.with(getApplicationContext())
                .load(ImageUrlBig)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap b, GlideAnimation<? super Bitmap> glideAnimation) {

                        try {

                            imgMovie.setImageBitmap(b);

                        } catch (Exception  e) {
                            e.printStackTrace();
                        }
                    }
                });




/*
        try {

            mCastContext = CastContext.getSharedInstance(MovieDetailsActivity.this);
            mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);

            setupCastListener();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

*/



        /*
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appbar = (AppBarLayout)findViewById(R.id.app_bar_layout);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isVisible = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    getSupportActionBar().setTitle(finalTitleMovie);
                    isVisible = true;
                } else if(isVisible) {
                    getSupportActionBar().setTitle("");
                    isVisible = false;
                }
            }
        });
*/



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 5) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
               MovieDetailsActivity.this.finish();
                String nb=data.getStringExtra("param_result");

                if (nb.equals("As You Go with data")) {
                    showToast(getString(R.string.MsgPlan2Demo),true);
                }else
                {
                    showToast(getString(R.string.MsgPlan1Demo),true);
                }
            }
        }
    }
    @Override
    protected void onResume() {
        // mCastContext.addCastStateListener(mCastStateListener);

        super.onResume();

        startTimer();

        if (mCastContext!=null)  mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener,
                CastSession.class);
        if (mCastSession == null && mCastContext!=null ) {
            // Get the current session if there is one
            mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
        }

        if (loading_spinner!=null)loading_spinner.setVisibility(GONE);
    }

    @Override
    protected void onPause() {
        //  mCastContext.removeCastStateListener(mCastStateListener);
        super.onPause();
        stoptimertask();
       if (mCastContext!=null) mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener,
                CastSession.class);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu, menu);


      //  mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);



        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;



            case R.id.media_route_menu_item:

                return true;



            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
