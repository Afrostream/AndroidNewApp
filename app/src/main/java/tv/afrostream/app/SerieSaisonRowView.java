package tv.afrostream.app;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import tv.afrostream.app.adapters.SerieEpisodeAdapter;

/**
 * Created by bahri on 18/01/2017.
 */


public class SerieSaisonRowView extends FrameLayout {



    public SerieSaisonRowView(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.serie_single_row, this);
        ((RecyclerView)findViewById(R.id.recyclerViewHorizontal)).setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));


    }

    public void setAdapter(String Categorie, SerieEpisodeAdapter adapter) {

        RecyclerView rc=((RecyclerView)findViewById(R.id.recyclerViewHorizontal));
        rc.setAdapter(adapter);




        ((TextView)findViewById(R.id.txtTitreCat)).setText(Categorie);


    }


}
