package tv.afrostream.app.activitys;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.afrostream.app.adapters.SerieSaisonListAdapter;
import tv.afrostream.app.fragments.MyDownloadFragment;
import tv.afrostream.app.models.SerieItemModel;
import tv.afrostream.app.models.SerieSaisonModel;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.BuildConfig;
import tv.afrostream.app.fragments.CategoriesFragement;
import tv.afrostream.app.fragments.HomeFragment;
import tv.afrostream.app.fragments.ListFavorisFragment;
import tv.afrostream.app.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toast toast;
    List<String> List_categories_ID;
    List<String> List_categories_Name;


    ProgressBar loading_spinner=null;
    public String  TAG=MainActivity.class.getSimpleName();

    public String user_id="";
    public String user_first_name="";
    public String user_last_name="";
    public String user_picture_url="";
    public String user_email="";
    TextView navName;
    TextView navEmail;
    ImageView navUserPic;
    ListView lstMenuCat;
    TextView txtdeconnect;
    public MenuItem searchMenuButton;
    public Boolean IsSearchButton=true;
public SharedPreferences sharedpreferences;
    int menuDrawerCount=0;
    boolean doubleBackToExitPressedOnce = false;

    public  DrawerLayout drawer=null;

    public FirebaseAnalytics mFirebaseAnalytics;

    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
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
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{

                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE},1
        );
    }

    private void makeGetImageInCach(String url) {


        ImageLoader mImageLoader;

        RequestQueue mRequestQueue= AppController.getInstance().getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

        });

        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

               // mImageView.setImageBitmap(response.getBitmap());

            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });



    }


    public void SavaBitmapToFile(Bitmap bitmap,String filename)
    {

        if (bitmap==null) return;
        try {
            File f = new File(getApplicationContext().getFilesDir(), filename);
            f.createNewFile();



            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }


    private void makeGetUserImage(String url, final ImageView mImageView) {


         ImageLoader mImageLoader;

         RequestQueue mRequestQueue=AppController.getInstance().getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

        });

        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                mImageView.setImageBitmap(response.getBitmap());
                SavaBitmapToFile(response.getBitmap(),"userpic.png");

            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });



    }

    public void DoResponseGetUserInfo(JSONObject response)
    {

            // Log.d(TAG, response.toString());

            try {

                loading_spinner.setVisibility(View.GONE);


                user_id=response.getString("_id");

                user_first_name=response.getString("first_name");


                user_last_name=response.getString("last_name");


                user_picture_url=response.getString("picture");


                user_email=response.getString("email");

                //showToast("test "+response.toString());

                if (user_first_name.equals("null"))user_first_name="";
                if (user_last_name.equals("null"))user_last_name="";
                if (user_picture_url.equals("null"))user_picture_url="";
                if (user_email.equals("null"))user_email="";

                StaticVar.user_id=user_id;


                Log.e("GetInfoUser","user_id "+user_id);

                navName.setText(user_first_name +" "+user_last_name);
                navEmail.setText(user_email);


                if (StaticVar.facebook_image_profil_url.equals(""))
                makeGetUserImage(user_picture_url,navUserPic);
                else
                    makeGetUserImage(StaticVar.facebook_image_profil_url,navUserPic);

                Log.e("GetInfoUser","1");

                SharedPreferences.Editor editor = sharedpreferences.edit();



                editor.putString("user_first_name", user_first_name);
                editor.putString("user_last_name", user_last_name);
                editor.putString("user_email", user_email);

                editor.commit();

                Log.e("GetInfoUser","2");


                if (StaticVar.FirstLaunch) {

                    if (findViewById(R.id.main_fragment_container) != null) {
                        FrameLayout nb = (FrameLayout) findViewById(R.id.main_fragment_container);
                        nb.setBackgroundResource(android.R.color.white);

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.replace(R.id.main_fragment_container, new HomeFragment());
                        Log.e("GetInfoUser", "2.5");
                        ft.commitAllowingStateLoss();
                        StaticVar.FirstLaunch=false;

                    }


                }





                Log.e("GetInfoUser","2.9");

                JSONArray subscriptions=null;
                try {
                    JSONObject subscriptionsStatus =response.getJSONObject("subscriptionsStatus");
                    try {
                        subscriptions = subscriptionsStatus.getJSONArray("subscriptions");
                    }catch (Exception ee)
                    {}

                    Log.e("GetInfoUser","3");

                    if (subscriptions==null || subscriptions.length()==0)


                    {

                        try {


                            if (mFirebaseAnalytics != null) {

                                Bundle params = new Bundle();
                                params.putString("paid", "no");

                                mFirebaseAnalytics.logEvent("login_user", params);
                            }
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                        StaticVar.subscription=false;
                        Intent nb=new Intent(this,PaymentActivity.class);
                        nb.putExtra("user_first_name",user_first_name);
                        nb.putExtra("user_last_name",user_last_name);
                        startActivity(nb);
                        MainActivity.this.finish();

                    }else{
                        Log.e("GetInfoUser","4");

                        JSONObject subscription=(JSONObject)subscriptions.get(0);
                        Log.e("GetInfoUser","5");

                        String isActive=subscription.getString("isActive");

                        if (isActive.equals("yes")) {
                            StaticVar.subscription=true;
                            Log.e("GetInfoUser","6");

                            /*if (findViewById(R.id.main_fragment_container) != null) {
                                FrameLayout nb=(FrameLayout)findViewById(R.id.main_fragment_container);
                                nb.setBackgroundResource(android.R.color.white);

                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                                ft.replace(R.id.main_fragment_container, new HomeFragment());

                                ft.commit();
                                Log.e("GetInfoUser","7");
                            }*/



                            try {


                                if (mFirebaseAnalytics != null) {

                                    Bundle params = new Bundle();
                                    params.putString("paid", "yes");

                                    mFirebaseAnalytics.logEvent("login_user", params);
                                }
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }


                        }else
                        {
                            String subStatus=subscription.getString("subStatus");




                            if (subStatus.contains("future"))
                            {

                                try {


                                    if (mFirebaseAnalytics != null) {

                                        Bundle params = new Bundle();
                                        params.putString("paid", "no");
                                        params.putString("subStatus", "future");

                                        mFirebaseAnalytics.logEvent("login_user", params);
                                    }
                                }catch (Exception ee)
                                {
                                    ee.printStackTrace();
                                }
                                showToast(getString(R.string.futuresubscription));
                                finishActivity(0);
                                return;

                            }else if (subStatus.contains("expired"))
                            {
                                try {


                                    if (mFirebaseAnalytics != null) {

                                        Bundle params = new Bundle();
                                        params.putString("paid", "no");
                                        params.putString("subStatus", "expired");

                                        mFirebaseAnalytics.logEvent("login_user", params);
                                    }
                                }catch (Exception ee)
                                {
                                    ee.printStackTrace();
                                }
                                showToast("expired account");
                            }else if (subStatus.contains("canceled"))
                            {
                                try {


                                    if (mFirebaseAnalytics != null) {

                                        Bundle params = new Bundle();
                                        params.putString("paid", "no");
                                        params.putString("subStatus", "canceled");

                                        mFirebaseAnalytics.logEvent("login_user", params);
                                    }
                                }catch (Exception ee)
                                {
                                    ee.printStackTrace();
                                }
                                showToast("canceled account");

                            } else if (subStatus.contains("active"))
                            {





                            }

                            StaticVar.subscription=false;
                            Intent nb=new Intent(this,PaymentActivity.class);
                            nb.putExtra("user_first_name",user_first_name);
                            nb.putExtra("user_last_name",user_last_name);
                            startActivity(nb);
                            MainActivity.this.finish();
                        /*Intent nb=new Intent(this,PaymentActivity.class);
                        startActivity(nb);
                        MainActivity.this.finish();*/


                        }

                    }

                }catch (Exception ee)
                {
                    ee.printStackTrace();

                }






                Log.e("GetInfoUser","8");





            } catch (Exception e) {


                e.printStackTrace();
                showToast("Error GetInfoUser: " + e.getMessage());
            }
            loading_spinner.setVisibility(View.GONE);

    }
    private void makeGetUserInfo(final String access_token) {


        if (access_token.equals("") )
        {

           // showToast(this.getString(R.string.activity_main_empty_access_token) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me";


        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                loading_spinner.setVisibility(View.GONE);

                JSONObject response=new JSONObject(data) ;

                DoResponseGetUserInfo(response);


            } catch (Exception e) {
                loading_spinner.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
        else {


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    loading_spinner.setVisibility(View.GONE);

                    DoResponseGetUserInfo(response);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    loading_spinner.setVisibility(View.GONE);

                    try {
                        String errorStr=error.getMessage();
                        if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                        showToast("Error Get User Info" + errorStr );
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
                    headers.put("Authorization", "Bearer "+access_token);
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }




    }



    public void OnResponseGetCategories(JSONArray response,final SubMenu catMenu,final Menu mn){
        Log.d(TAG, response.toString());

        try {


            List<String> List_file=new ArrayList<String>();
            List_categories_ID=new ArrayList<String>();
            List_categories_Name=new ArrayList<String>();
            for (int i = 0; i < response.length(); i++) {

                JSONObject cat = (JSONObject) response
                        .get(i);

                String label = cat.getString("label");
                int _id = cat.getInt("_id");


                List_file.add(label);
                List_categories_Name.add(label);
                List_categories_ID.add(""+_id);
                catMenu.add(label);


                //Create an adapter for the listView and add the ArrayList to the adapter.


                              /*  mn.add(""+i);
                                MenuItem mi = mn.getItem(i);

                                mi.setTitle(label);*/

                //   mi.getActionView().setTag(_id);





            }

          //  lstMenuCat.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,List_file));

        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error GetCat: " + e.getMessage());
        }
        final SubMenu catCGU = mn.addSubMenu(R.string.afrostream).setIcon(R.drawable.categories);
        catCGU.add(R.string.MentionsLegal);
        catCGU.add(R.string.CGU);
        catCGU.add(R.string.contact);



        drawer.requestLayout();
    }


    private void makeGetCategories(final String access_token, final NavigationView navigationView) {


        final Menu mn=  navigationView.getMenu();
        mn.clear();
        mn.add(R.string.home).setIcon(R.drawable.home);
        mn.add(R.string.listefavoris).setIcon(R.drawable.favmenu);
        mn.add(R.string.mydownload).setIcon(R.drawable.downloadicon);
        final SubMenu catMenu = mn.addSubMenu(R.string.categories).setIcon(R.drawable.categories);


        menuDrawerCount=mn.size();


        if (access_token.equals("") )
        {

        //    showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/categorys/menu"+StaticVar.ApiUrlParams;




        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                loading_spinner.setVisibility(View.GONE);

                JSONArray response=new JSONArray(data) ;

                OnResponseGetCategories(response,catMenu,mn);


            } catch (Exception e) {
                loading_spinner.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
        else {



            JsonArrayRequest req = new JsonArrayRequest(urlJsonObj,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            loading_spinner.setVisibility(View.GONE);

                            OnResponseGetCategories(response,catMenu,mn);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    final SubMenu catCGU = mn.addSubMenu(R.string.afrostream).setIcon(R.drawable.categories);
                    catCGU.add(R.string.MentionsLegal);
                    catCGU.add(R.string.CGU);
                    catCGU.add(R.string.contact);
                    loading_spinner.setVisibility(View.GONE);
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    try {
                        String errorStr=error.getMessage();
                        if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                        showToast("Error HomeCatMovie " + errorStr );
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


            AppController.getInstance().addToRequestQueue(req);

        }


    }

    public void  DoResponseMovieByIDForNotification (JSONObject response) {
        Log.d(TAG, response.toString());

        ArrayList<SerieSaisonModel> CatMoviesList = new ArrayList<SerieSaisonModel>();

        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();


            JSONObject Movie = (JSONObject) response;

            Intent intent = new Intent(this, MovieDetailsActivity.class);



            intent.putExtra ("movieInfo",Movie.toString());


            startActivity(intent);






        } catch (Exception e) {
            e.printStackTrace();
            // showToast("Error: " + e.getMessage());
        }



    }
    private void makeMovieByIDForNotification(final String access_token,  String idMovie) {



        if (access_token.equals("") )
        {

           // showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        // loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/movies/"+idMovie+StaticVar.ApiUrlParams;



        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONObject response=new JSONObject(data) ;

                DoResponseMovieByIDForNotification(response);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    DoResponseMovieByIDForNotification(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    try {
                        if(error.networkResponse != null && error.networkResponse.data != null){
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson=error2.getMessage();
                            JSONObject errorJ=new JSONObject(errorJson);
                            String MessageError=errorJ.getString("error");
                            showToast("Error MovieNotif: " + MessageError);

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("MainAct","New Intent");


        if (intent.getExtras() != null) {

            sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);


            Log.e("MainAct","MainNotif");

            String user= sharedpreferences.getString(StaticVar.usernamePref,"");
            String pass= sharedpreferences.getString(StaticVar.passwordPref,"");


            String access_token= sharedpreferences.getString("access_token","");
            String refresh_token= sharedpreferences.getString("refresh_token","");


            if (!refresh_token.equals(""))StaticVar.refresh_token=refresh_token;

            if (!access_token.equals(""))
            {
                StaticVar.access_token=access_token;
            }else
            {
                Intent nb= new Intent(this,LoginActivity.class);

                for (String key : intent.getExtras().keySet()) {
                    String value = intent.getExtras().getString(key);
                    nb.putExtra(key,value);
                }


                startActivity(nb);
                this.finish();
            }


            try{

                String action ="";
                try {
                    action = intent.getExtras().getString("action");
                }catch (Exception ee){}
                String movieID ="";
                try {
                    movieID=intent.getExtras().getString("movieID");
                }catch (Exception ee){}

                String open_url ="";
                try {
                    open_url=intent.getExtras().getString("open_url");
                }catch (Exception ee){}




                Log.e("MainAct","Action : "+action);

                if (action.equals("open_video"))
                {
                    makeMovieByIDForNotification(StaticVar.access_token,movieID);
                    //showToast(movieID);
                }else  if (action.equals("open_url")) {
                    if (!open_url.equals("")) {
                        if (!open_url.startsWith("http://") && !open_url.startsWith("https://"))
                            open_url = "http://" + open_url;

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(open_url));
                        startActivity(i);
                        this.finish();
                    }

                }

            }catch (Exception ee)
            {
                ee.printStackTrace();
            }



        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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


     /*   if (BuildConfig.DEBUG) {
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
     Log.e("MainAct","Create");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        if ( StaticVar.access_token.equals(""))
        {

            sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);

            String user= sharedpreferences.getString(StaticVar.usernamePref,"");
            String pass= sharedpreferences.getString(StaticVar.passwordPref,"");


            String access_token= sharedpreferences.getString("access_token","");
            String refresh_token= sharedpreferences.getString("refresh_token","");

            if (!access_token.equals("")) {
                StaticVar.access_token = access_token;
                if (!refresh_token.equals("")) StaticVar.refresh_token = refresh_token;
            }else
            {
                Intent nb= new Intent(this,LoginActivity.class);

                for (String key : getIntent().getExtras().keySet()) {
                    String value = getIntent().getExtras().getString(key);
                    nb.putExtra(key,value);
                }


                startActivity(nb);
                this.finish();
            }


        }







        if (getIntent().getExtras() != null) {




            Log.e("MainAct","MainNotif");









            try{

                String action ="";
                try {
                    action = getIntent().getExtras().getString("action");
                }catch (Exception ee){}
                String movieID ="";
                try {
                    movieID=getIntent().getExtras().getString("movieID");
                }catch (Exception ee){}

                String open_url ="";
                try {
                    open_url=getIntent().getExtras().getString("open_url");
                }catch (Exception ee){}




                Log.e("MainAct","Action : "+action);

                if (action.equals("open_video"))
                {
                    makeMovieByIDForNotification(StaticVar.access_token,movieID);
                    //showToast(movieID);
                }else  if (action.equals("open_url")) {
                    if (!open_url.equals("")) {
                        if (!open_url.startsWith("http://") && !open_url.startsWith("https://"))
                            open_url = "http://" + open_url;

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(open_url));
                        startActivity(i);
                        this.finish();
                    }

                }

            }catch (Exception ee)
            {
                ee.printStackTrace();
            }



        }



        setContentView(R.layout.activity_main);


        if (sharedpreferences==null)  sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);
        loading_spinner=(ProgressBar) this.findViewById(R.id.loading_spinner);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }




        StaticVar.densityPixel = getResources().getDisplayMetrics().density;


      //  lstMenuCat =(ListView) this.findViewById(R.id.menuListViewCat);



        navName=(TextView)    navigationView.getHeaderView(0).findViewById(R.id.txtnavname);
        navEmail=(TextView)    navigationView.getHeaderView(0).findViewById(R.id.txtnavemail);
        navUserPic=(ImageView)    navigationView.getHeaderView(0).findViewById(R.id.userimageView);

        txtdeconnect=(TextView)      navigationView.getHeaderView(0).findViewById(R.id.txtdeconnect);

      /*  TextView lblMenuHome=(TextView) this.findViewById(R.id.lblMenuHome);
        TextView lblCategories=(TextView) this.findViewById(R.id.lblCategories);

        TextView lblMenuFavoris=(TextView) this.findViewById(R.id.lblMenuFavoris);*/

        StaticVar.mainAct=this;


        txtdeconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (sharedpreferences != null) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();


                        editor.putString("auto_login", "false");


                        editor.commit();
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

                finish();
            }
        });



    makeGetUserInfo(StaticVar.access_token);
    //  makeGetCategories(StaticVar.access_token,lstMenuCat);

    makeGetCategories(StaticVar.access_token, navigationView);









    /*   lblMenuFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.main_fragment_container, new ListFavorisFragment());

                    ft.commit();
                }
            }
        });





        lblMenuHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.main_fragment_container, new HomeFragment());

                    ft.commit();
                }

            }
        });




        lstMenuCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



drawer.closeDrawer(GravityCompat.START);
                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    CategoriesFragement nb=new CategoriesFragement();
                    Bundle args = new Bundle();

                    args.putString("idcat", List_categories_ID.get(position));
                    args.putString("catname", lstMenuCat.getItemAtPosition(position).toString());

                    nb.setArguments(args);

                    ft.replace(R.id.main_fragment_container, nb);

                    ft.commit();
                }

            }
        });


     */
























    }

    @Override
    public void onBackPressed() {

      //  super.onBackPressed();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {

                try {

                    if (sharedpreferences != null) {

                        SharedPreferences.Editor editor = sharedpreferences.edit();


                        editor.putString("auto_login", "false");


                        editor.commit();
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            showToast(getString(R.string.backexit));

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

         searchMenuButton = menu.findItem(R.id.action_search);
        if (!IsSearchButton)searchMenuButton.setVisible(false);


     /*   SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_search) {

            Intent nb =new Intent(MainActivity.this,SearchActivity.class);
            this.startActivity(nb);

        }


        return super.onOptionsItemSelected(item);
    }

    public int getMenuPositionByTitle(String title)
    {

        for (int i=0;i<List_categories_ID.size();i++)
        {
            String t=(String)List_categories_Name.get(i);
            if (t.equals(title))
                return  i;
        }
        return -1;

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String  title = item.getTitle().toString();
        int position =getMenuPositionByTitle(title);


        if (position>-1) {



                drawer.closeDrawer(GravityCompat.START);


                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    CategoriesFragement nb = new CategoriesFragement();
                    Bundle args = new Bundle();

                    args.putString("idcat", List_categories_ID.get(position));
                    args.putString("catname", title);

                    nb.setArguments(args);

                    ft.replace(R.id.main_fragment_container, nb);

                    ft.commit();
                }



        }else
        {
            if (title.equals(getString( R.string.CGU)))
            {
                Intent nb = new Intent(MainActivity.this, WebViewAuth.class);
                nb.putExtra("url", "https://www.afrostream.tv/"+StaticVar.CountryCode.toLowerCase()+"/cgu");


                startActivity(nb);


            }

            if (title.equals(getString(R.string.MentionsLegal)))
            {
                Intent nb = new Intent(MainActivity.this, WebViewAuth.class);
                nb.putExtra("url", "https://www.afrostream.tv/"+StaticVar.CountryCode.toLowerCase()+"/legals");


                startActivity(nb);


            }
            if (title.equals(getString(R.string.contact)))
            {
                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("text/plain");
               // intent.putExtra(Intent.EXTRA_SUBJECT, "Subject of email");
                //intent.putExtra(Intent.EXTRA_TEXT, "Body of email");
                intent.setData(Uri.parse("mailto:support@afrostream.tv")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);


            }
            if (title.equals(getString(R.string.home)))
            {

                drawer.closeDrawer(GravityCompat.START);
                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.main_fragment_container, new HomeFragment());

                    ft.commit();
                }

            }else if (title.equals(getString( R.string.listefavoris)))
            {

                drawer.closeDrawer(GravityCompat.START);
                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.main_fragment_container, new ListFavorisFragment());

                    ft.commit();
                }

            }else if (title.equals(getString(R.string.mydownload)))
            {

               /* drawer.closeDrawer(GravityCompat.START);
                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.main_fragment_container, new MyDownloadFragment());

                    ft.commit();
                }*/
                Intent nb=new Intent(this,MyDownloadActivity.class);
                startActivity(nb);

            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
