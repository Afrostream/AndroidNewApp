package tv.afrostream.app.fragments;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;
import tv.afrostream.app.adapters.ListDownloadMoviesCardAdapter;
import tv.afrostream.app.adapters.ListFavorisMoviesCardAdapter;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 13/02/2017.
 */




public class MyDownloadFragment extends Fragment {


    private Toast toast;
    String idcat="";
    String catName="";
    ProgressBar loading_spinner=null;
    public String  TAG=MyDownloadFragment.class.getSimpleName();
    RecyclerView rc;



    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }





    public void DoResponseGetListFav(JSONArray response){




        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();

            ArrayList<MovieItemModel> MoviesList = new ArrayList<MovieItemModel>();

            for (int i = 0; i < response.length(); i++) {

                JSONObject movie = (JSONObject) response.get(i);



                Log.d(TAG,"film "+movie.getString("title"));


                String titleMovie=movie.getString("title");
                String labelMovie=movie.getString("genre");
                JSONObject posterMovie  =movie.getJSONObject("poster");
                String urlImageMovie= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=130&h=120&q=100&fm=jpg&facepad=1&crop=entropy&auto=format&dpr="+StaticVar.densityPixel;
                MoviesList.add(new MovieItemModel(titleMovie,labelMovie,urlImageMovie,movie,labelMovie));



            }

            ListFavorisMoviesCardAdapter adapter = new ListFavorisMoviesCardAdapter(MoviesList);

            rc.setAdapter(adapter);

            loading_spinner.setVisibility(View.GONE);



        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error: " + e.getMessage());
            loading_spinner.setVisibility(View.GONE);
        }



    }

    public JSONObject FileToJson(File fl)
    {
        BufferedReader input = null;
        JSONObject jsonObject=null;
        try {
            input = new BufferedReader(new FileReader(fl));
            String line;
            StringBuffer content = new StringBuffer();
            char[] buffer = new char[1024];
            int num;
            while ((num = input.read(buffer)) > 0) {
                content.append(buffer, 0, num);
            }
             jsonObject = new JSONObject(content.toString());
            return jsonObject;

        }catch (Exception e) {


            return jsonObject;

        }
    }

    public static String getFileExt(String fileName) {
        String st="";
        st=fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
        return st;
    }
    public void makeMyDownloadList(final String access_token) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);

        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator;

        ArrayList<MovieItemModel> MoviesList = new ArrayList<MovieItemModel>();

        File dir = new File(path);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; ++i) {
                File file = files[i];
                if (!file.isDirectory()) {

                    Log.d(TAG,file.getPath());

                    String Path=file.getPath();
                    String PathImage=Path.substring(0,Path.lastIndexOf(".")+1 ) +"aim";
                    String PathVideo=Path.substring(0,Path.lastIndexOf(".")+1 ) +"aiv";
                    File imageFile=null;
                    try{
                         imageFile=new File(PathImage);
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                    if (getFileExt(file.getPath()).equals("afd") && imageFile!=null && imageFile.exists()) {


                        JSONObject movie = FileToJson(file);

                        if (movie != null) {
                            try {

                                Log.d(TAG, "film " + movie.getString("title"));


                                String titleMovie = movie.getString("title");
                                String labelMovie="";
                                try {
                                     labelMovie = movie.getString("genre");
                                }catch (Exception ee)
                                {
                                    ee.printStackTrace();
                                }
                                try{
                                JSONObject posterMovie = movie.getJSONObject("poster");
                                String urlImageMovie = posterMovie.getString("imgix") + "?&crop=entropy&fit=min&w=350&h=300&q=75&fm=jpg&facepad=1&crop=faces&auto=format&dpr="+StaticVar.densityPixel;

                                MoviesList.add(new MovieItemModel(titleMovie, labelMovie, urlImageMovie, movie,PathImage,Path,PathVideo,labelMovie));
                                }catch (Exception ee)
                                {
                                    ee.printStackTrace();
                                }
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                    }else
                    {
                        try
                        {

                            Boolean img=imageFile.exists();
                            Boolean fl=file.exists();

                            if (img==false || fl==false) {
                                if (imageFile.exists()) imageFile.delete();
                                if (file.exists()) file.delete();
                            }
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }
                    }


                }
            }
        }

        ListDownloadMoviesCardAdapter adapter = new ListDownloadMoviesCardAdapter(MoviesList);

        rc.setAdapter(adapter);

        loading_spinner.setVisibility(View.GONE);










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

        //StaticVar.downloadFragment=this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_download, container, false);






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

        mainA.getSupportActionBar().setTitle(getString(R.string.mydownload));


        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open navigation drawer when click navigation back button

                mainA.drawer.openDrawer(GravityCompat.START);
            }
        });



        // CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);

        rc = (RecyclerView) view.findViewById(R.id.lstCatMovies);
        rc.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        rc.setHasFixedSize(true);

        // AppBarLayout appbar = (AppBarLayout)view.findViewById(R.id.app_bar_layout);


        makeMyDownloadList(StaticVar.access_token);

    }



}