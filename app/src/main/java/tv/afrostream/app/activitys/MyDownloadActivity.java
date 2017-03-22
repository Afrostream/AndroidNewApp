package tv.afrostream.app.activitys;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import tv.afrostream.app.R;
import tv.afrostream.app.adapters.ListDownloadMoviesCardAdapter;
import tv.afrostream.app.adapters.ListFavorisMoviesCardAdapter;
import tv.afrostream.app.fragments.MyDownloadFragment;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 15/02/2017.
 */

public class MyDownloadActivity  extends AppCompatActivity {



    private Toast toast;
    String idcat="";
    String catName="";
    ProgressBar loading_spinner=null;
    public String  TAG=MyDownloadFragment.class.getSimpleName();
    RecyclerView rc;

    public MaterialDialog dialogLoading;


    public void ShowLoadingDialog()
    {
        dialogLoading= new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog_decrypt)
                .content(R.string.please_wait)
                .progress(true, 0)
                .show();
    }

    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
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

        ArrayList<MovieItemModel> MoviesList = new ArrayList<MovieItemModel>();
        ListDownloadMoviesCardAdapter adapteEmpty = new ListDownloadMoviesCardAdapter(MoviesList);

        rc.setAdapter(adapteEmpty);



        loading_spinner.setVisibility(View.VISIBLE);

        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator;



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
                                    String urlImageMovie = posterMovie.getString("imgix") + "?&crop=entropy&fit=min&w=130&h=120&q=100&fm=jpg&facepad=1&crop=faces&auto=format";

                                    MoviesList.add(new MovieItemModel(titleMovie, labelMovie, urlImageMovie, movie,PathImage,Path,PathVideo));
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
    protected void onCreate(Bundle savedInstanceState)  {


        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }

        setContentView(R.layout.activity_download);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        loading_spinner=(ProgressBar) this.findViewById(R.id.loading_spinner);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(true);

        this.getSupportActionBar().setTitle(getString(R.string.mydownload));


        StaticVar.downloadAct=this;


        // CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);

        rc = (RecyclerView) this.findViewById(R.id.lstCatMovies);
        rc.setLayoutManager(new LinearLayoutManager(this));

        rc.setHasFixedSize(true);

        // AppBarLayout appbar = (AppBarLayout)view.findViewById(R.id.app_bar_layout);


        makeMyDownloadList(StaticVar.access_token);

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
