package tv.afrostream.app.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;

import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URLEncoder;

import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;

import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 23/05/2017.
 */

public class LifeFragment extends Fragment {


    private Toast toast;

    public String  TAG=LifeFragment.class.getSimpleName();
    public WebView wv;



    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        StaticVar.lifeFragment=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {



               getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StaticVar.lifeFragment=this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_life, container, false);


        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme_NoActionBar);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        return localInflater.inflate(R.layout.fragment_life, container, false);




    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {





            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }

        final MainActivity mainA=(MainActivity)this.getActivity();
        mainA.IsSearchButton=false;
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mainA.setSupportActionBar(toolbar);





        mainA.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainA.getSupportActionBar().setDisplayShowTitleEnabled(true);

        mainA.getSupportActionBar().setTitle(getString(R.string.life));


        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open navigation drawer when click navigation back button

                mainA.drawer.openDrawer(GravityCompat.START);
            }
        });


        try {


            if (mainA.mFirebaseAnalytics != null) {

                Bundle params = new Bundle();





                mainA.mFirebaseAnalytics.logEvent("life_menu", params);
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        wv=(WebView)view.findViewById(R.id.webview);
        final ProgressBar pb=(ProgressBar) view.findViewById(R.id.pB1);




        wv.setSystemUiVisibility(View.VISIBLE);
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
       // wv.setWebViewClient(new WebViewAuth.MyWebViewClient());
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);

        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);


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


        JSONObject nb=new JSONObject();
        try {
            nb.put("accessToken", StaticVar.access_token);
            nb.put("access_token",  StaticVar.access_token);
            nb.put("refreshToken",StaticVar.refresh_token);
            nb.put("refresh_token", StaticVar.refresh_token);
            nb.put("expiresIn", StaticVar.expires_in);
            nb.put("expires_in", StaticVar.expires_in);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
        String jsonData=nb.toString();
        String EncodeData="";
        try {
             EncodeData = URLEncoder.encode(jsonData, "UTF-8");
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        String url="";
        if (StaticVar.DevMode)
         url = "https://staging.afrostream.tv/"+StaticVar.CountryCode.toLowerCase()+"/life/?wvmtok=" + EncodeData;
        else
             url = "https://www.afrostream.tv/"+StaticVar.CountryCode.toLowerCase()+"/life/?wvmtok=" + EncodeData;
        wv.loadUrl(url);


    }



}
