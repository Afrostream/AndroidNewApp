package tv.afrostream.app.fragments;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.afrostream.app.adapters.ListFavorisMoviesCardAdapter;
import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.adapters.CategorieMoviesCardAdapter;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by bahri on 23/01/2017.
 */




public class CategoriesFragement extends Fragment {


    private Toast toast;
    String idcat="";
    String catName="";
    ProgressBar loading_spinner=null;
    public String  TAG=HomeFragment.class.getSimpleName();

   public ArrayList<MovieItemModel> MoviesListFavoris;
    RecyclerView rc;

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }



    public void DoResponseGetListFav(JSONArray response,Boolean ReloadList){




        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();

            MoviesListFavoris = new ArrayList<MovieItemModel>();

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
                        try{
                        JSONObject posterMovie  =movie.getJSONObject("poster");
                        String urlImageMovie= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=130&h=120&q=90&fm=jpg&facepad=1&crop=entropy&auto=compress&dpr="+StaticVar.densityPixel;
                        MoviesListFavoris.add(new MovieItemModel(titleMovie,labelMovie,urlImageMovie,movie,labelMovie));

                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

            }





            loading_spinner.setVisibility(View.GONE);

            if (ReloadList)
                makeGetCategorieMovies(StaticVar.access_token,idcat,rc);




        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
            loading_spinner.setVisibility(View.GONE);
            if (ReloadList)
                makeGetCategorieMovies(StaticVar.access_token,idcat,rc);
        }



    }
    public void makeGetListFav(final String access_token, final Boolean ReloadList) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


       if (ReloadList) loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/favoritesMovies/"+StaticVar.ApiUrlParams;


        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONArray response=new JSONArray(data) ;

                DoResponseGetListFav(response,ReloadList);
                //loading_spinner.setVisibility(View.GONE);


            } catch (Exception e) {
                e.printStackTrace();
                loading_spinner.setVisibility(View.GONE);
                // loading_spinner.setVisibility(View.GONE);
            }
        }
        else{


            JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,urlJsonObj,null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            DoResponseGetListFav(response,ReloadList);
                            // loading_spinner.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  loading_spinner.setVisibility(View.GONE);.
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




    }


    public void makeDeleteMovieToFavoris(final String access_token, String MovieId, final ImageView fav, final ProgressBar loading_spinner) {


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

                        loading_spinner.setVisibility(GONE);
                        fav.setBackgroundResource(R.drawable.favbutton);
                        fav.setImageResource(R.drawable.favbutton);


                        AnimationUtils.rotateX(fav,100);

                        makeGetListFav(StaticVar.access_token,false);



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
    public void makeAddMovieToFavoris(final String access_token, String MovieId, final ImageView fav, final ProgressBar loading_spinner) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }

        fav.setVisibility(View.GONE);
        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/favoritesMovies/";


        JSONObject params=new JSONObject();
        try {
            params.put("_id", MovieId);
        }catch (Exception ee)
        {
            loading_spinner.setVisibility(View.GONE);
            fav.setVisibility(View.VISIBLE);
            ee.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST,urlJsonObj,params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loading_spinner.setVisibility(View.GONE);

                        fav.setBackgroundResource(R.drawable.deletefav);
                        fav.setImageResource(R.drawable.deletefav);
                        fav.setVisibility(View.VISIBLE);




                      AnimationUtils.rotateX(fav,100);
                        showToast("Ajouté à la liste des favoris"  );

                        makeGetListFav(StaticVar.access_token,false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
                loading_spinner.setVisibility(View.GONE);
                fav.setVisibility(View.VISIBLE);
                error.printStackTrace();
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());
                try {
                    String errorStr=error.getMessage();
                    if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                    showToast("Error " + errorStr );
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


    public void DoResponseGetCatMovies(JSONObject res,RecyclerView rc){




        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();

            ArrayList<MovieItemModel> MoviesList = new ArrayList<MovieItemModel>();
            JSONArray response=res.getJSONArray("movies");
            for (int i = 0; i < response.length(); i++) {

                try{

                        JSONObject movie = (JSONObject) response.get(i);



                        Log.d(TAG,"film "+movie.getString("title"));


                        String titleMovie=movie.getString("title");
                        String labelMovie="";
                        try{
                         labelMovie=movie.getString("genre");
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }
                        try{
                        JSONObject posterMovie  =movie.getJSONObject("poster");
                        String urlImageMovie= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=130&h=120&q=100&fm=jpg&facepad=1&auto=format&dpr="+StaticVar.densityPixel;
                        MoviesList.add(new MovieItemModel(titleMovie,labelMovie,urlImageMovie,movie,labelMovie));
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

            }

            CategorieMoviesCardAdapter adapter = new CategorieMoviesCardAdapter(MoviesList);

            rc.setAdapter(adapter);

            loading_spinner.setVisibility(View.GONE);



        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
            loading_spinner.setVisibility(View.GONE);
        }



    }
    private void makeGetCategorieMovies(final String access_token, String catID, final RecyclerView rc) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/categorys/"+catID+StaticVar.ApiUrlParams;


        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONObject response=new JSONObject(data) ;

                DoResponseGetCatMovies(response,rc);
                //loading_spinner.setVisibility(View.GONE);


            } catch (Exception e) {
                e.printStackTrace();
                loading_spinner.setVisibility(View.GONE);
                // loading_spinner.setVisibility(View.GONE);
            }
        }
        else{


            JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,urlJsonObj,null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {


                            DoResponseGetCatMovies(response,rc);
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

        //  int SomeInt = getArguments().getInt("someInt", 0);

        StaticVar.catFragment=this;
         idcat = getArguments().getString("idcat", "0");

        catName= getArguments().getString("catname", "0");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragement_home_categorie, container, false);






    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        final MainActivity mainA=(MainActivity)this.getActivity();
        mainA.IsSearchButton=false;
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mainA.setSupportActionBar(toolbar);

        loading_spinner=(ProgressBar) view.findViewById(R.id.loading_spinner);

        mainA.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainA.getSupportActionBar().setDisplayShowTitleEnabled(true);

        mainA.getSupportActionBar().setTitle(catName);

        toolbar.setNavigationIcon(R.drawable.ic_menu);
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
                params.putString("category_name", catName);




                mainA.mFirebaseAnalytics.logEvent("in_category", params);
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }



       // CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);

         rc = (RecyclerView) view.findViewById(R.id.lstCatMovies);
        rc.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        rc.setHasFixedSize(true);

       // AppBarLayout appbar = (AppBarLayout)view.findViewById(R.id.app_bar_layout);

        makeGetListFav(StaticVar.access_token,true);


    }



}