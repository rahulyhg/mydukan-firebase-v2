package org.app.mydukan.fragments.myschemes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rojesharunkumar on 13/11/17.
 */

public class AddedModelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<String> modelsList;

    public AddedModelsAdapter() {
        modelsList = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.simple_item, parent, false);
        return new AddedModelsAdapter.ModelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ModelViewHolder viewHolder = (ModelViewHolder) holder;

        viewHolder.txtBrandTitle.setText(modelsList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelsList.size();
    }

    public void updateList(List<String> modelList) {
        this.modelsList = modelList;
    }


    public static class ModelViewHolder extends RecyclerView.ViewHolder {
        TextView txtBrandTitle;

        public ModelViewHolder(View itemView) {
            super(itemView);
            txtBrandTitle = (TextView) itemView.findViewById(R.id.item);
        }
    }
}
