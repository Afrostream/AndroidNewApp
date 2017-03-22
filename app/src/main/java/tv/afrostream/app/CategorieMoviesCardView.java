package tv.afrostream.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import tv.afrostream.app.models.MovieItemModel;

/**
 * Created by bahri on 23/01/2017.
 */


public class CategorieMoviesCardView extends FrameLayout {

    Context context;
    public CategorieMoviesCardView(ViewGroup parent) {
        super(parent.getContext());
        initializeView(parent);
        context=parent.getContext();
    }

    private void initializeView(ViewGroup parent) {

   /* LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categorie_cell_card, this, false)
        .setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));*/

        LayoutInflater.from(parent.getContext()).inflate(R.layout.categorie_cell_card, this)
                .setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));;
    }

    public void displayItem(  MovieItemModel movieItem) {
        ((TextView)findViewById(R.id.txtMovieTitle)).setText(movieItem.title);

        ((TextView)findViewById(R.id.txtMovieDescription)).setText(movieItem.label);



        ImageView mNetworkImageView =(ImageView)findViewById(R.id.NetworkImageView);
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
