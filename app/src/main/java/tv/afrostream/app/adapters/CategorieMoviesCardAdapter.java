package tv.afrostream.app.adapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.afrostream.app.AppController;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.CategorieMoviesCardView;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;
import tv.afrostream.app.activitys.MovieDetailsActivity;

/**
 * Created by bahri on 23/01/2017.
 */


public class CategorieMoviesCardAdapter extends RecyclerView.Adapter {


    public ArrayList<MovieItemModel> moviesArray;






public Boolean ifExistFav(ArrayList<MovieItemModel> favlist,MovieItemModel movie)
{
    Boolean result=false;
    for(int i=0;i<favlist.size();i++)
    {
        MovieItemModel favMovie=favlist.get(i);
        if (favMovie.label.equals(movie.label) && favMovie.title.equals(movie.title))
            result=  true;


    }
    return result;

}

    public CategorieMoviesCardAdapter( ArrayList<MovieItemModel>  chipsArray) {

        this.moviesArray = chipsArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {




        return new ChipViewHolder(new CategorieMoviesCardView(parent));
    }

        @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((CategorieMoviesCardView)holder.itemView).displayItem(moviesArray.get(position));

         final ImageView fav =(ImageView) holder.itemView.findViewById(R.id.bntAddFav);
            final ProgressBar loading_spinner =(ProgressBar) holder.itemView.findViewById(R.id.loading_spinner);




            if (ifExistFav(StaticVar.catFragment.MoviesListFavoris,moviesArray.get(position))) {
                fav.setImageResource(R.drawable.deletefav);

            }else
            {
                fav.setImageResource(R.drawable.favbutton);

            }


            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String movieId="";
                    try {
                        movieId = moviesArray.get(position).movie_all_Info.getString("_id");

                        if (StaticVar.catFragment.MoviesListFavoris.contains(moviesArray.get(position))) {

                        }
                        if(StaticVar.catFragment!=null) {



                            if (ifExistFav(StaticVar.catFragment.MoviesListFavoris,moviesArray.get(position)))  {
                                StaticVar.catFragment.makeDeleteMovieToFavoris(StaticVar.access_token, movieId, fav, loading_spinner);
                            }else
                            {
                                StaticVar.catFragment.makeAddMovieToFavoris(StaticVar.access_token, movieId, fav, loading_spinner);
                            }
                        }




                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }


                }
            });

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
                            params.putString("from", "categorie");
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
