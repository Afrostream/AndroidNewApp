package tv.afrostream.app.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.afrostream.app.services.DownloadFileService;
import tv.afrostream.app.services.LocalModeService;
import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.models.CatMoviesModel;
import tv.afrostream.app.adapters.ImageSliderAdapter;
import tv.afrostream.app.adapters.ListHomeCatAdapter;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;

/**
 * Created by bahri on 20/01/2017.
 */

public class HomeFragment extends Fragment  implements  ViewPager.OnPageChangeListener {


    private Toast toast;

    ProgressBar loading_spinner=null;
    public String  TAG=HomeFragment.class.getSimpleName();

    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private Handler slide_handler;
    private int slide_delay = 4000; //milliseconds
    private int slide_page = 0;
    private Runnable slide_runnable;



    private void showToast(String message) {
        try {
            if (toast != null) {
                toast.cancel();
                toast = null;
            }
            toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
            toast.show();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    public void  SaveSlideJsonAndCallService(JSONArray object) throws IOException {
        try {
            String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += "slides.afj";
            File data = new File(path);
            if (!data.createNewFile()) {
                data.delete();
                data.createNewFile();
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(data));
            output.write(object.toString());
            output.close();



            Intent intent = new Intent(StaticVar.mainAct, LocalModeService.class);

            intent.putExtra("action","slides");

            if ( StaticVar.mainAct!=null) {
                StaticVar.mainAct.startService(intent);
            }

        }catch (Exception ee)
        {

        }


    }

    public void  SaveCatMoviesJsonAndCallService(JSONArray object) throws IOException {
        try {
            String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += "catmovies.afj";
            File data = new File(path);
            if (!data.createNewFile()) {
                data.delete();
                data.createNewFile();
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(data));
            output.write(object.toString());
            output.close();

            Intent intent = new Intent(StaticVar.mainAct, LocalModeService.class);

            intent.putExtra("action","CatMovies");

            if ( StaticVar.mainAct!=null) {
                StaticVar.mainAct.startService(intent);
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


    }

    public void OnReponseGetCategorieMovieHome(JSONArray response, RecyclerView catMoviesRecyclerView){
        Log.d(TAG, response.toString());

        ArrayList<CatMoviesModel> CatMoviesList = new ArrayList<CatMoviesModel>();

        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();
            List<String> List_file=new ArrayList<String>();
            for (int i = 0; i < response.length(); i++) {

                JSONObject cat = (JSONObject) response
                        .get(i);

                String label = cat.getString("label");
                int _id = cat.getInt("_id");
                JSONArray movies =cat.getJSONArray("movies");


                ArrayList<MovieItemModel> MoviesList = new ArrayList<MovieItemModel>();
                for (int x=0 ;x< movies.length();x++)
                {
                    try{
                        JSONObject movie= (JSONObject) movies.get(x);
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
                            String urlImageMovie = posterMovie.getString("imgix") + "?&crop=entropy&fit=min&w=300&h=250&q=75&fm=jpg&auto=format&dpr="+StaticVar.densityPixel;
                            MoviesList.add(new MovieItemModel(titleMovie, labelMovie, urlImageMovie, movie));
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }






                CatMoviesList.add(new CatMoviesModel(label,MoviesList));




                //Create an adapter for the listView and add the ArrayList to the adapter.


                              /*  mn.add(""+i);
                                MenuItem mi = mn.getItem(i);

                                mi.setTitle(label);*/

                //   mi.getActionView().setTag(_id);





            }

            ListHomeCatAdapter lstHomeCatAdapter = new ListHomeCatAdapter(CatMoviesList,false);

            catMoviesRecyclerView.setAdapter(lstHomeCatAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
        }



    }







    private void makeGetCategoriesMoviesHome(final String access_token, final RecyclerView catMoviesRecyclerView) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/categorys/meas"+StaticVar.ApiUrlParams;



        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONArray response=new JSONArray(data) ;

                OnReponseGetCategorieMovieHome(response,catMoviesRecyclerView);
                loading_spinner.setVisibility(View.GONE);


            } catch (Exception e) {
                e.printStackTrace();
                loading_spinner.setVisibility(View.GONE);
            }
        }
        else {


            JsonArrayRequest req = new JsonArrayRequest(urlJsonObj,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            loading_spinner.setVisibility(View.GONE);
                            OnReponseGetCategorieMovieHome(response,catMoviesRecyclerView);


                            try {
                                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP) {
                                    // Do something for lollipop and above versions
                                    StaticVar.mainAct.checkPermission();
                                    if (!StaticVar.mainAct.checkPermission()) {
                                        StaticVar.mainAct.requestPermission();
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


            AppController.getInstance().addToRequestQueue(req);
        }




    }


    public void DoResponseSliderMoviesHome(JSONArray response, final ViewPager viewPager){
        Log.d(TAG, response.toString());



        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();

            ArrayList<MovieItemModel> MoviesList = new ArrayList<MovieItemModel>();
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
                        String urlImageMovie= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=600&h=400&q=90&fm=jpg&&auto=format&dpr="+StaticVar.densityPixel;
                        MoviesList.add(new MovieItemModel(titleMovie,labelMovie,urlImageMovie,movie));
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }








                //Create an adapter for the listView and add the ArrayList to the adapter.


                              /*  mn.add(""+i);
                                MenuItem mi = mn.getItem(i);

                                mi.setTitle(label);*/

                //   mi.getActionView().setTag(_id);





            }

            final ImageSliderAdapter adapter = new ImageSliderAdapter(this.getActivity(),MoviesList,false);

            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(0);




            dotsCount = adapter.getCount();
            dots = new ImageView[dotsCount];
            pager_indicator.removeAllViews();

            for (int i = 0; i < dotsCount; i++) {
                dots[i] = new ImageView(this.getActivity());
                dots[i].setImageDrawable(ContextCompat.getDrawable(this.getActivity(), R.drawable.nonselecteditem_dot));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(4, 0, 4, 0);

                pager_indicator.addView(dots[i], params);
            }

            dots[0].setImageDrawable(ContextCompat.getDrawable(this.getActivity(),R.drawable.selecteditem_dot));


            if (slide_handler!=null) slide_handler.removeCallbacks(slide_runnable);

            slide_handler = new Handler();


            slide_runnable = new Runnable() {
                public void run() {
                    if (adapter.getCount() <= slide_page) {
                        slide_page = 0;
                    } else {
                        slide_page++;
                    }
                    viewPager.setCurrentItem(slide_page, true);
                    slide_handler.postDelayed(this, slide_delay);
                }
            };
            slide_handler.postDelayed(slide_runnable, slide_delay);









        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
        }



    }
    private void makeSliderMoviesHome(final String access_token, final ViewPager viewPager) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/categorys/1/spots"+StaticVar.ApiUrlParams;


        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONArray response=new JSONArray(data) ;

                DoResponseSliderMoviesHome(response,viewPager);
                //loading_spinner.setVisibility(View.GONE);


            } catch (Exception e) {
                e.printStackTrace();
               // loading_spinner.setVisibility(View.GONE);
            }
        }
        else{


            JsonArrayRequest req = new JsonArrayRequest(urlJsonObj,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            DoResponseSliderMoviesHome(response,viewPager);
                           // loading_spinner.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  //  loading_spinner.setVisibility(View.GONE);
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

    private void makeGetCategoriesMoviesHomeLocal(final String access_token) {


        if (access_token.equals("") )
        {


            return;
        }


        String urlJsonObj= StaticVar.BaseUrl+"/api/categorys/meas"+StaticVar.ApiUrlParams;//+"?withYoutubeTrailer=true";




            JsonArrayRequest req = new JsonArrayRequest(urlJsonObj,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            try {
                                SaveCatMoviesJsonAndCallService(response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }




                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    VolleyLog.d(TAG, "Error: " + error.getMessage());


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


            AppController.getInstance().addToRequestQueue(req);





    }

    private void makeSliderMoviesHomeLocal(final String access_token) {


        if (access_token.equals("") )
        {


            return;
        }



        String urlJsonObj= StaticVar.BaseUrl+"/api/categorys/1/spots"+StaticVar.ApiUrlParams; //+"&withYoutubeTrailer=true";





            JsonArrayRequest req = new JsonArrayRequest(urlJsonObj,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {


                            try {
                                SaveSlideJsonAndCallService( response);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            // loading_spinner.setVisibility(View.GONE);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //  loading_spinner.setVisibility(View.GONE);
                    VolleyLog.d(TAG, "Error: " + error.getMessage());


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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        try {
            slide_page = position;
            for (int i = 0; i < dotsCount; i++) {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this.getActivity(), R.drawable.nonselecteditem_dot));
            }

            dots[position].setImageDrawable(ContextCompat.getDrawable(this.getActivity(), R.drawable.selecteditem_dot));
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

       /* if (position + 1 == dotsCount) {
            btnNext.setVisibility(View.GONE);
            btnFinish.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnFinish.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onResume() {
        super.onResume();
        //if (slide_handler!=null)slide_handler.postDelayed(slide_runnable, slide_delay);

        try {

            if (dotsCount > 1) {
                if (slide_handler != null) slide_handler.removeCallbacks(slide_runnable);

                slide_handler = new Handler();


                slide_handler.postDelayed(slide_runnable, slide_delay);
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (slide_handler != null) slide_handler.removeCallbacks(slide_runnable);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //  int SomeInt = getArguments().getInt("someInt", 0);
        //String someTitle = getArguments().getString("someTitle", "");
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragement_home, container, false);



    }





    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        loading_spinner=(ProgressBar) view.findViewById(R.id.loading_spinner);
        pager_indicator = (LinearLayout) view.findViewById(R.id.viewPagerCountDots);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.ImageSlider);


        final MainActivity mainA=(MainActivity)this.getActivity();
        mainA.IsSearchButton=true;

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mainA.setSupportActionBar(toolbar);



        mainA.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainA.getSupportActionBar().setDisplayShowTitleEnabled(true);





        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);


        AppBarLayout appbar = (AppBarLayout)view.findViewById(R.id.app_bar_layout);
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isVisible = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mainA.getSupportActionBar().setTitle(getString(R.string.app_name));
                    isVisible = true;
                } else if(isVisible) {
                    mainA.getSupportActionBar().setTitle("");
                    isVisible = false;
                }
            }
        });



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this.getActivity(), mainA.drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mainA.drawer.addDrawerListener(toggle);
        toggle.syncState();


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {

            try {



                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                int height = metrics.heightPixels;
                int width = metrics.widthPixels;

                float heightDp = getResources().getDisplayMetrics().heightPixels ;// (float) StaticVar.densityPixel;
                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();


                lp.height = (int) heightDp;
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

        }



        RecyclerView catMoviesRecyclerView = (RecyclerView)view.findViewById(R.id.lstHomeMovies);

        LinearLayoutManager layoutManager =  new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false) ;


        catMoviesRecyclerView.setLayoutManager(layoutManager);
        viewPager.addOnPageChangeListener(this);







try {
    makeSliderMoviesHome(StaticVar.access_token, viewPager);
    makeGetCategoriesMoviesHome(StaticVar.access_token, catMoviesRecyclerView);
   // makeSliderMoviesHomeLocal(StaticVar.access_token);
    //makeGetCategoriesMoviesHomeLocal(StaticVar.access_token);
}catch (Exception ee)
{
    ee.printStackTrace();
}

    }



}
