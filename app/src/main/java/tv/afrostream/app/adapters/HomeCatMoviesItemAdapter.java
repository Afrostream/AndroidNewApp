package tv.afrostream.app.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tv.afrostream.app.HomeCatMoviesItemView;
import tv.afrostream.app.R;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.activitys.MainActivity;
import tv.afrostream.app.activitys.MovieDetailsActivity;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 13/01/2017.
 */

public class HomeCatMoviesItemAdapter extends RecyclerView.Adapter {




    public   ArrayList<MovieItemModel>   moviesArray;

    Boolean localMode=false;



    public HomeCatMoviesItemAdapter( ArrayList<MovieItemModel>  chipsArray,Boolean localMode) {

        this.moviesArray = chipsArray;
        this.localMode=localMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChipViewHolder(new HomeCatMoviesItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((HomeCatMoviesItemView)holder.itemView).displayItem(moviesArray.get(position),localMode);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieItemModel item=moviesArray.get(position);


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
                            params.putString("from", "home");
                            params.putString("movie_title", titleMovie);
                            params.putString("movie_label", labelMovie);


                            StaticVar.mainAct.mFirebaseAnalytics.logEvent("open_movie", params);
                        }
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }


                try {
                    Intent intent = new Intent(StaticVar.mainAct.getApplicationContext(), MovieDetailsActivity.class);

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(StaticVar.mainAct, view, "movieimage");

                    intent.putExtra("movieInfo", item.movie_all_Info.toString());
                    intent.putExtra("coverImageUrl", item.coverImageUrl.toString());

                    intent.putExtra("label", item.categorie);

                    ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
                }catch (Exception ee)
                {
                    ee.getStackTrace();
                }



            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesArray.size();
    }

    private class ChipViewHolder extends RecyclerView.ViewHolder {

        public ChipViewHolder(View itemView) {
            super(itemView);
        }
    }
}
