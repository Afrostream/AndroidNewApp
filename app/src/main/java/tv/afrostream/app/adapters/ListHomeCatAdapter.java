package tv.afrostream.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tv.afrostream.app.models.CatMoviesModel;
import tv.afrostream.app.HomeCatRowView;

/**
 * Created by bahri on 13/01/2017.
 */

public class ListHomeCatAdapter extends RecyclerView.Adapter {


    private ArrayList<CatMoviesModel> chipsArray;
    Boolean localMode=false;
    public ListHomeCatAdapter(ArrayList<CatMoviesModel> chipsArray,Boolean localMode) {

        this.chipsArray = chipsArray;
        this.localMode=localMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeCatViewHolder(new HomeCatRowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((HomeCatRowView)holder.itemView).setAdapter(chipsArray.get(position).categorie,new HomeCatMoviesItemAdapter(chipsArray.get(position).Movies,localMode));


    }

    @Override
    public int getItemCount() {
        return chipsArray.size();
    }

    private class HomeCatViewHolder extends RecyclerView.ViewHolder {

        public HomeCatViewHolder(View itemView) {

            super(itemView);


        }
    }
}
