package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.app.mydukan.R;
import org.app.mydukan.data.PriceDropHistory;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.NetworkUtil;

import java.util.ArrayList;

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

    ArrayList<PriceDropHistory> priceDropHistories;

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
                fetchPrice_HistryPage();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return mView;


    }
//=============== Get the Pr1ce drop History from The data Base. ===============

    private void fetchPrice_HistryPage() {
        showProgress();
        priceDropHistories = new ArrayList<>();
        ApiManager.getInstance(context).getPriceHistryDetails(mProduct.getProductId(),
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        try{
                            dismissProgress();
                            if (data!=null){
                                priceDropHistories= (ArrayList<PriceDropHistory>) data;

                            }

                        }catch (Exception e){
                            dismissProgress();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        try {
                            dismissProgress();

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }

    //===========================================

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
