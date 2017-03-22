package tv.afrostream.app.activitys;

/**
 * Created by bahri on 20/02/2017.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.fragments.CategoriesFragement;
import tv.afrostream.app.fragments.HomeFragment;
import tv.afrostream.app.fragments.HomeLocalFragment;
import tv.afrostream.app.fragments.ListFavorisFragment;
import tv.afrostream.app.models.SerieSaisonModel;
import tv.afrostream.app.utils.StaticVar;



public class MainActivityLocal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toast toast;
    List<String> List_categories_ID;
    List<String> List_categories_Name;


    ProgressBar loading_spinner=null;
    public String  TAG=MainActivityLocal.class.getSimpleName();

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

    public DrawerLayout drawer=null;

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
        ActivityCompat.requestPermissions(MainActivityLocal.this,new String[]{

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




            navName.setText(user_first_name +" "+user_last_name);
            navEmail.setText(user_email);


            makeGetUserImage(user_picture_url,navUserPic);


            JSONObject subscriptionsStatus =response.getJSONObject("subscriptionsStatus");

            JSONArray subscriptions=null;
            try {
                subscriptions = subscriptionsStatus.getJSONArray("subscriptions");
            }catch (Exception ee)
            {

            }

            if (subscriptions==null || subscriptions.length()==0)


            {

                Intent nb=new Intent(this,PaymentActivity.class);
                startActivity(nb);
                MainActivityLocal.this.finish();
                   /* if (findViewById(R.id.main_fragment_container) != null) {

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.replace(R.id.main_fragment_container, new ListPlansSubscriptionFragment());

                        ft.commit();
                    }*/
            }else{

                JSONObject subscription=(JSONObject)subscriptions.get(0);


                String isActive=subscription.getString("isActive");

                if (isActive.equals("yes")) {

                    if (findViewById(R.id.main_fragment_container) != null) {
                        FrameLayout nb=(FrameLayout)findViewById(R.id.main_fragment_container);
                        nb.setBackgroundResource(android.R.color.white);

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.replace(R.id.main_fragment_container, new HomeFragment());

                        ft.commit();
                    }
                }else
                {
                    String subStatus=subscription.getString("subStatus");




                    if (subStatus.contains("future"))
                    {
                        showToast(getString(R.string.futuresubscription));
                        finishActivity(0);
                        return;

                    }else if (subStatus.contains("expired"))
                    {

                    }else if (subStatus.contains("canceled"))
                    {

                    }else
                    {



                    }


                    Intent nb=new Intent(this,PaymentActivity.class);
                    startActivity(nb);
                    MainActivityLocal.this.finish();


                }

            }










        } catch (Exception e) {


            e.printStackTrace();
            showToast("Error: " + e.getMessage());
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
                        showToast("Error " + errorStr );
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



    public void OnResponseGetCategories(JSONArray response,final SubMenu catMenu){
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
            showToast("Error: " + e.getMessage());
        }


        drawer.requestLayout();
    }


    private void makeGetCategories(final String access_token, final NavigationView navigationView) {


        Menu mn=  navigationView.getMenu();
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

                OnResponseGetCategories(response,catMenu);


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

                            OnResponseGetCategories(response,catMenu);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loading_spinner.setVisibility(View.GONE);
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    try {
                        String errorStr=error.getMessage();
                        if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                        showToast("Error " + errorStr );
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
                        String errorStr=error.getMessage();
                        if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                        showToast("Error " + errorStr );
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
    protected void onCreate(Bundle savedInstanceState) {






        super.onCreate(savedInstanceState);



        StaticVar.mainActLocal=this;


        setContentView(R.layout.activity_main_local);
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







        //  lstMenuCat =(ListView) this.findViewById(R.id.menuListViewCat);



        navName=(TextView)    navigationView.getHeaderView(0).findViewById(R.id.txtnavname);
        navEmail=(TextView)    navigationView.getHeaderView(0).findViewById(R.id.txtnavemail);
        navUserPic=(ImageView)    navigationView.getHeaderView(0).findViewById(R.id.userimageView);

        txtdeconnect=(TextView)      navigationView.getHeaderView(0).findViewById(R.id.txtdeconnect);

      /*  TextView lblMenuHome=(TextView) this.findViewById(R.id.lblMenuHome);
        TextView lblCategories=(TextView) this.findViewById(R.id.lblCategories);

        TextView lblMenuFavoris=(TextView) this.findViewById(R.id.lblMenuFavoris);*/

        loading_spinner.setVisibility(View.VISIBLE);




                    if (findViewById(R.id.main_fragment_container) != null) {

                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                        ft.replace(R.id.main_fragment_container, new HomeLocalFragment());

                        ft.commit();
                        loading_spinner.setVisibility(View.GONE);
                    }






        txtdeconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {


            @Override
            public void run() {
                try {


                        Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getFilesDir()+ File.separator+"userpic.png");

                        navUserPic.setImageBitmap(bitmap);

                        sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);


                        user_first_name=sharedpreferences.getString("user_first_name", "");


                        user_last_name=sharedpreferences.getString("user_last_name", "");





                        user_email=sharedpreferences.getString("user_email", "");



                        navName.setText(user_first_name +" "+user_last_name);
                        navEmail.setText(user_email);



                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

            }},100);









        ///makeGetUserInfo(StaticVar.access_token);
        //  makeGetCategories(StaticVar.access_token,lstMenuCat);

       // makeGetCategories(StaticVar.access_token,navigationView);

        Menu mn=  navigationView.getMenu();
        mn.clear();
        mn.add(R.string.home).setIcon(R.drawable.home);

        mn.add(R.string.mydownload).setIcon(R.drawable.downloadicon);









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

            Intent nb =new Intent(MainActivityLocal.this,SearchActivity.class);
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

            if (title.equals(getString(R.string.home)))
            {

                drawer.closeDrawer(GravityCompat.START);
                if (findViewById(R.id.main_fragment_container) != null) {

                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                    ft.replace(R.id.main_fragment_container, new HomeLocalFragment());

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

