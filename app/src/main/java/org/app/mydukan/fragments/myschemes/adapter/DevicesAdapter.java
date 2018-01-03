package org.app.mydukan.fragments.myschemes.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.fragments.myschemes.calculator.DeviceHelper;
import org.app.mydukan.fragments.myschemes.model.Device;

import java.util.List;



/**
 * Created by rojesharunkumar on 20/10/17.
 */

public class DevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<Device> deviceList;

    public DevicesAdapter() {
        deviceList = DeviceHelper.getInstance().getDeviceList();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.calculaor_header, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DeviceViewHolder deviceViewHolder = (DeviceViewHolder) holder;
        Device device = deviceList.get(position);

        deviceViewHolder.txtModel.setText(device.getModel());
        deviceViewHolder.txtPercent.setText(device.getPercent());
        deviceViewHolder.txtValue.setText(device.getValue());
        deviceViewHolder.txtIncentiveAmount.setText(device.getIncentiveAmount());
        deviceViewHolder.txtDp.setText(device.getDp());
        deviceViewHolder.txtQuantity.setText(device.getQuantity());

    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView txtModel;
        TextView txtTarget;
        TextView txtQuantity;
        TextView txtDp;
        TextView txtIncentiveAmount;
        TextView txtValue;
        TextView txtPercent;


        public DeviceViewHolder(View itemView) {
            super(itemView);
            txtModel = (TextView) itemView.findViewById(R.id.lbl_model);
            txtTarget = (TextView) itemView.findViewById(R.id.lbl_target);
            txtQuantity = (TextView) itemView.findViewById(R.id.lbl_qty);
            txtDp = (TextView) itemView.findViewById(R.id.lbl_dp_withvat);
            txtIncentiveAmount = (TextView) itemView.findViewById(R.id.lbl_incentiveamount);
            txtValue = (TextView) itemView.findViewById(R.id.lbl_value);
            txtPercent = (TextView) itemView.findViewById(R.id.lbl_schemeperhandset);
        }
    }
}
