package tv.afrostream.app;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.models.MovieItemModel;

/**
 * Created by bahri on 27/01/2017.
 */


public class ListFavorisMoviesCardView extends FrameLayout {

    Context context;
    public ListFavorisMoviesCardView(ViewGroup parent) {
        super(parent.getContext());
        initializeView(parent);
        context=parent.getContext();
    }


    private void makeDeleteMovieToFavoris(final String access_token, String MovieId, final ProgressBar loading_spinner, final ImageView deletefav) {


        if (access_token.equals("") )
        {


            return;
        }



        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/favoritesMovies/"+MovieId;


        loading_spinner.setVisibility(VISIBLE);
        deletefav.setVisibility(GONE);


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE,urlJsonObj,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading_spinner.setVisibility(GONE);


                if (StaticVar.favFragment!=null)
                    StaticVar.favFragment.makeGetListFav(StaticVar.access_token);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
                loading_spinner.setVisibility(GONE);
                deletefav.setVisibility(VISIBLE);
                error.printStackTrace();
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());
                try {
                    String errorStr=error.getMessage();
                    // if (errorStr.length()>300)errorStr=errorStr.substring(0,300);
                    //showToast("Error " + errorStr );
                }catch (Exception ee) {
                    ee.printStackTrace();
                }


            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer "+access_token);
                // headers.put("key", "Value");
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(req);






    }

    private void initializeView(ViewGroup parent) {

   /* LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categorie_cell_card, this, false)
        .setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));*/

        LayoutInflater.from(parent.getContext()).inflate(R.layout.list_favoris_cell_card, this)
                .setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));;
    }

    public void displayItem(final MovieItemModel movieItem) {
        ((TextView)findViewById(R.id.txtMovieTitle)).setText(movieItem.title);

        ((TextView)findViewById(R.id.txtMovieDescription)).setText(movieItem.label);


        final ProgressBar loading_spinner=((ProgressBar)findViewById(R.id.loading_spinner));

        final ImageView deletefav=((ImageView)findViewById(R.id.bntDeleteFav));


        ImageView mNetworkImageView =(ImageView)findViewById(R.id.NetworkImageView);
        /*Glide.with(context)
                .load(movieItem.coverImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(mNetworkImageView);*/

        Picasso.with(context)
                .load(movieItem.coverImageUrl)


                .into(mNetworkImageView);



        ((ImageView)findViewById(R.id.bntDeleteFav)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                final MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title(R.string.deletefavTitleDialog)
                        .content(R.string.deletefavTextDialog)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .show();

                View positive = dialog.getActionButton(DialogAction.POSITIVE);


                positive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String videoId="";
                        try {
                            videoId = movieItem.movie_all_Info.getString("_id");
                            makeDeleteMovieToFavoris(StaticVar.access_token,videoId,loading_spinner,deletefav);
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
