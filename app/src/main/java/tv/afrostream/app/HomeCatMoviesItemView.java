package tv.afrostream.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.io.File;

import tv.afrostream.app.models.MovieItemModel;

/**
 * Created by bahri on 13/01/2017.
 */

public class HomeCatMoviesItemView extends FrameLayout {

Context context;
    public HomeCatMoviesItemView(Context contextS) {
        super(contextS);
        initializeView(contextS);
        context=contextS;
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.home_cat_movie_item, this);
    }

    public void displayItem(  MovieItemModel movieItem,Boolean localMode) {
        ((TextView)findViewById(R.id.txtMovieTitle)).setText(movieItem.title);


        ((TextView)findViewById(R.id.txtMovieLabel)).setText(movieItem.label);


        ((TextView)findViewById(R.id.txtMovieLabel)).setVisibility(View.GONE);
       // if (movieItem.label.equals("null"))((TextView)findViewById(R.id.txtMovieLabel)).setVisibility(View.GONE);

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

        ImageView mNetworkImageView =(ImageView)findViewById(R.id.NetworkImageView);
        if (localMode==false) {
           /* Glide.with(context)
                    .load(movieItem.coverImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading_spinner)
                    .crossFade()
                    .into(mNetworkImageView);*/

            Picasso.with(context)
                    .load(movieItem.coverImageUrl)


                    .into(mNetworkImageView);

        }else
        {
            File imgFile=new File(movieItem.pathImage);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            mNetworkImageView.setImageBitmap(myBitmap);
        }






    }
}
