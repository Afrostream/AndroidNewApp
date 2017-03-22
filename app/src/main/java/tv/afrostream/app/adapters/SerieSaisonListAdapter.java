package tv.afrostream.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import tv.afrostream.app.models.SerieSaisonModel;
import tv.afrostream.app.SerieSaisonRowView;

/**
 * Created by bahri on 18/01/2017.
 */


public class SerieSaisonListAdapter extends RecyclerView.Adapter {


    private ArrayList<SerieSaisonModel> chipsArray;

    public SerieSaisonListAdapter(ArrayList<SerieSaisonModel> chipsArray) {

        this.chipsArray = chipsArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HomeCatViewHolder(new SerieSaisonRowView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SerieSaisonRowView)holder.itemView).setAdapter(chipsArray.get(position).categorie,new SerieEpisodeAdapter(chipsArray.get(position).Movies));


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
