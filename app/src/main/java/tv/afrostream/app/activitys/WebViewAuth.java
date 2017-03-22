package tv.afrostream.app.activitys;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import tv.afrostream.app.R;
import tv.afrostream.app.utils.StaticVar;


/**
 * Created by bahri on 20/03/2017.
 */

public class WebViewAuth extends AppCompatActivity {

    private Toast toast;
    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private class MyWebViewClient extends WebViewClient {



        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }




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
        WebView wv=(WebView)findViewById(R.id.webview);
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
                wv.loadUrl(url);
            }
        }



    }
}
