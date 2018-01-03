package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.PriceDropHistoryAdapter;
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

    ListView list_pricedrophistory;
    private TextView mOthersTextView;
    LinearLayout pricedrophistoryLabels;

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

        list_pricedrophistory = (ListView)  mView.findViewById(R.id.list_pricedrophistory);
        mOthersTextView = (TextView) mView.findViewById(R.id.othersTextView);

        pricedrophistoryLabels = (LinearLayout) mView.findViewById(R.id.pricedrophistoryLabels);

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
                                priceDropHistories = (ArrayList<PriceDropHistory>) data;
//                                mOthersTextView.setText(priceDropHistories.get(0).getDp());

                            }
                            setupOthers();

                        }catch (Exception e){
                            dismissProgress();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String response) {
                        try {
                            setupOthers();
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

    private void setupOthers(){
        String othersStr = "";


        if (priceDropHistories!=null && !priceDropHistories.isEmpty()){
            try{

                PriceDropHistoryAdapter adapter = new PriceDropHistoryAdapter(context,priceDropHistories);

                list_pricedrophistory.setAdapter(adapter);

            }catch (Exception e){
                e.printStackTrace();

                PriceDropHistoryAdapter adapter = new PriceDropHistoryAdapter(context,priceDropHistories);
                list_pricedrophistory.setAdapter(adapter);
            }


        }else{
            mOthersTextView.setVisibility(View.VISIBLE);
            mOthersTextView.setText("Not available");
            pricedrophistoryLabels.setVisibility(View.GONE);
        }

    }

}
