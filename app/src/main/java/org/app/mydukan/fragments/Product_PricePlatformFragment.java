package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.utils.AppContants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Product_PricePlatformFragment extends Fragment {


    View mView;
    Context context;
    private Product mProduct;


    private String mProductDesc;
    private SupplierBindData mSupplier;

    private ProgressDialog mProgress;
    Product product;

    ListView list_pricePlatform;


    public Product_PricePlatformFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_product__price_platform, container, false);
        context = mView.getContext();



        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            try {
                mProduct = (Product) extras.getSerializable(AppContants.PRODUCT);
                mSupplier = (SupplierBindData) extras.getSerializable(AppContants.SUPPLIER);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


        list_pricePlatform = (ListView)  mView.findViewById(R.id.list_pricePlatform);

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
