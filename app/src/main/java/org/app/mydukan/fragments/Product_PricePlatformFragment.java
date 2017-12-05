package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.app.mydukan.R;
import org.app.mydukan.adapters.KeySpecificationAdapter;
import org.app.mydukan.adapters.PricePlatformAdapter;
import org.app.mydukan.data.PlatForm_Info;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.util.HashMap;
import java.util.Set;

import static org.app.mydukan.activities.ProductDescriptionActivity.fullpage;
import static org.app.mydukan.activities.ProductDescriptionActivity.mApp;

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
    private TextView mOthersTextView;


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
        mOthersTextView = (TextView) mView.findViewById(R.id.othersTextView);
        fetchProductAndShow();
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
    private void fetchProductAndShow(){
        showProgress();
        ApiManager.getInstance(context).getProductDetails(mProduct.getProductId(),
                new ApiResult() {
                    @Override
                    public void onSuccess(Object data) {
                        product = (Product)data;
                        if(product != null) {
                            product.setmPlatforms(mProduct.getmPlatforms());
                        }
                        dismissProgress();
                        setupOthers();
                    }
                    @Override
                    public void onFailure(String response) {
                        dismissProgress();
                        setupOthers();
                    }
                });
    }

    private void setupOthers(){
        String othersStr = "";





        //=================================


/*
        for (String attKey: mProduct.getmPlatforms().keySet()) {
            String value = mProduct.getmPlatforms().get(attKey);

            if(!mApp.getUtils().isStringEmpty(othersStr) && !othersStr.endsWith(", ")){
                othersStr += ", ";
            }

            if(!mApp.getUtils().isStringEmpty(value)){
                if(attKey.equalsIgnoreCase("amazon")){
                    othersStr += value + " " + getString(R.string.amazon);
                }
                else if(attKey.equalsIgnoreCase("snapdeal")){
                    othersStr += value + " " + getString(R.string.snapdeal);
                }
                else if(attKey.equalsIgnoreCase("paytm")){
                    othersStr += value + " " + getString(R.string.paytm);
                }
                else if(attKey.equalsIgnoreCase("lockthedeal")){
                    othersStr += value + " " + getString(R.string.lockthedeal);
                }
                else if(attKey.equalsIgnoreCase("shopclues")){
                    othersStr += value + " " + getString(R.string.shopclues);
                }
                else if(attKey.equalsIgnoreCase("flipkart")){
                    othersStr += value + " " + getString(R.string.flipkart);
                }
                else if(attKey.equalsIgnoreCase("gadget360")){
                    othersStr += value + " " + getString(R.string.gadget360);
                }
            }
        }
*/


        if (mProduct.getmPlatforms()!=null && mProduct.getmPlatforms().size()>0){

        /*    String comparePrice="";
            Set<String> platformName=mProduct.getmPlatforms().keySet();
            for (String mplatform:mProduct.getmPlatforms().keySet()) {
                comparePrice=comparePrice+mplatform+": "+"₹ "+mProduct.getmPlatforms().get(mplatform)+" | ";
            }*/
            PricePlatformAdapter adapter = new PricePlatformAdapter(context, mProduct.getmPlatforms());
            list_pricePlatform.setAdapter(adapter);

          /*  list_pricePlatform.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    HashMap<String,PlatForm_Info> hashMap= mProduct.getmPlatforms();

                    Toast.makeText(getContext(),"Clicked"+" "+position,Toast.LENGTH_SHORT).show();
                }
            });*/

        }else{
            mOthersTextView.setVisibility(View.VISIBLE);
            mOthersTextView.setText("Not available");
        }

       /* if(!mApp.getUtils().isStringEmpty(othersStr)){
            // mOthersTextView.setText(othersStr);
            // showProgress(false);
            // KeySpecificationAdapter adapter = new KeySpecificationAdapter(context,  mProduct.getAttributes());
            // ksListView.setAdapter(adapter);


        } else {
      mOthersTextView.setVisibility(View.VISIBLE);
            mOthersTextView.setText("Not available");
        }*/
    }

}
