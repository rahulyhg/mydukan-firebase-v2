package org.app.mydukan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.Record;
import org.app.mydukan.data.RecordInfo;

import java.util.ArrayList;

/**
 * Created by user on 15-12-2017.
 */

public class RecordsClaimAdapter extends RecyclerView.Adapter<RecordsClaimAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Record> mRecordList = new ArrayList<>();


    public RecordsClaimAdapter(Context mContext, ArrayList<Record> mRecordList) {
        this.mContext = mContext;
        this.mRecordList = mRecordList;
    }

    @Override
    public RecordsClaimAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = null;
        contactView = inflater.inflate(R.layout.items_my_claims, parent, false);
        // Return a new holder instance
        return new RecordsClaimAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(RecordsClaimAdapter.ViewHolder holder, int position) {

        Record record = mRecordList.get(position);

        holder.model_name.setText(String.valueOf(record.getProductname()));
        holder.model_quantity.setText(String.valueOf(record.getRecordList().size()));
        if (record.getRecordList().size()>0){
            long quantity,mprice;
            quantity = record.getRecordList().size();
            mprice= Integer.parseInt(record.getRecordList().get(0).getPrice());

            long TClaim = mprice*quantity;
            holder.model_claims.setText(String.valueOf("₹ "+TClaim));
        }else{
            holder.model_claims.setText("-");

        }


    }

    @Override
    public int getItemCount() {
        return mRecordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView model_name;
        public TextView model_quantity;
        public TextView model_claims;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.model_name = (TextView) itemView.findViewById(R.id.model_name);
            this.model_quantity = (TextView) itemView.findViewById(R.id.model_quantity);
            this.model_claims = (TextView) itemView.findViewById(R.id.model_claims);
        }
    }
}
