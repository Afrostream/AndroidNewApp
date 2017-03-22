package tv.afrostream.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MovieDetailsActivity;
import tv.afrostream.app.activitys.PaymentActivity;

import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.SerieEpisodeItemView;
import tv.afrostream.app.models.SerieItemModel;
import tv.afrostream.app.activitys.PlayerActivity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by bahri on 18/01/2017.
 */





public class SerieEpisodeAdapter extends RecyclerView.Adapter {


    public ArrayList<SerieItemModel> moviesArray;


    String videoDashUrl="";
    String videoHlsUrl="";
    String videoSmoothUrl="";
    String movietype="";


    Boolean videoDRM=false;
   Context context=null;

    private UUID getDrmUuid(String typeString) throws ParserException {
        switch (typeString.toLowerCase()) {
            case "widevine":
                return C.WIDEVINE_UUID;
            case "playready":
                return C.PLAYREADY_UUID;
            default:
                try {
                    return UUID.fromString(typeString);
                } catch (RuntimeException e) {
                    throw new ParserException("Unsupported drm type: " + typeString);
                }
        }
    }
    private abstract static class Sample {

        public final String name;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;

        public Sample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
            this.name = name;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;
        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
            if (drmSchemeUuid != null) {
                intent.putExtra(PlayerActivity.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
                intent.putExtra(PlayerActivity.DRM_LICENSE_URL, drmLicenseUrl);
                intent.putExtra(PlayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
                intent.putExtra(PlayerActivity.VIDEO_NAME, name);
            }
            return intent;
        }

    }

    private static final class UriSample extends Sample {

        public final String uri;
        public final String uriSmooth;
        public final String imageurl;
        public final String extension;
        public final String Video_ID;
        public final int playerPosition;
        public final String video_type;

        public UriSample(String name,String Video_ID,UUID drmSchemeUuid, String drmLicenseUrl,
                         String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,String uriSmooth,final String imageurl,
                         String extension,int playerPosition,String video_type) {
            super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
            this.uri = uri;
            this.extension = extension;
            this.uriSmooth=uriSmooth;
            this.imageurl=imageurl;
            this.Video_ID=Video_ID;
            this.playerPosition=playerPosition;
            this.video_type=video_type;
        }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra("uriSmooth",uriSmooth)
                    .putExtra("imageUrl",imageurl)
                    .putExtra("Video_ID",Video_ID)
                    .putExtra("playerPosition",playerPosition)
                    .putExtra("video_type",video_type)
                    .putExtra(PlayerActivity.EXTENSION_EXTRA, extension)
                    .setAction(PlayerActivity.ACTION_VIEW);
        }

    }


    private void makeGetVideoInformationPosition(final String access_token, final String videoID, final String name, final UUID drmSchemeUuid, final String drmLicenseUrl,
                                                 final String[] drmKeyRequestProperties, final boolean preferExtensionDecoders, final String uri, final String uriSmooth, final String ImageUrl,
                                                 final String extension) {


        if (access_token.equals("") )
        {


            return;
        }



        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/videos/"+videoID;




        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,urlJsonObj,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {



                        try {

                            try{
                                int  playerPositionJ = response.getInt("playerPosition");
                                int playerPosition=playerPositionJ*1000;

                                Sample smp=new UriSample( name,videoID,  drmSchemeUuid,  drmLicenseUrl,   drmKeyRequestProperties,  false,
                                        videoDashUrl,videoSmoothUrl,ImageUrl,extension,playerPosition,movietype) ;




                                Intent nb=smp.buildIntent(context);
                                context.startActivity(nb);


                            }catch (Exception ee)
                            {

                            }




                        }catch (Exception ee)
                        {

                            ee.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



                try {


                    Sample smp=new UriSample( name,videoID,  drmSchemeUuid,  drmLicenseUrl,   drmKeyRequestProperties,  false,
                            videoDashUrl,videoSmoothUrl,ImageUrl,extension,0,movietype) ;
                    Intent nb=smp.buildIntent(context);
                    context.startActivity(nb);
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson = error2.getMessage();
                        JSONObject errorJ = new JSONObject(errorJson);
                        String MessageError = errorJ.getString("error");






                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }
                error.printStackTrace();
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());



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

    public void  DoResponseVideoInfo (String idVideo,JSONObject response,String titleMovie,final RecyclerView.ViewHolder holder) {




        ((SerieEpisodeItemView)holder.itemView).hideLoadingSpinner();

        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();


            videoDRM=response.getBoolean("drm");



            JSONArray js= response.getJSONArray("sources");

            for (int i=0;i<js.length();i++)
            {
                JSONObject sr=js.getJSONObject(i);

                String type=sr.getString("type");
                String src=sr.getString("src");

                if (type.equals("application/dash+xml"))
                    videoDashUrl=src;
                else    if (type.equals("application/vnd.apple.mpegurl"))
                    videoHlsUrl=src;
                else  if (type.equals("application/vnd.ms-sstr+xml"))
                    videoSmoothUrl=src;
            }



            String extension = "mpd";
            String name=titleMovie;
            UUID drmSchemeUuid= null;
            try {
                if (videoDRM) drmSchemeUuid = getDrmUuid("widevine");
                else
                    drmSchemeUuid = getDrmUuid("");
            } catch (ParserException e) {
                e.printStackTrace();
            }


            String drmLicenseUrl="";
            if (videoDRM)drmLicenseUrl= StaticVar.drmLicenseUrl;


            String[] drmKeyRequestProperties=null;

            ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();

            drmKeyRequestProperties = drmKeyRequestPropertiesList.toArray(new String[0]);

            String ImageUrl="";




            makeGetVideoInformationPosition(StaticVar.access_token,idVideo,name,  drmSchemeUuid,  drmLicenseUrl,
                    drmKeyRequestProperties,  false,videoDashUrl,videoSmoothUrl,ImageUrl,extension);


            try {

                if (StaticVar.mainAct!=null) {


                    if (StaticVar.mainAct.mFirebaseAnalytics != null) {



                        try {

                        }catch (Exception ee){
                            ee.printStackTrace();
                        }


                        Bundle params = new Bundle();
                        params.putString("type", "serie");
                        params.putString("movie_title", titleMovie);



                        StaticVar.mainAct.mFirebaseAnalytics.logEvent("open_video", params);
                    }
                }
            }catch (Exception ee)
            {
                ee.printStackTrace();
            }











        } catch (Exception e) {
            e.printStackTrace();

        }



    }

    private void makeGetVideoInfo(final String access_token, final String idVideo, final String titleMovie, final RecyclerView.ViewHolder holder) {



        if (access_token.equals("") )
        {


            return;
        }

        ((SerieEpisodeItemView)holder.itemView).showLoadingSpinner();
        // loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/videos/"+idVideo;



        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                // handle data, like converting it to xml, json, bitmap etc.,

                JSONObject response=new JSONObject(data) ;

                DoResponseVideoInfo(idVideo,response,titleMovie,holder);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    DoResponseVideoInfo(idVideo,response,titleMovie,holder);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    ((SerieEpisodeItemView)holder.itemView).hideLoadingSpinner();
                    try {
                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson = error2.getMessage();
                            JSONObject errorJ = new JSONObject(errorJson);
                            String MessageError = errorJ.getString("error");


                        }
                    }catch (Exception e)
                    {
                        e.printStackTrace();
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


            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }


    }





    public SerieEpisodeAdapter( ArrayList<SerieItemModel>  chipsArray) {

        this.moviesArray = chipsArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        context = parent.getContext();
        return new ChipViewHolder(new SerieEpisodeItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ((SerieEpisodeItemView)holder.itemView).displayItem(moviesArray.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                SerieItemModel item=moviesArray.get(position);
                movietype= "serie";
                if (StaticVar.subscription) {
                    String videoID = "";
                    try {

                        videoID = item.episode_all_Info.getJSONObject("video").getString("_id");

                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }

                    makeGetVideoInfo(StaticVar.access_token, videoID, item.title, holder);
                }else
                {
                    if ( StaticVar.MovieDetailsAct!=null) {
                        Intent nb = new Intent(StaticVar.MovieDetailsAct, PaymentActivity.class);
                        StaticVar.MovieDetailsAct.startActivityForResult(nb,5);
                    }
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
