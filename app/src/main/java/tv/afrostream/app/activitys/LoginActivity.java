package tv.afrostream.app.activitys;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


import org.apache.http.annotation.Experimental;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.afrostream.app.services.AfrostreamBootWakefulService;
import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.R;


/**
 * Created by bahri on 10/01/2017.
 */

public class LoginActivity extends AppCompatActivity {

    Button bntLogin;
    private FirebaseAnalytics mFirebaseAnalytics;








    Button bntNewAccount;
    Button bnt_my_download;
    EditText txtUsername, txtPassword;
    ImageView logo;
    TextView txtForgetPassword;

    TextView txtConnectWith;

    ImageView bntFacebook;
    ImageView bntOrange;
    ImageView bntBouygue;
    ImageView imageback;
    private Toast toast;
    String backimageurl="";
    String autoLogin="";

    ProgressBar loading_spinner=null;
    public String  TAG=LoginActivity.class.getSimpleName();


    SharedPreferences sharedpreferences;

CallbackManager callbackManager;

    String fb_token="";






    class SaveBitmapFile extends AsyncTask<Bitmap, Void, String>{
        @Override
        protected String doInBackground(Bitmap... urls) {
            if (urls.length > 0) {
                Bitmap b = urls[0];
                  FileOutputStream out = null;
                                            try {

                                                final String path = Environment.getExternalStorageDirectory() + File.separator + "backimage.png";

                                                out = new FileOutputStream(path);
                                                b.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                                                // PNG is a lossless format, the compression factor (100) is ignored
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            } finally {
                                                try {
                                                    if (out != null) {
                                                        out.close();
                                                    }

                                                    return "";
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
            }

            return "";
        }

        @Override
        protected void onPostExecute(String bmp) {



        }
    }



    class LoadBitmapFile extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... urls) {
            if (urls.length > 0) {
                String myurl = urls[0];
                return BitmapFactory.decodeFile(myurl);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {


            if (bmp != null) {

                imageback.setImageBitmap(bmp);

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        StaticVar.FirstLaunch=true;
try {
    bntOrange.setEnabled(true);
    bntBouygue.setEnabled(true);
    bntFacebook.setEnabled(true);
    bntNewAccount.setEnabled(true);
    bntLogin.setEnabled(true);
    txtForgetPassword.setEnabled(true);
}catch (Exception ee)
{
    ee.getStackTrace();
}

        try {



       if (sharedpreferences!=null) {

           txtUsername.setText(sharedpreferences.getString(StaticVar.usernamePref, ""));
           txtPassword.setText(sharedpreferences.getString(StaticVar.passwordPref, ""));

       }

        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LoginManager.getInstance().logOut();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

private  void OnResponseAuthGeo(JSONObject response,String Username,String Password)
{


    try {

        loading_spinner.setVisibility(View.GONE);


        String country=response.getString("countryCode");

      //  country="FR";

        if (country.equals("null") ||country.equals("")  )country="--";


        StaticVar.CountryCode=country;

        StaticVar.ApiUrlParams="?country="+country;

        makeLogin(Username,Password);





    } catch (Exception e) {

        bntLogin.setEnabled(true);
        e.printStackTrace();
        showToast("Error: " + e.getMessage());
    }
    loading_spinner.setVisibility(View.GONE);

}
    private void makeLoginFacebook(final String TokenFb,final String email,final String firstname,final String lastname) {


        if (TokenFb.equals("") )
        {

            showToast("Error facebook token" );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/auth/oauth2/token";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "facebook");
        params.put("client_id", StaticVar.clientApiID);
        params.put("client_secret", StaticVar.clientSecret);
        params.put("token",TokenFb);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());

                try {




                    String access_token="";
                    access_token=response.getString("access_token");
                    String refresh_token="";
                    refresh_token=response.getString("refresh_token");
                    String expires_in="";
                    expires_in=response.getString("expires_in");
                    String token_type="";
                    token_type=response.getString("token_type");

                    StaticVar.access_token=access_token;
                    StaticVar.refresh_token=refresh_token;
                    StaticVar.expires_in=expires_in;
                    StaticVar.token_type=token_type;



                    synchronized (this) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();



                        editor.putString("access_token", access_token);
                        editor.putString("refresh_token", refresh_token);

                        editor.commit();
                    }



                    StaticVar.FirstLaunch=true;
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);


                   if (getIntent().getExtras() != null) {


                        for (String key : getIntent().getExtras().keySet()) {
                            String value = getIntent().getExtras().getString(key);

                            intent.putExtra(key,value);

                        }

                    }
                     Thread.sleep(200);


                    startActivity(intent);
                    loading_spinner.setVisibility(View.GONE);


                } catch (Exception e) {

                    loading_spinner.setVisibility(View.GONE);
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
                loading_spinner.setVisibility(View.GONE);
                try{
                    if(error.networkResponse != null && error.networkResponse.data != null){

                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson=error2.getMessage();
                        JSONObject errorJ=new JSONObject(errorJson);
                        String MessageError=errorJ.getString("error");

                        if (error.networkResponse.statusCode==403)
                        {


                                if (!MessageError.equals("invalid_grant")) {
                                    Intent nb = new Intent(LoginActivity.this, CreateUserActivity.class);
                                    nb.putExtra("email", email);
                                    nb.putExtra("firstanme", firstname);
                                    nb.putExtra("lastname", lastname);

                                    startActivity(nb);
                                }else
                                {
                                    showToast("Error: " + MessageError);
                                }


                        }else
                        {
                            showToast("Error: " + MessageError);
                        }



                    }
                }catch(Exception ee){ee.printStackTrace();}


            }



        } ) {

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

    private void makeAuthGeo(final String Username, final String Password) {





        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/auth/geo";




        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                loading_spinner.setVisibility(View.GONE);

                JSONObject response=new JSONObject(data) ;

                OnResponseAuthGeo(response, Username, Password);


            } catch (Exception e) {
                loading_spinner.setVisibility(View.GONE);
                e.printStackTrace();
                FirebaseCrash.log(e.getMessage() +" -- "+e.getStackTrace());
            }
        }
        else {



            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    loading_spinner.setVisibility(View.GONE);

                    OnResponseAuthGeo(response, Username, Password);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    loading_spinner.setVisibility(View.GONE);
                    bntLogin.setEnabled(true);



                   try {
                       if(error.networkResponse != null && error.networkResponse.data != null){
                           VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                           String errorJson=error2.getMessage();
                           JSONObject errorJ=new JSONObject(errorJson);
                           String MessageError=errorJ.getString("error");

                           showToast("Error: " + MessageError);
                           FirebaseCrash.log("Geo Error :"+MessageError);

                       }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }



                }



            } ) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();

                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);

        }


    }

    private void makeApiAuth() {






        String urlJsonObj= StaticVar.BaseUrl+"/auth/oauth2/token";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", StaticVar.clientApiID);
        params.put("client_secret", StaticVar.clientSecret);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());

                try {

                    loading_spinner.setVisibility(View.GONE);


                    String access_token="";
                    access_token=response.getString("access_token");
                    String refresh_token="";
                    refresh_token=response.getString("refresh_token");
                    String expires_in="";
                    expires_in=response.getString("expires_in");
                    String token_type="";
                    token_type=response.getString("token_type");

                    StaticVar.access_token_api=access_token;

                    try{
                        makeGetBackgroundConfig(imageback);
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
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
                loading_spinner.setVisibility(View.GONE);
                try{

                    if(error.networkResponse != null && error.networkResponse.data != null){
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson=error2.getMessage();
                        JSONObject errorJ=new JSONObject(errorJson);
                        String MessageError=errorJ.getString("error");
                        FirebaseCrash.log("APIAuth Error :"+MessageError);
                        showToast("Error: " + MessageError);

                    }

                }catch(Exception ee){ee.printStackTrace();}


            }



        } ) {

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

    private void makeGetBackgroundConfig(final ImageView imgMovie) {


        String urlJsonObj= StaticVar.BaseUrl+"/api/app/config";





            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {



                try{
                    String backimage=response.getString("backgroundImage");
                    final String path = Environment.getExternalStorageDirectory() + File.separator + "backimage.png";

                    if (!backimage.equals("")) {
                        Glide.with(getApplicationContext())
                                .load(backimage)
                                .asBitmap()
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap b, GlideAnimation<? super Bitmap> glideAnimation) {

                                        try {







                                            new SaveBitmapFile().execute(b);

                                            imgMovie.setImageBitmap(b);

                                } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                });

                        try {

                            SharedPreferences.Editor editor = sharedpreferences.edit();


                            editor.putString("backimage", backimage);


                            editor.commit();
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

        }else

        {
        File n=new File(path);
        if (n.exists())
        {
        try
        {
        n.delete();
        imgMovie.setImageResource( R.drawable.login_bg);
        }catch (Exception ee)
        {
        ee.printStackTrace();
        }
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
        VolleyLog.d(TAG, "Error: " + error.getMessage());
        loading_spinner.setVisibility(View.GONE);

        try {
        if(error.networkResponse != null && error.networkResponse.data != null){
        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
        String errorJson=error2.getMessage();
        JSONObject errorJ=new JSONObject(errorJson);
        String MessageError=errorJ.getString("error");
        showToast("Error: " + MessageError);

        }
        }catch (Exception ee)
        {
        ee.printStackTrace();
        }



        }



        } ) {

/**
 * Passing some request headers
 */
@Override
public Map<String, String> getHeaders() throws AuthFailureError {


        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer "+StaticVar.access_token_api);
        // headers.put("key", "Value");
        return headers;
        }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(jsonObjReq);




        }
private void ifHuaweiAlert() {
final SharedPreferences settings = getSharedPreferences("ProtectedApps", MODE_PRIVATE);
final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(this);
                dontShowAgain.setText("Do not show again");
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Huawei Protected Apps")
                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Protected Apps", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }

    private void subscribeToPushService() {

        if (StaticVar.DevMode==false)
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        else
            FirebaseMessaging.getInstance().subscribeToTopic("newsdev");





        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        Log.d(TAG, token);

    }
    private void makeLogin(final String Username, final String Password) {


         if (Username.equals("") || Password.equals(""))
         {

             showToast(this.getString(R.string.activity_login_error_login_empty) );
             return;
         }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/auth/oauth2/token";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("client_id", StaticVar.clientApiID);
        params.put("client_secret", StaticVar.clientSecret);
        params.put("username",Username);
        params.put("password", Password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
               // Log.d(TAG, response.toString());

                try {




                    String access_token="";
                    access_token=response.getString("access_token");
                    String refresh_token="";
                    refresh_token=response.getString("refresh_token");
                    String expires_in="";
                    expires_in=response.getString("expires_in");
                    String token_type="";
                    token_type=response.getString("token_type");

                    StaticVar.access_token=access_token;
                    StaticVar.refresh_token=refresh_token;
                    StaticVar.expires_in=expires_in;
                    StaticVar.token_type=token_type;



                    synchronized (this) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString(StaticVar.usernamePref, Username);
                        editor.putString(StaticVar.passwordPref, Password);

                        editor.putString("auto_login", "true");

                        editor.putString("access_token", access_token);
                        editor.putString("refresh_token", refresh_token);

                        editor.commit();
                    }



                    StaticVar.FirstLaunch=true;
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);


                   if (getIntent().getExtras() != null) {


                        for (String key : getIntent().getExtras().keySet()) {
                            String value = getIntent().getExtras().getString(key);

                            intent.putExtra(key,value);

                        }

                    }

                    try {
                        bntOrange.setEnabled(true);
                        bntBouygue.setEnabled(true);
                        bntFacebook.setEnabled(true);
                        bntNewAccount.setEnabled(true);
                        bntLogin.setEnabled(true);
                        txtForgetPassword.setEnabled(true);
                    }catch (Exception ee)
                    {
                        ee.getStackTrace();
                    }

                    Thread.sleep(200);


                    startActivity(intent);
                    loading_spinner.setVisibility(View.GONE);



                } catch (Exception e) {

                    loading_spinner.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                bntLogin.setEnabled(true);
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                loading_spinner.setVisibility(View.GONE);
				try{
                if (error.networkResponse.statusCode==403) {
                   showToast(getString(R.string.activity_login_error_login_incorrect));
                }else
                {
                    showToast(getString(R.string.activity_login_error_login_problem)+ " status code : "+error.networkResponse.statusCode);
                }
				}catch(Exception ee){ee.printStackTrace();}


            }



        } ) {

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


    private void makeLoginAPIAndResterPassword(final String Username, final String Password,final  MaterialDialog dialog,final ProgressBar loading_spinner) {





        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/auth/oauth2/token";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", StaticVar.clientApiID);
        params.put("client_secret", StaticVar.clientSecret);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());

                try {

                    loading_spinner.setVisibility(View.GONE);


                    String access_token="";
                    access_token=response.getString("access_token");
                    String refresh_token="";
                    refresh_token=response.getString("refresh_token");
                    String expires_in="";
                    expires_in=response.getString("expires_in");
                    String token_type="";
                    token_type=response.getString("token_type");








                    makeResetPassword(access_token,Username,Password,dialog,loading_spinner);







                } catch (Exception e) {

                    loading_spinner.setVisibility(View.GONE);
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
                loading_spinner.setVisibility(View.GONE);
                try{

                    showToast(error.getMessage());

                }catch(Exception ee){ee.printStackTrace();}


            }



        } ) {

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

    private void makeResetPassword(final String access_token,final String Username, final String Password,final  MaterialDialog dialog,final ProgressBar loading_spinner) {


        if (Username.equals("") || Password.equals(""))
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/auth/reset ";

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("email",Username);
        params.put("password", Password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());

                try {

                    loading_spinner.setVisibility(View.GONE);
                    showToast(getString(R.string.resetpasswordmsg));

                    dialog.dismiss();

                    try {


                        if (mFirebaseAnalytics != null) {

                            Bundle params = new Bundle();
                            params.putString("user", Username);



                            mFirebaseAnalytics.logEvent("forget_password", params);
                        }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }




                } catch (Exception e) {

                    loading_spinner.setVisibility(View.GONE);
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
                loading_spinner.setVisibility(View.GONE);
                try{
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson=error2.getMessage();
                        JSONObject errorJ=new JSONObject(errorJson);
                        String MessageError=errorJ.getString("error");
                        showToast("Error: " + MessageError);

                    }
                }catch(Exception ee){ee.printStackTrace();}


            }



        } ) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+access_token);
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showInputDialog() {
        final MaterialDialog dialog=new MaterialDialog.Builder(this)
                .title(R.string.activity_login_forget_dialog)

                .customView(R.layout.dialog_forget_password, false)

                .positiveText(R.string.activity_login_forget_validate)
                .negativeText(R.string.cancel)
                .titleColor(Color.WHITE)
                .contentColor(Color.WHITE) // notice no 'res' postfix for literal color
                .linkColorAttr(R.attr.colorPrimary)  // notice attr is used instead of none or res for attribute resolving
                .dividerColorRes(R.color.colorPrimary)
                .backgroundColorRes(R.color.colorPrimary)
                .positiveColor(Color.WHITE)
                .widgetColorRes(R.color.colorPrimary)
                .buttonRippleColorRes(R.color.colorPrimary)
                .show();


        View positive = dialog.getActionButton(DialogAction.POSITIVE);

        final View dialogView = dialog.getView();


        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                ProgressBar loading_spinner=(ProgressBar)dialogView.findViewById(R.id.loading_spinner);
               EditText txtUser =(EditText)dialogView.findViewById(R.id.txtEmail);
                EditText txtPassword =(EditText)dialogView.findViewById(R.id.txtPass);
                EditText txtPasswordRetype =(EditText)dialogView.findViewById(R.id.txtPassVerif);


                if (txtPassword.getText().toString().equals(txtPasswordRetype.getText().toString())) {

                    makeLoginAPIAndResterPassword(txtUser.getText().toString(),txtPassword.getText().toString(),dialog,loading_spinner);
                }else
                {
                    showToast(getString(R.string.verifpasswordmsg));
                }

              //  showToast(txt.getText().toString());


            }
        });

        View negative = dialog.getActionButton(DialogAction.NEGATIVE);
        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });


    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE );
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(LoginActivity.this,new String[]{

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE},1
        );
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {



      /*  if (BuildConfig.DEBUG) {
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


      /*  if (BuildConfig.DEBUG) {
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



       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowReturnTransitionOverlap(true);
            Transition trans = new Explode();
            getWindow().setEnterTransition(trans);

            Transition returnTrans = new Slide();
            returnTrans.setDuration(1000);
            ((Slide) returnTrans).setSlideEdge(Gravity.LEFT);
            getWindow().setReturnTransition(returnTrans);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                //getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }

       setContentView(R.layout.activity_login);






       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "tv.afrostream.app",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String k= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", k);
            }
        } catch (PackageManager.NameNotFoundException e) {


        } catch (NoSuchAlgorithmException e) {

        }*/


       try {

           mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
           FacebookSdk.sdkInitialize(this.getApplicationContext());

           callbackManager = CallbackManager.Factory.create();

           LoginManager.getInstance().registerCallback(callbackManager,
                   new FacebookCallback<LoginResult>() {
                       @Override
                       public void onSuccess(LoginResult loginResult) {
                           Log.d("Success", "Login");
                           fb_token = loginResult.getAccessToken().getToken().toString();
                           // showToast("FB OK "+fb_token);


                           GraphRequest request = GraphRequest.newMeRequest(
                                   loginResult.getAccessToken(),
                                   new GraphRequest.GraphJSONObjectCallback() {
                                       @Override
                                       public void onCompleted(
                                               JSONObject object,
                                               GraphResponse response) {

                                           String email = "";
                                           String firstName = "";
                                           String lastName = "";
                                           String phone = "";
                                           try {


                                               email = object.getString("email");
                                               try {
                                                   Profile profile = Profile.getCurrentProfile();
                                                   if (profile != null) {
                                                       firstName = profile.getFirstName();
                                                       lastName = profile.getLastName();
                                                       Uri ImageProfil = profile.getProfilePictureUri(48, 48);
                                                       StaticVar.facebook_image_profil_url = ImageProfil.toString();
                                                   }
                                               } catch (Exception ee) {
                                                   ee.printStackTrace();
                                               }
                                               // String birthday = object.getString("birthday"); // 01/31/1980 format

                                               makeLoginFacebook(fb_token, email, firstName, lastName);

                                               try {


                                                   if (mFirebaseAnalytics != null) {

                                                       Bundle params = new Bundle();
                                                       params.putString("email", email);
                                                       params.putString("firstName", firstName);
                                                       params.putString("lastName", lastName);


                                                       mFirebaseAnalytics.logEvent("facebook_connect", params);
                                                   }
                                               }catch (Exception ee)
                                               {
                                                   ee.printStackTrace();
                                               }


                                           } catch (Exception ee) {
                                               ee.printStackTrace();
                                           }
                                       }
                                   });
                           Bundle parameters = new Bundle();
                           parameters.putString("fields", "id,name,link, first_name, last_name, email,gender, birthday, location, about");
                           request.setParameters(parameters);
                           request.executeAsync();


                       }

                       @Override
                       public void onCancel() {
                           Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                       }

                       @Override
                       public void onError(FacebookException exception) {
                           Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                       }
                   });


       }catch (Exception ee)
       {
           ee.printStackTrace();
       }

        loading_spinner=(ProgressBar) this.findViewById(R.id.loading_spinner);
        bntLogin = (Button) this.findViewById(R.id.bnt_login);
        bntNewAccount= (Button) this.findViewById(R.id.bnt_new_account);

        bnt_my_download= (Button) this.findViewById(R.id.bnt_my_download);
        logo = (ImageView) this.findViewById(R.id.logo);
        txtUsername = (EditText) this.findViewById(R.id.username);
        txtPassword= (EditText) this.findViewById(R.id.password);

        txtForgetPassword= (TextView) this.findViewById(R.id.forgetpassword);

        txtConnectWith= (TextView) this.findViewById(R.id.connectwith);

        bntFacebook=(ImageView) this.findViewById(R.id.bntfacebook);
        bntOrange=(ImageView) this.findViewById(R.id.bntorange);
        bntBouygue=(ImageView) this.findViewById(R.id.bntbouygue);
        imageback=(ImageView) this.findViewById(R.id.imageback);


        try {

            synchronized (this) {
                sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);

                txtUsername.setText(sharedpreferences.getString(StaticVar.usernamePref, ""));
                txtPassword.setText(sharedpreferences.getString(StaticVar.passwordPref, ""));

                autoLogin =sharedpreferences.getString("auto_login", "");

                backimageurl=sharedpreferences.getString(StaticVar.backimagePref, "");
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }



        try{
            synchronized (this) {
                String path = Environment.getExternalStorageDirectory() + File.separator + "backimage.png";
                File backimage = new File(path);

                if (backimage.exists()) {
                    /*Bitmap bitmap = BitmapFactory.decodeFile(path);
                    imageback.setImageBitmap(bitmap);
                    bitmap=null;*/

                    new LoadBitmapFile().execute(path);



                }
            }



           /* if (backimageurl.equals(""))
            {

                Glide.with(getApplicationContext())
                        .load(backimageurl)

                        .into(imageback) ;
            }*/
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        loading_spinner.setVisibility(View.GONE);

        logo.setVisibility(View.VISIBLE);



        try{
            makeApiAuth();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        bntOrange.setEnabled(false);
        bntBouygue.setEnabled(false);
        bntFacebook.setEnabled(false);
        bntNewAccount.setEnabled(false);
        bntLogin.setEnabled(false);
        txtForgetPassword.setEnabled(false);


       /* SharedPreferences.Editor editor = sharedpreferences.edit();



        editor.putString("access_token", "");


        editor.commit();*/



      //  AnimationUtils.showMe(logo,300);
       // AnimationUtils.enterTop(logo,800);
      //  AnimationUtils.popOut(logo,900);
       // AnimationUtils.popOut(divider,300);
  //      AnimationUtils.changeBound(this,divider,150,150);
        AnimationUtils.enterBottom(txtUsername, 400);
        AnimationUtils.enterBottom(txtPassword,600);

        AnimationUtils.showMe(txtForgetPassword,700);
        AnimationUtils.enterBottom(bntLogin,800);

        AnimationUtils.enterBottom(bntNewAccount,900);







        AnimationUtils.showMe(txtConnectWith,1100);
        AnimationUtils.enterBottom(bntOrange,1200);
        AnimationUtils.enterBottom(bntBouygue,1300);
        AnimationUtils.enterBottom(bntFacebook,1400);


        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        StaticVar.android_id=android_id;


        try {
            subscribeToPushService();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }




        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AnimationUtils.rotateX(txtForgetPassword,0);


                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        showInputDialog();

                    }
                },  250);

            }
        });

        bntOrange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nb = new Intent(LoginActivity.this, WebViewAuth.class);
                nb.putExtra("url", StaticVar.BaseUrl+"/auth/orange/signin?clientType=legacy-api.android");


                startActivity(nb);



            }
        });

        bntBouygue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent nb = new Intent(LoginActivity.this, WebViewAuth.class);
                nb.putExtra("url", StaticVar.BaseUrl+"/auth/bouygues/signin?clientType=legacy-api.android");


                startActivity(nb);

            }
        });




        bntFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StaticVar.facebook_image_profil_url="";


                //LoginManager.getInstance().logInWithPublishPermissions(LoginActivity.this, Arrays.asList("publish_actions"          ));

                try {
                    LoginManager.getInstance().logOut();
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }



                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email",

                        "user_birthday",
"user_actions.video","user_actions.news",
                        "public_profile",
                        "user_friends",
                        "user_about_me",
                        "user_location"));
            }
        });


        bntLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bntLogin.setEnabled(false);

                StaticVar.facebook_image_profil_url="";

                String Username =txtUsername.getText().toString();
                String Password=txtPassword.getText().toString();


                try {
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                        // Do something for lollipop and above versions
                       checkPermission();
                        if (!checkPermission()) {
                            requestPermission();
                            return;
                        }
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

                makeAuthGeo( Username, Password);


            }
        });

        bntNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticVar.facebook_image_profil_url="";
                Intent nb= new Intent(LoginActivity.this,CreateUserActivity.class);
                startActivity(nb);

            }
        });

        try {

            StaticVar.densityPixel = getResources().getDisplayMetrics().density;

            String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;

            String pathSlide = path + "slides.afj";
            String pathCatMovies = path + "catmovies.afj";
            File slide = new File(pathSlide);
            File catMovies = new File(pathCatMovies);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

       // if (slide.exists() && catMovies.exists()) {
          //  AnimationUtils.enterBottom(bnt_my_download,1000);
       /* }else
        {
            bnt_my_download.setVisibility(View.GONE);
        }*/


        bnt_my_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;

                String pathSlide = path+"slides.afj";
                String pathCatMovies = path+"catmovies.afj";
                File slide=new File(pathSlide);
                File catMovies=new File(pathCatMovies);

                //if (slide.exists() && catMovies.exists()) {
                    loading_spinner.setVisibility(View.VISIBLE);



                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.postDelayed(new Runnable() {


                        @Override
                        public void run() {
                           // Intent i = new Intent(LoginActivity.this, MainActivityLocal.class);
                            Intent i = new Intent(LoginActivity.this, MyDownloadActivity.class);
                            startActivity(i);

                        }},300);

                    Handler handler2 = new Handler(Looper.getMainLooper());

                    handler2.postDelayed(new Runnable() {


                        @Override
                        public void run() {


                            loading_spinner.setVisibility(View.GONE);
                        }},3000);

              /*  }else
                {
                    showToast(getString(R.string.nolocalfile));
                }*/

            }
        });






        if (autoLogin.equals("true"))
        {

            bntLogin.callOnClick();

        }

        bntOrange.setEnabled(true);
        bntBouygue.setEnabled(true);
        bntFacebook.setEnabled(true);
        bntNewAccount.setEnabled(true);
        bntLogin.setEnabled(true);
        txtForgetPassword.setEnabled(true);





        try {
            AfrostreamBootWakefulService mSensorService = new AfrostreamBootWakefulService(this);
            Intent mServiceIntent = new Intent(this, mSensorService.getClass());

            if (!isMyServiceRunning(mServiceIntent.getClass())) {
                startService(mServiceIntent);
            }


        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        try{
            String versionName = LoginActivity.this.getPackageManager()
                    .getPackageInfo(LoginActivity.this.getPackageName(), 0).versionName;

            TextView txtVersion=(TextView)findViewById(R.id.txtVersion);
            txtVersion.setText("App version : "+versionName);



        }catch (Exception ee)
        {
            ee.printStackTrace();
        }







        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {


            @Override
            public void run() {


                try {

                    AppController.getInstance().getRequestQueue().getCache().clear();


                    try {
                        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                            // Do something for lollipop and above versions
                            checkPermission();
                            if (!checkPermission()) {
                                requestPermission();
                                return;
                            }
                        }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }




                }catch (Exception e)
                {
                    e.printStackTrace();

                }






            }
        }, 1000 );








    }
}
