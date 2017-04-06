package tv.afrostream.app.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.BuildConfig;
import tv.afrostream.app.fragments.HomeFragment;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.R;
import tv.afrostream.app.adapters.SearchMoviesCardAdapter;

/**
 * Created by bahri on 26/01/2017.
 */

public class SearchActivity  extends AppCompatActivity {



    private Toast toast;
    RecyclerView rc;
    ProgressBar loading_spinner=null;
    public String  TAG=HomeFragment.class.getSimpleName();
    private FirebaseAnalytics mFirebaseAnalytics;

    android.os.Handler handler=null;
    Runnable nb=null;

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }





    public void DoResponseSearchMovies(JSONObject res, RecyclerView rc){




        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();

            ArrayList<MovieItemModel> MoviesList = new ArrayList<MovieItemModel>();
            JSONArray response=res.getJSONArray("hits");
            for (int i = 0; i < response.length(); i++) {

                try{

                JSONObject movie = (JSONObject) response.get(i);



                Log.d(TAG,"film "+movie.getString("title"));


                String titleMovie=movie.getString("title");
                    String labelMovie="";
                try {
                     labelMovie = movie.getString("genre");
                 }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                try {
                    JSONObject posterMovie = movie.getJSONObject("poster");
                    String urlImageMovie = posterMovie.getString("imgix") + "?&crop=entropy&fit=min&w=350&h=300&q=65&fm=jpg&facepad=1&crop=faces&auto=format";
                    ;
                    MoviesList.add(new MovieItemModel(titleMovie, labelMovie, urlImageMovie, movie,labelMovie));
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }



            }

            SearchMoviesCardAdapter adapter = new SearchMoviesCardAdapter(MoviesList);

            rc.setAdapter(adapter);

            loading_spinner.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
            loading_spinner.setVisibility(View.GONE);
        }



    }
    private void makeSearchMovies(final String access_token, final String query, final RecyclerView rc) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/movies/search"+StaticVar.ApiUrlParams;


        JSONObject params=new JSONObject();
        try {
            params.put("query", query);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,urlJsonObj,params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                            DoResponseSearchMovies(response,rc);

                            try {


                                if (mFirebaseAnalytics != null) {

                                    Bundle params = new Bundle();
                                    params.putString("query", query);


                                    mFirebaseAnalytics.logEvent("search", params);
                                }
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }
                            // loading_spinner.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  loading_spinner.setVisibility(View.GONE);
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



    @Override
    public void onCreate(Bundle savedInstanceState) {


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
        setContentView(R.layout.activity_search);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        StaticVar.searchAct=this;

        loading_spinner=(ProgressBar) this.findViewById(R.id.loading_spinner);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }



        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);


         rc = (RecyclerView) this.findViewById(R.id.lstCatMovies);
        rc.setLayoutManager(new LinearLayoutManager(this));

        rc.setHasFixedSize(true);

        // AppBarLayout appbar = (AppBarLayout)view.findViewById(R.id.app_bar_layout);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem searchMenuItem=menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setIconified(false);
        searchView.requestFocus();

        handler = new android.os.Handler(Looper.getMainLooper());




        // searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeSearchMovies(StaticVar.access_token,query,rc);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {


               if (nb!=null) {
                   handler.removeCallbacks(nb);
               }

                 nb=new Runnable() {
                    @Override
                    public void run() {
                        makeSearchMovies(StaticVar.access_token,newText,rc);
                    }
                };



                handler.postDelayed(nb,700);




                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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
