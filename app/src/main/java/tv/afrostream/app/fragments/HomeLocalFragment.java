package tv.afrostream.app.fragments;

import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.afrostream.app.activitys.MainActivityLocal;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.models.CatMoviesModel;
import tv.afrostream.app.adapters.ImageSliderAdapter;
import tv.afrostream.app.adapters.ListHomeCatAdapter;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.R;


/**
 * Created by bahri on 20/02/2017.
 */

public class HomeLocalFragment extends Fragment  implements  ViewPager.OnPageChangeListener {


    private Toast toast;

    ProgressBar loading_spinner=null;
    public String  TAG=HomeLocalFragment.class.getSimpleName();

    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private Handler slide_handler;
    private int slide_delay = 4000; //milliseconds
    private int slide_page = 0;
    private Runnable slide_runnable;

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public JSONArray FileToJsonArray(File fl)
    {
        BufferedReader input = null;
        JSONArray jsonObject=null;
        try {
            input = new BufferedReader(new FileReader(fl));
            String line;
            StringBuffer content = new StringBuffer();
            char[] buffer = new char[1024];
            int num;
            while ((num = input.read(buffer)) > 0) {
                content.append(buffer, 0, num);
            }
            jsonObject = new JSONArray(content.toString());
            return jsonObject;

        }catch (Exception e) {


            return jsonObject;

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
                        String idMovie=movie.getString("_id");
                        String labelMovie="";
                        try {
                            labelMovie = movie.getString("genre");
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                        final String filename="afro_local_"+idMovie;
                        try {
                            String pathImage = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;

                            File dir = new File(pathImage);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            pathImage +=filename+ ".aim";


                            JSONObject posterMovie = movie.getJSONObject("poster");
                            String urlImageMovie = posterMovie.getString("imgix") + "?&crop=entropy&fit=min&w=250&h=200&q=100&fm=jpg&facepad=1&crop=entropy&dpr="+StaticVar.densityPixel;
                            MoviesList.add(new MovieItemModel(titleMovie, labelMovie, urlImageMovie, movie,pathImage,"",""));
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

            ListHomeCatAdapter lstHomeCatAdapter = new ListHomeCatAdapter(CatMoviesList,true);

            catMoviesRecyclerView.setAdapter(lstHomeCatAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
        }



    }


    private void makeGetCategoriesMoviesHome(final String access_token, final RecyclerView catMoviesRecyclerView) {




        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += "catmovies.afj";

        JSONArray response= FileToJsonArray(new File(path));

        if (response!=null)
            OnReponseGetCategorieMovieHome(response,catMoviesRecyclerView);


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

                    String idMovie=movie.getString("_id");
                    String titleMovie=movie.getString("title");
                    String labelMovie="";
                    try{
                        labelMovie=movie.getString("genre");
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }


                    final String filename="afro_local_slides_"+idMovie;

                    String pathImage = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;

                    pathImage +=filename+ ".aim";

                    try{
                        JSONObject posterMovie  =movie.getJSONObject("poster");
                        String urlImageMovie= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=550&h=350&q=100&fm=jpg&facepad=1&crop=entropy&auto=format&dpr="+StaticVar.densityPixel;
                        MoviesList.add(new MovieItemModel(titleMovie,labelMovie,urlImageMovie,movie,pathImage,"",""));
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

            final ImageSliderAdapter adapter = new ImageSliderAdapter(this.getActivity(),MoviesList,true);

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


        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
         path += "slides.afj";

         JSONArray response= FileToJsonArray(new File(path));

        if (response!=null)
            DoResponseSliderMoviesHome(response,viewPager);




    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        slide_page = position;
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(ContextCompat.getDrawable(this.getActivity(),R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(ContextCompat.getDrawable(this.getActivity(),R.drawable.selecteditem_dot));

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
    }

    @Override
    public void onPause() {
        super.onPause();
        if (slide_handler!=null) slide_handler.removeCallbacks(slide_runnable);
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
        return inflater.inflate(R.layout.fragement_home_local, container, false);



    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        loading_spinner=(ProgressBar) view.findViewById(R.id.loading_spinner);
        pager_indicator = (LinearLayout) view.findViewById(R.id.viewPagerCountDots);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.ImageSlider);


        final MainActivityLocal mainA=(MainActivityLocal)this.getActivity();
        mainA.IsSearchButton=false;

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






        RecyclerView catMoviesRecyclerView = (RecyclerView)view.findViewById(R.id.lstHomeMovies);
        catMoviesRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        viewPager.addOnPageChangeListener(this);


        try {
            makeSliderMoviesHome(StaticVar.access_token, viewPager);
            makeGetCategoriesMoviesHome(StaticVar.access_token, catMoviesRecyclerView);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

    }

}
