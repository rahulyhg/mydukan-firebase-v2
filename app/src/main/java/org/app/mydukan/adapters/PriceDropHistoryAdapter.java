package org.app.mydukan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.PriceDropHistory;
import org.app.mydukan.data.ServiceCenterInfo;
import org.app.mydukan.fragments.PriceDropHistoryFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 02-01-2018.
 */

public class PriceDropHistoryAdapter extends BaseAdapter {

    // Declare Variables
    private Context mContext;
    private LayoutInflater inflater;
    private List<PriceDropHistory> priceDropHistories = null;
    private ArrayList<PriceDropHistory> arraylist;

    public PriceDropHistoryAdapter(Context context, ArrayList<PriceDropHistory> priceDropHistories) {

        this. mContext = context;
        this.priceDropHistories = priceDropHistories;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<PriceDropHistory>();
        this.arraylist.addAll(priceDropHistories);
    }

    public class ViewHolder {
        TextView txtDate;
        TextView txtDp;
        TextView txtMop;
        TextView txtDrop;
    }

    @Override
    public int getCount() {
        return priceDropHistories.size();
    }

    @Override
    public PriceDropHistory getItem(int position) {
        return priceDropHistories.get(position);
    }
/*
    @Override
    public Object getItem(int position) {
        return null;
    }*/

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {
            holder = new PriceDropHistoryAdapter.ViewHolder();
            view = inflater.inflate(R.layout.item_pricedrophistory, null);
            // Locate the TextViews in listview_item.xml
            holder.txtDate = (TextView) view.findViewById(R.id.history_date);
            holder.txtDp = (TextView) view.findViewById(R.id.history_DP);
            holder.txtMop = (TextView) view.findViewById(R.id.history_MOP);
            holder.txtDrop = (TextView) view.findViewById(R.id.history_Drop);
            view.setTag(holder);
        } else {
            holder = (PriceDropHistoryAdapter.ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.txtDate.setText(priceDropHistories.get(position).getDate());
        holder.txtDp.setText(priceDropHistories.get(position).getDp());
        holder.txtMop.setText(priceDropHistories.get(position).getMop());
        holder.txtDrop.setText(priceDropHistories.get(position).getDrop());

        return view;
    }
}
