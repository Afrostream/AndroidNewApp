package tv.afrostream.app.activitys;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.utils.StaticVar;


/**
 * Created by bahri on 20/03/2017.
 */

public class WebViewAuth extends AppCompatActivity {

    private Toast toast;
    WebView wv;
    SharedPreferences sharedpreferences;
    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }



    private  void OnResponseAuthGeo(JSONObject response,String url)
    {


        try {


            String country="";
            try {


                country = response.getString("countryCode");
            }catch (Exception ee)
            {
                ee.getStackTrace();
            }


            String Language="FR";
            try {


                Language= Locale.getDefault().getLanguage().toUpperCase();
            }catch (Exception ee)
            {
                ee.getStackTrace();
            }



            //  country="FR";

            if (country.equals("null") ||country.equals("")  )country="--";


            StaticVar.CountryCode=country;


            StaticVar.ApiUrlParams="?country="+country+"&language="+Language;

            wv.loadUrl(url);





        } catch (Exception e) {


            e.printStackTrace();
            showToast("Error: " + e.getMessage());
        }


    }

    private void makeAuthGeoLoadUrl(final String url) {






        String urlJsonObj= StaticVar.BaseUrl+"/auth/geo";




        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,



                JSONObject response=new JSONObject(data) ;

                OnResponseAuthGeo(response, url);


            } catch (Exception e) {

                e.printStackTrace();
                FirebaseCrash.log(e.getMessage() +" -- "+e.getStackTrace());
            }
        }
        else {



            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {



                    OnResponseAuthGeo(response, url);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {





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

    private class MyWebViewClient extends WebViewClient {








        @Override
        public void onLoadResource(WebView view, String url) {

            //url="afrostream://hash/eyJzdGF0dXNDb2RlIjo1MDAsImRhdGEiOnsibWVzc2FnZSI6IkVycm9yOiBObyB1c2VyIGZvdW5kLCBwbGVhc2UgYXNzb2NpYXRlIHlvdXIgcHJvZmlsZSBhZnRlciBiZWluZyBjb25uZWN0ZWQiLCJlcnJvciI6IlVuYXV0aG9yaXplZCJ9fQ==";



            if (url.contains("callback-android")) {
                // This is my web site, so do not override; let my WebView load the page



                String data=url.substring( url.lastIndexOf("?")+1);
                byte[] DataDecode= android.util.Base64.decode(data,Base64.DEFAULT);
                String DataStr=new String(DataDecode);
                try {
                    JSONObject DataJ = new JSONObject(DataStr);

                    if (DataJ.getString("statusCode").equals("200"))


                    {

                        String access_token=DataJ.getJSONObject("data").getString("access_token");
                        String refresh_token=DataJ.getJSONObject("data").getString("refresh_token");
                        String expires_in=DataJ.getJSONObject("data").getString("expires_in");

                        StaticVar.FirstLaunch=true;
                        StaticVar.access_token=access_token;
                        StaticVar.refresh_token=refresh_token;
                        StaticVar.expires_in=expires_in;


                        synchronized (this) {

                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            String currentDateandTime="";

                            try {


                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                currentDateandTime = sdf.format(new Date());
                            }catch (Exception ee)
                            {
                                ee.getStackTrace();
                            }



                            StaticVar.date_token=currentDateandTime;


                            editor.putString("access_token", access_token);
                            editor.putString("refresh_token", refresh_token);
                            editor.putString("expires_in", expires_in);
                            editor.putString("date_token", currentDateandTime);

                            editor.commit();
                        }


                        final Intent intent = new Intent(WebViewAuth.this, MainActivity.class);




                        startActivity(intent);
                        finish();

                    }else
                    {
                        String Error="Error :";
                        try{

                            Error+=DataJ.getString("statusCode")+DataJ.getJSONObject("data").getString("message");

                        }catch (Exception ee)
                        {
                            ee.getStackTrace();
                        }
                        showToast(Error);
                    }


                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }





            }
        }


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
        setContentView(R.layout.activity_webview);
         wv=(WebView)findViewById(R.id.webview);
        final ProgressBar pb=(ProgressBar) findViewById(R.id.pB1);




        wv.setSystemUiVisibility(View.VISIBLE);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);



        webSettings.setLoadsImagesAutomatically(true);


        wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                try {
                    if (progress < 100 && pb.getVisibility() == ProgressBar.GONE) {
                        pb.setVisibility(ProgressBar.VISIBLE);

                    }

                    pb.setProgress(progress);
                    if (progress == 100) {
                        pb.setVisibility(ProgressBar.GONE);

                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }
            }
        });




        if (this.getIntent().getExtras()!=null) {
            String url = this.getIntent().getExtras().getString("url");


            if (url!="" && url!="null")
            {
                makeAuthGeoLoadUrl(url);
            }
        }


        sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;





            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
