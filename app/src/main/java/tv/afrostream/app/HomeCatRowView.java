package tv.afrostream.app;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import tv.afrostream.app.adapters.HomeCatMoviesItemAdapter;

/**
 * Created by bahri on 13/01/2017.
 */

public class HomeCatRowView extends FrameLayout {

    public HomeCatRowView(Context context) {
        super(context);
        initializeView(context);
    }

    private void initializeView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.home_cat_single_row, this);

        LinearLayoutManager layoutManager =  new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) ;

        ((RecyclerView)findViewById(R.id.recyclerViewHorizontal)).setLayoutManager(layoutManager);
    }

    public void setAdapter(String Categorie, HomeCatMoviesItemAdapter adapter) {
        ((RecyclerView)findViewById(R.id.recyclerViewHorizontal)).setAdapter(adapter);

        ((TextView)findViewById(R.id.txtTitreCat)).setText(Categorie);


    }


}
