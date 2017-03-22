package tv.afrostream.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 14/02/2017.
 */



public class ListDownloadMoviesCardView extends FrameLayout {

    Context context;
    public ListDownloadMoviesCardView(ViewGroup parent) {
        super(parent.getContext());
        initializeView(parent);
        context=parent.getContext();
    }




    private void initializeView(ViewGroup parent) {

   /* LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categorie_cell_card, this, false)
        .setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));*/

        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_download_cell_card, this)
                .setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));;
    }

    public void displayItem(final MovieItemModel movieItem) {
        ((TextView)findViewById(R.id.txtMovieTitle)).setText(movieItem.title);

        ((TextView)findViewById(R.id.txtMovieDescription)).setText(movieItem.label);


        final ProgressBar loading_spinner=((ProgressBar)findViewById(R.id.loading_spinner));

        final ImageView deletefav=((ImageView)findViewById(R.id.bntDeleteFav));
        ImageView mNetworkImageView = (ImageView) findViewById(R.id.NetworkImageView);

       if (movieItem.pathImage.equals("")) {


           Glide.with(context)
                   .load(movieItem.coverImageUrl)
                   .diskCacheStrategy(DiskCacheStrategy.ALL)
                   .crossFade()
                   .into(mNetworkImageView);
       }else
       {
           File imgFile=new File(movieItem.pathImage);
           Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
           mNetworkImageView.setImageBitmap(myBitmap);
       }


        ((ImageView)findViewById(R.id.bntDeleteFav)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                final MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title(R.string.mydownload)
                        .content(R.string.deletedownloadDialog)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .show();

                View positive = dialog.getActionButton(DialogAction.POSITIVE);


                positive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String imagePath="";
                        try {
                            imagePath = movieItem.pathImage;

                            try {

                                File fileImage = new File(imagePath);
                                boolean deleted = fileImage.delete();

                                File fileInfoJson = new File(movieItem.pathJsonFile);
                                boolean deleted2 = fileInfoJson.delete();

                                File fileVideo = new File(movieItem.pathvideoFile);
                                boolean deleted3 = fileVideo.delete();
                                if ( StaticVar.downloadAct!=null) StaticVar.downloadAct.makeMyDownloadList(StaticVar.access_token);
                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
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


    }



}
