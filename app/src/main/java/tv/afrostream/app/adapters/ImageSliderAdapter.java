package tv.afrostream.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;
import tv.afrostream.app.activitys.MovieDetailsActivity;
import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 16/01/2017.
 */

public class ImageSliderAdapter extends PagerAdapter {
    Context context;


    public ArrayList<MovieItemModel> GalImages;

    LayoutInflater mLayoutInflater;
    Boolean localMode=false;

    public ImageSliderAdapter(Context context, ArrayList<MovieItemModel> GalImagesS,Boolean localMode){
        this.context=context;
        GalImages=GalImagesS;
        this.localMode=localMode;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return GalImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.slider_image_item, container, false);

        ImageView imgdown = (ImageView) itemView.findViewById(R.id.imgdown);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {


                    if (imgdown.getVisibility()==View.GONE) {
                        AnimationUtils.enterBottom(imgdown, 1000);

                        AnimationUtils.rotateX(imgdown, 1300);
                    }
        }else
        {
            imgdown.setVisibility(View.GONE);
        }



        ImageView imageView = (ImageView) itemView.findViewById(R.id.NetworkImageView);






              /*  Glide.with(context)
                .load(GalImages.get(position).coverImageUrl)
                .centerCrop()
                .placeholder(R.drawable.cast_mini_controller_progress_drawable)
                .crossFade();*/

            if (localMode==false) {
               /* Glide.with(context)
                        .load(GalImages.get(position).coverImageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .crossFade()
                        .into(imageView);*/

                Picasso.with(context)
                        .load(GalImages.get(position).coverImageUrl)


                        .into(imageView);
            }else
            {
                File imgFile=new File(GalImages.get(position).pathImage);
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }


        if (GalImages.get(position).title.equals("null"))
            ((TextView)itemView.findViewById(R.id.txtMovieTitle)).setVisibility(View.GONE);
        else {
            ((TextView) itemView.findViewById(R.id.txtMovieTitle)).setText(GalImages.get(position).title);
            ((TextView)itemView.findViewById(R.id.txtMovieTitle)).setVisibility(View.VISIBLE);
        }

        if (GalImages.get(position).label.equals("null"))
            ((TextView)itemView.findViewById(R.id.txtMovieLabel)).setVisibility(View.GONE);
        else {
            ((TextView) itemView.findViewById(R.id.txtMovieLabel)).setText(GalImages.get(position).label);
            ((TextView)itemView.findViewById(R.id.txtMovieLabel)).setVisibility(View.VISIBLE);
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieItemModel item=GalImages.get(position);



                try {

                    if (StaticVar.mainAct!=null) {


                        if (StaticVar.mainAct.mFirebaseAnalytics != null) {


                            String titleMovie="";
                            String labelMovie="";
                            try {
                                titleMovie=item.movie_all_Info.getString("title");
                                labelMovie = item.movie_all_Info.getString("genre");
                            }catch (Exception ee){
                                ee.printStackTrace();
                            }


                            Bundle params = new Bundle();
                            params.putString("from", "slide");
                            params.putString("movie_title", titleMovie);
                            params.putString("movie_label", labelMovie);


                            StaticVar.mainAct.mFirebaseAnalytics.logEvent("open_movie", params);
                        }
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

                Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((MainActivity)view.getContext(), view, "movieimage");

                intent.putExtra ("movieInfo",item.movie_all_Info.toString());
                intent.putExtra ("coverImageUrl",item.coverImageUrl.toString());

                ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());



            }
        });


       // imageView.setImageResource(GalImages.get(position) );

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
