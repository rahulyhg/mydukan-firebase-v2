package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.adapters.PricePlatformAdapter;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.io.PrintWriter;
import java.io.StringWriter;

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
        try {
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

            list_pricePlatform = (ListView) mView.findViewById(R.id.list_pricePlatform);
            mOthersTextView = (TextView) mView.findViewById(R.id.othersTextView);
            fetchProductAndShow();
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onCreate : ",errors.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onCreate : ",errors.toString());
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
    private void fetchProductAndShow(){
        try {
            showProgress();
            ApiManager.getInstance(context).getProductDetails(mProduct.getProductId(),
                    new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            product = (Product) data;
                            if (product != null) {
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
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - fetchProductAndShow : ",e.toString());
            Crashlytics.log(0,"Exception - Product_PricePlatformFragment - fetchProductAndShow : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - fetchProductAndShow : ",errors.toString());
            Crashlytics.log(0,"1 - Product_PricePlatformFragment - fetchProductAndShow : ",errors.toString());
        }
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
