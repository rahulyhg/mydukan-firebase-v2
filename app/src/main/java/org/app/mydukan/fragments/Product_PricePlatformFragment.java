package org.app.mydukan.fragments;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import org.app.mydukan.adapters.CategoryAdapter;
import org.app.mydukan.adapters.KeySpecificationAdapter;
import org.app.mydukan.adapters.PricePlatformAdapter;
import org.app.mydukan.adapters.PricePlatformAdapterListener;
import org.app.mydukan.data.PlatForm_Info;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.app.mydukan.activities.ProductDescriptionActivity.fullpage;
import static org.app.mydukan.activities.ProductDescriptionActivity.mApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class Product_PricePlatformFragment extends Fragment implements  AdapterView.OnItemSelectedListener, PricePlatformAdapterListener {

    View mView;
    Context context;
    private Product mProduct;

    private String mProductDesc;
    private SupplierBindData mSupplier;

    private ProgressDialog mProgress;
    Product product;

    ListView list_pricePlatform;
    private TextView mOthersTextView;
    private HashMap<String, HashMap<String, String>> mProductList = new HashMap<>();

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
            try{
        /*    String comparePrice="";
            Set<String> platformName=mProduct.getmPlatforms().keySet();
            for (String mplatform:mProduct.getmPlatforms().keySet()) {
                comparePrice=comparePrice+mplatform+": "+"₹ "+mProduct.getmPlatforms().get(mplatform)+" | ";
            }*/


          /*  mProductList.putAll(mProduct.getmPlatforms());

            ArrayList<String> mData = new ArrayList();
            mData.addAll(mProductList.keySet());

            HashMap<String, String> companyFounder = new HashMap<String, String>();

            for ( String cData:mData) {
                companyFounder.put(cData,mProductList.get(cData).get("price"));
            }

            // get entrySet from HashMap object
            Set<Map.Entry<String, String>> companyFounderSet = companyFounder.entrySet();
            // convert HashMap to List of Map entries
            List<Map.Entry<String, String>> companyFounderListEntry =
                    new ArrayList<Map.Entry<String, String>>(companyFounderSet);

            // sort list of entries using Collections class utility method sort(ls, cmptr)
            Collections.sort(companyFounderListEntry,
                    new Comparator<Map.Entry<String, String>>() {
                            //Integer.valueOf(es2.getValue().trim())
                        @Override
                        public int compare(Map.Entry<String, String> es1,
                                           Map.Entry<String, String> es2) {
                            return Integer.valueOf(es1.getValue().trim()).compareTo(Integer.valueOf(es2.getValue().trim()));
                        }
                    });

             // store into LinkedHashMap for maintaining insertion order
            Map<String, String> companyFounderLHMap =
                    new LinkedHashMap<String, String>();

            // iterating list and storing in LinkedHahsMap
            for(Map.Entry<String, String> map : companyFounderListEntry){
                companyFounderLHMap.put(map.getKey(), map.getValue());
            }

            System.out.println("Sorting HashMap by its Values in descending order\n");

            // iterate LinkedHashMap to retrieved stored values
            for(Map.Entry<String, String> lhmap : companyFounderLHMap.entrySet()){
                System.out.println("Key : "  + lhmap.getKey() + "\t\t"
                        + "Value : "  + lhmap.getValue());
            }
*/
          /*  Collections.sort((List<HashMap<String, HashMap<String, String>>>) mProductList, new PriceComparator());
            Collections.reverse((List<?>) mProductList);*/

            PricePlatformAdapter adapter = new PricePlatformAdapter(context,mProduct.getmPlatforms(),this);

            list_pricePlatform.setAdapter(adapter);

        }catch (Exception e){
            e.printStackTrace();

            PricePlatformAdapter adapter = new PricePlatformAdapter(context,mProduct.getmPlatforms(),this);
            list_pricePlatform.setAdapter(adapter);
        }


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void OnClick(int position, String platForm_URL) {
        try{
            String  mUrl= platForm_URL;
            if(mUrl!=null){
               /* Intent intent = new Intent(context, WebviewActivity.class);
                intent.putExtra(AppContants.VIEW_PLATFORM,mUrl );
                startActivity(intent);*/
              //  openWebPage(mUrl);
              startOpenWebPage(mUrl);
            }
        }catch (Exception e){
            e.printStackTrace();

        }
    }
    public boolean startOpenWebPage(String url) {
        boolean result = false;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;

        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

        try {
            startActivity(intent);
            result = true;
        }catch (Exception e){
            try {
                if (url.startsWith("http://")){
                    startOpenWebPage(url.replace("http://","https://"));
                }
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(context, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
                ex.printStackTrace();
            }
        }
        return result;
    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No application can handle this request. Please install a web browser or check your URL.",  Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    /*private class PriceComparator implements Comparator<HashMap<String, HashMap<String, String>>> {

   *//*     public int compare(Product p1, Product p2) {
            if (mApp.getUtils().isStringEmpty(p1.getPrice())) {
                p1.setPrice("0");
                //mItem.get(item).get("price")
            }

            if (mApp.getUtils().isStringEmpty(p2.getPrice())) {
                p2.setPrice("0");
            }

            int price1 = Integer.valueOf(p1.getPrice().trim());
            int price2 = Integer.valueOf(p2.getPrice().trim());

            if (price1 > price2) {
                return 1;
            } else if (price1 < price2) {
                return -1;
            } else {
                return 0;
            }
        }*//*

        @Override
        public int compare(HashMap<String, HashMap<String, String>> obj1, HashMap<String, HashMap<String, String>> obj2) {

            int result=0;
           ArrayList mData = new ArrayList();
            mData.addAll(obj1.keySet());

            ArrayList kData = new ArrayList();
            kData.addAll(obj2.keySet());


            HashMap<String, HashMap<String, String>> price1 = (HashMap<String, HashMap<String, String>>) obj1;
            HashMap<String, HashMap<String, String>> price2 = (HashMap<String, HashMap<String, String>>) obj2;

            for ( Object cData:mData) {
                HashMap<String, String> companyFounder =
                        new HashMap<String, String>();
                result= price1.get(cData).get("price").compareTo(price2.get(kData).get("price"));
            }
            return result;
        }
    }*/

}
