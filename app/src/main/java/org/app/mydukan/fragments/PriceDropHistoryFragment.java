package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.app.mydukan.R;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.NetworkUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class PriceDropHistoryFragment extends Fragment {

    View mView;
    Context context;
    private Product mProduct;
    private ProgressDialog pDialog;
    private NetworkUtil networkUtil;
    private ProgressDialog mProgress;
    Product product;
    private SupplierBindData mSupplier;

    public PriceDropHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_price_drop_history, container, false);

        context = mView.getContext();
        networkUtil = new NetworkUtil();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            try {
                mProduct = (Product) extras.getSerializable(AppContants.PRODUCT);
                mSupplier = (SupplierBindData) extras.getSerializable(AppContants.SUPPLIER);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return mView;


    }

    public void showProgress() {

        try {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress.setCancelable(true);
            mProgress.setCanceledOnTouchOutside(false);
        } catch (Exception e) {

        }
    }

    public void dismissProgress() {
        try {
            if ( mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }
            mProgress = null;
        } catch (Exception e) {

        }
    }

}
