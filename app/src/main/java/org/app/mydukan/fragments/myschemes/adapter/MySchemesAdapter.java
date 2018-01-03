package org.app.mydukan.fragments.myschemes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.SchemesAdapter;
import org.app.mydukan.data.Scheme;

import java.util.List;


/**
 * Created by rojesharunkumar on 20/10/17.
 */

public class MySchemesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<Scheme> schemeList;
    public SchemesAdapter.SchemesAdapterListener mListener;


    public MySchemesAdapter(List<Scheme> mSchemesList, SchemesAdapter.SchemesAdapterListener listener) {
        mListener = listener;
        schemeList = mSchemesList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.my_scheme_item, parent, false);
        return new SchemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SchemeViewHolder schemeViewHolder = (SchemeViewHolder) holder;
        Scheme scheme = schemeList.get(position);

        schemeViewHolder.txtTitle.setText(scheme.getName());


        schemeViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnClick(position);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    public void notifyDataSetChanged(List<Scheme> mSchemesList) {
        this.schemeList = mSchemesList;
        notifyDataSetChanged();
    }

    public static class SchemeViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        View view;

        public SchemeViewHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_scheme_title);
            view = itemView;

        }
    }
}
