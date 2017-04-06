package tv.afrostream.app.adapters;

/**
 * Created by bahri on 26/01/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tv.afrostream.app.CategorieMoviesCardView;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.activitys.MovieDetailsActivity;
import tv.afrostream.app.activitys.SearchActivity;
import tv.afrostream.app.utils.StaticVar;


public class SearchMoviesCardAdapter extends RecyclerView.Adapter {


    public ArrayList<MovieItemModel> moviesArray;





    public SearchMoviesCardAdapter( ArrayList<MovieItemModel>  chipsArray) {

        this.moviesArray = chipsArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {




        return new ChipViewHolder(new CategorieMoviesCardView(parent));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((CategorieMoviesCardView)holder.itemView).displayItem(moviesArray.get(position));

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
                            params.putString("from", "search");
                            params.putString("movie_title", titleMovie);
                            params.putString("movie_label", labelMovie);


                            StaticVar.mainAct.mFirebaseAnalytics.logEvent("open_movie", params);
                        }
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }
				try{

                Intent intent = new Intent(StaticVar.mainAct.getApplicationContext(), MovieDetailsActivity.class);

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(StaticVar.searchAct, view, "movieimage");

                intent.putExtra ("movieInfo",item.movie_all_Info.toString());
                intent.putExtra ("coverImageUrl",item.coverImageUrl.toString());
                intent.putExtra ("label",item.categorie.toString());

                ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
				}catch (Exception ee)
                {
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
