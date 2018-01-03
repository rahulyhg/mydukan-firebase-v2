package org.app.mydukan.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.activities.RecordClaimActivity;
import org.app.mydukan.data.Record;
import org.app.mydukan.data.RecordInfo;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;

/**
 * Created by user on 15-12-2017.
 */

public class RecordsClaimAdapter extends RecyclerView.Adapter<RecordsClaimAdapter.ViewHolder> {


    private Context mContext;
    private ArrayList<Record> mRecordList = new ArrayList<>();
    private ArrayList<String> brandName = new ArrayList<>();
    private String page = "";

    public RecordsClaimAdapter(Context mContext, ArrayList<Record> mRecordList, ArrayList<String> brandName, String page) {
        this.mContext = mContext;
        this.mRecordList = mRecordList;
        this.brandName = brandName;
        this.page = page;
    }

    public RecordsClaimAdapter(Context mContext, ArrayList<Record> mRecordList, String page){
        this.mContext = mContext;
        this.mRecordList = mRecordList;
        this.page = page;
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
    public void onBindViewHolder(RecordsClaimAdapter.ViewHolder holder, final int position) {

        if(page != null && page.equalsIgnoreCase("brand")) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, RecordClaimActivity.class);
                    intent.putExtra(AppContants.RECORD, mRecordList);
                    intent.putExtra(AppContants.BRAND_NAME, brandName.get(position));
                    mContext.startActivity(intent);
                }
            });

            int qty = 0, price = 0;
            for (int i = 0; i < mRecordList.size(); i++) {
                Record record = mRecordList.get(i);
                if (record.getBrandName().equalsIgnoreCase(brandName.get(position))) {
                    System.out.println("Quantity:" + record.getRecordList().size());
                    System.out.println("Price:" + Integer.parseInt(record.getRecordList().get(0).getPrice()));
                    qty += record.getRecordList().size();
                    price += record.getRecordList().size() * Integer.parseInt(record.getRecordList().get(0).getPrice());
                }
           /* holder.model_name.setText(String.valueOf(record.getBrandName()));
            holder.model_quantity.setText(String.valueOf(record.getRecordList().size()));
            if (record.getRecordList().size() > 0) {
                int quantity, mPrice;
                quantity = record.getRecordList().size();
                mPrice = Integer.parseInt(record.getRecordList().get(0).getPrice());
                long TClaim = mPrice * quantity;
                holder.model_claims.setText(String.valueOf("₹ " + TClaim));
            } else {
                holder.model_claims.setText("-");

            }*/
            }
            holder.model_name.setText(brandName.get(position));
            holder.model_quantity.setText(String.valueOf(qty));
            holder.model_claims.setText(String.valueOf("₹ " + price));
        }
        else if(page != null && page.equalsIgnoreCase("model")){
//            for(int i=0; i<mRecordList.size(); i++){
            int i = position;
                holder.model_name.setText(mRecordList.get(i).getProductname());
                holder.model_quantity.setText(String.valueOf(mRecordList.get(i).getRecordList().size()));
                long price = mRecordList.get(i).getRecordList().size() * Integer.parseInt(mRecordList.get(i).getRecordList().get(0).getPrice());
                holder.model_claims.setText(String.valueOf(price));
//            }
        }
    }

    @Override
    public int getItemCount() {
        if(page != null && page.equalsIgnoreCase("brand")) {
            return brandName.size();
        }
        else{
            return mRecordList.size();
        }
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
