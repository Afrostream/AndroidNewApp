package tv.afrostream.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import tv.afrostream.app.activitys.MovieDetailsActivity;
import tv.afrostream.app.activitys.PaymentActivity;

import tv.afrostream.app.models.SerieItemModel;
import tv.afrostream.app.services.DownloadFileService;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 18/01/2017.
 */


public class SerieEpisodeItemView extends FrameLayout {

    Context context;

    public SerieEpisodeItemView(Context contextS) {
        super(contextS);
        initializeView(contextS);
        context=contextS;
    }


    public static String SaveMovieInfoJson(JSONObject movie_Info,JSONObject episode_Info, String filename) throws IOException {
        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += filename+".afd";
        File data = new File(path);
        if (!data.createNewFile()) {
            data.delete();
            data.createNewFile();
        }



        BufferedWriter output = new BufferedWriter(new FileWriter(data));
        output.write(movie_Info.toString());
        output.close();


        String pathEpisodes = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator+filename+  File.separator;

        File dirEpisodes = new File(pathEpisodes);
        if (!dirEpisodes.exists()) {
            dirEpisodes.mkdirs();
        }

        String episodeNumber="";
            try {
                 episodeNumber = episode_Info.getString("episodeNumber");
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

        pathEpisodes += episodeNumber+".afd";


            File dataEpisode = new File(pathEpisodes);
            if (!dataEpisode.createNewFile()) {
                dataEpisode.delete();
                dataEpisode.createNewFile();
            }



            BufferedWriter outputEpisode = new BufferedWriter(new FileWriter(dataEpisode));
            outputEpisode.write(episode_Info.toString());
            outputEpisode.close();



        return pathEpisodes;
    }

    public void ImageToFile (ImageView imgMovie, String filename)
    {
        try {
            imgMovie.setDrawingCacheEnabled(true);
            String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += filename+".aim";
            File data = new File(path);
            FileOutputStream fos = new FileOutputStream(data);
            Bitmap bitmap = imgMovie.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ImageToFileEpisode (ImageView imgMovie,String MovieId, String filename)
    {
        try {
            imgMovie.setDrawingCacheEnabled(true);
            String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.DownloadFolderName + File.separator+MovieId+ File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += filename+".aim";
            File data = new File(path);
            FileOutputStream fos = new FileOutputStream(data);
            Bitmap bitmap = imgMovie.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.serie_episode_item, this);
    }

    public void showLoadingSpinner(){

        ((ProgressBar)findViewById(R.id.loading_spinner)).setVisibility(VISIBLE);

    }

    public void hideLoadingSpinner(){

        ((ProgressBar)findViewById(R.id.loading_spinner)).setVisibility(GONE);

    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE );
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(StaticVar.mainAct,new String[]{

                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE},1
        );
    }
    public  String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;


        if(size < sizeMo)
            return df.format(size / sizeKb)+ " Kb";
        else if(size < sizeGo)
            return df.format(size / sizeMo) + " Mo";
        else if(size < sizeTerra)
            return df.format(size / sizeGo) + " Go";

        return "";
    }
    public void displayItem(final SerieItemModel movieItem) {
        ((TextView)findViewById(R.id.txtMovieTitle)).setText(movieItem.title);
        ((TextView)findViewById(R.id.txtMovieLabel)).setText(movieItem.episodeNumber);
        final ImageView mNetworkImageView =(ImageView)findViewById(R.id.NetworkImageView);

        ImageView bntDownload=(ImageView)findViewById(R.id.bntDownload);


        String videoMp4DownloadUrl="";
        try {
            videoMp4DownloadUrl = movieItem.episode_all_Info.getJSONObject("video").getString("sourceMp4");
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        if (!videoMp4DownloadUrl.equals("") && !videoMp4DownloadUrl.equals("null"))
            bntDownload.setVisibility(VISIBLE);
        else
            bntDownload.setVisibility(GONE);


        bntDownload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (StaticVar.subscription==false){
                    if (StaticVar.MovieDetailsAct!=null) {
                        Intent nb = new Intent(StaticVar.MovieDetailsAct, PaymentActivity.class);
                        StaticVar.MovieDetailsAct.startActivityForResult(nb, 5);
                    }
                    return;
                }
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP){
                    // Do something for lollipop and above versions

                    if (!checkPermission()) {
                        requestPermission();
                        return;
                    }
                }
                String sourceMp4Size="";
                try {
                     sourceMp4Size = movieItem.episode_all_Info.getJSONObject("video").getString("sourceMp4Size");
                }catch (Exception ee)


                {
                    ee.printStackTrace();
                }
                String Taille="";
                if (!sourceMp4Size.equals("") && !sourceMp4Size.equals("null"))
                    try {
                        long t= Long.parseLong(sourceMp4Size);

                        Taille =context.getString(R.string.sizeoffile)+" "+ getStringSizeLengthFile(t);
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();

                    }

                String txt=context.getString(R.string.askdownload )+" "+Taille;

                final MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title(R.string.download)
                        .content(txt )

                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .show();

                View positive = dialog.getActionButton(DialogAction.POSITIVE);



                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String MovieID="";






                        try {





                            try {
                                MovieID=movieItem.movie_all_Info.getString("_id");
                               // String EpisodeVideo= movieItem.episode_all_Info.getJSONObject("video").getString("_id");
                                String EpisodeVideo= movieItem.episode_all_Info.getString("episodeNumber");


                                if (!MovieID.equals("")) {
                                   String Path= SaveMovieInfoJson(movieItem.movie_all_Info,movieItem.episode_all_Info, MovieID);
                                    ImageToFile(mNetworkImageView,MovieID);




                                    if (context!=null) {
                                        MovieDetailsActivity nb = (MovieDetailsActivity) context;

                                        ImageToFile(   nb.imgMovie,MovieID);

                                    }


                                    ImageToFileEpisode(mNetworkImageView,MovieID,EpisodeVideo);

                                
                                    String tmDevice = StaticVar.android_id;





                                    String url=movieItem.episode_all_Info.getJSONObject("video").getString("sourceMp4");




                                    String tile= movieItem.movie_all_Info.getString("title")+" - "+  movieItem.episode_all_Info.getString("title");

                                    String PathVideo=Path.substring(0,Path.lastIndexOf(".")+1 ) +"aiv";
                                    String PathImage=Path.substring(0,Path.lastIndexOf(".")+1 ) +"aim";


                                    Intent intent = new Intent(Intent.ACTION_SYNC, null,  StaticVar.mainAct, DownloadFileService.class);

                                    intent.putExtra("url",url);
                                    intent.putExtra("title",tile);
                                    intent.putExtra("tmDevice",tmDevice);
                                    intent.putExtra("PathVideo",PathVideo);
                                    intent.putExtra("PathImage",PathImage);
                                    intent.putExtra("tmDevice",StaticVar.android_id);





                                    mNetworkImageView.buildDrawingCache();
                                    Bitmap image= mNetworkImageView.getDrawingCache();

                                  //  Bundle extras = new Bundle();
                                    //extras.putParcelable("imagebitmap", image);
                                    //intent.putExtras(extras);
                                    if ( StaticVar.mainAct!=null) {
                                        StaticVar.mainAct.startService(intent);
                                        StaticVar.mainAct.showToast(StaticVar.mainAct.getString(R.string.downloadbegin));
                                    }
                                    
                                }

                            } catch (Exception e) {

                                if ( StaticVar.mainAct!=null)StaticVar.mainAct.showToast(e.getMessage());
                                e.printStackTrace();
                            }

                            dialog.dismiss();

                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                    }
                });
            }
        });


      /*  ImageLoader mImageLoader;

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


        mNetworkImageView.setImageUrl(movieItem.coverImageUrl, mImageLoader);*/



       /* Glide.with(context)
                .load(movieItem.coverImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(mNetworkImageView);*/

        Picasso.with(context)
                .load(movieItem.coverImageUrl)


                .into(mNetworkImageView);



    }
}
