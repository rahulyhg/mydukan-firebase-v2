package org.app.mydukan.activities;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.PriceDropAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Product;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PriceDropActivity extends BaseActivity {

    RecyclerView priceDropList;
    private TextView mNoDataView;
    private TextView heading;
    private ImageView back;
    private TextView allPriceDrop, myPriceDrop;

    private SupplierBindData mSupplier;
    private String mCategoryId;
    private User userdetails;
    private HashMap<String, Integer> priceRange;
    int pmin, pmax;
    String ptype="";
    private MyDukan mApp;
    private Boolean isProductFetched = false;
    public HashMap<String, ArrayList<Product>> mProductList;
    private String mSerachText = null;
    private HashMap<String, Integer> priceDropCount = new HashMap<>();
    private PriceDropAdapter priceDropAdapter;
    ArrayList<Product> list = new ArrayList<Product>();
    List<String> categoryList = new ArrayList<>();
    Bundle bundle;
    String page = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_drop);

        //Initializing Widgets..
        priceDropList = (RecyclerView) findViewById(R.id.pricedrop_list);
        mApp = (MyDukan) getApplicationContext();
        mNoDataView = (TextView) findViewById(R.id.nodata_view);
        heading = (TextView) findViewById(R.id.heading);
        back = (ImageView)findViewById(R.id.back);
        allPriceDrop = (TextView) findViewById(R.id.allPriceDrop);
        myPriceDrop = (TextView) findViewById(R.id.myPriceDrop);

        heading.setText("Price Drop");

        //Getting Bundle..
        bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(AppContants.SUPPLIER)) {
                mSupplier = (SupplierBindData) bundle.getSerializable(AppContants.SUPPLIER);
            }
            if (bundle.containsKey(AppContants.CATEGORY_ID)) {
                mCategoryId = bundle.getString(AppContants.CATEGORY_ID);
                Log.i("Category", mCategoryId);
            }
            if (bundle.containsKey(AppContants.USER_DETAILS)) {
                userdetails = (User) bundle.getSerializable(AppContants.USER_DETAILS);
            }
            if (bundle.containsKey(AppContants.PRICE_TYPE)) {
                ptype = bundle.getString(AppContants.PRICE_TYPE);
                Log.i("Ptype", ptype);
            }
            if(bundle.containsKey("page")){
                page = bundle.getString("page");
            }
            else{
                page = "allPriceDrop";
            }
            if (bundle.containsKey(AppContants.PRICE_RANGE)){
                Log.i("PRICERANGE", "Found");
                priceRange = (HashMap<String, Integer>) bundle.getSerializable(AppContants.PRICE_RANGE);
                pmin = priceRange.get("Min");
                pmax = priceRange.get("Max");
            }

            if(page.equalsIgnoreCase("myPriceDrop")){
                allPriceDrop.setBackgroundColor(ContextCompat.getColor(this, R.color.authui_colorPrimary));
                allPriceDrop.setTextColor(ContextCompat.getColor(this, R.color.white));
                myPriceDrop.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                myPriceDrop.setTextColor(ContextCompat.getColor(this, R.color.authui_colorPrimary));
            }
            else{
                allPriceDrop.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                allPriceDrop.setTextColor(ContextCompat.getColor(this, R.color.authui_colorPrimary));
                myPriceDrop.setBackgroundColor(ContextCompat.getColor(this, R.color.authui_colorPrimary));
                myPriceDrop.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(page.equalsIgnoreCase("allPriceDrop")) {
            getProductList();
        }
        else if(page.equalsIgnoreCase("myPriceDrop")){
            getPriceDropProductList();
        }

        allPriceDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PriceDropActivity.this, PriceDropActivity.class);
                intent.putExtra(AppContants.CATEGORY_ID, mCategoryId);
                intent.putExtra(AppContants.SUPPLIER, mSupplier);
                intent.putExtra(AppContants.USER_DETAILS, userdetails);
                intent.putExtra("page", "allPriceDrop");
                startActivity(intent);
                finish();
            }
        });

        myPriceDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PriceDropActivity.this, PriceDropActivity.class);
                intent.putExtra(AppContants.CATEGORY_ID, mCategoryId);
                intent.putExtra(AppContants.SUPPLIER, mSupplier);
                intent.putExtra(AppContants.USER_DETAILS, userdetails);
                intent.putExtra("page", "myPriceDrop");
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getProductList() {
        showProgress();
        String uid = mApp.getFirebaseAuth().getCurrentUser().getUid();
        System.out.println("Category:."+mCategoryId);
        try {
            ApiManager.getInstance(getApplicationContext()).getSupplierProductList(mSupplier, mCategoryId,
                    mSerachText, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            if (!isProductFetched) {
                                mProductList = (HashMap<String, ArrayList<Product>>) data;

                                for(Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()){
                                    ArrayList<Product> prods = entry.getValue();
                                    list.addAll(entry.getValue());
                                    categoryList.add(entry.getKey());
                                    priceDropCount.put(entry.getKey(), prods.size());
                                    for(Iterator<Product> iterator = prods.iterator(); iterator.hasNext();){
                                        Product p = iterator.next();
                                        if(ptype.equals("DP")) {
                                            if (p.getDpInt() > pmax || p.getDpInt() < pmin) {
                                                iterator.remove();
                                            }
                                        }
                                        else if(ptype.equals("MOP")){
                                            if (p.getMopInt() > pmax || p.getMopInt() < pmin) {
                                                iterator.remove();
                                            }
                                        }
                                    }
                                }

                                try{
                                    for (Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()) {

                                        Collections.sort(entry.getValue(), new DateComparator());
                                        Collections.reverse(entry.getValue());
                                        Log.i("error", "error at: " + entry.getKey());
                                        //mList.put(entry.getKey(), entry.getValue());
                                        //Adding the tabs using addTab() method
//                                                tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                    }

                                    /*//Creating our pager adapter
                                    ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                            new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                    //Adding adapter to pager
                                    viewPager.setAdapter(adapter);*/


                                }catch (Exception e){
                                    e.printStackTrace();
                                    for (Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()) {
                                        //Adding the tabs using addTab() method
                                        if(entry.getValue().size() == 0){
                                            continue;
                                        }
//                                        tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                    }
                                   /* //Creating our pager adapter
                                    ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                            new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                    //Adding adapter to pager
                                    viewPager.setAdapter(adapter);*/
                                }

                                if (!mProductList.isEmpty()) {
                                    mNoDataView.setVisibility(View.GONE);
                                    if(!ptype.equals("")){
                                        /*for(Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()){
                                            ArrayList<Product> prods = entry.getValue();
                                            list = entry.getValue();
                                            for(Iterator<Product> iterator = prods.iterator(); iterator.hasNext();){
                                                Product p = iterator.next();
                                                if(ptype.equals("DP")) {
                                                    if (p.getDpInt() > pmax || p.getDpInt() < pmin) {
                                                        iterator.remove();
                                                    }
                                                }
                                                else if(ptype.equals("MOP")){
                                                    if (p.getMopInt() > pmax || p.getMopInt() < pmin) {
                                                        iterator.remove();
                                                    }
                                                }
                                            }
                                        }*/
                                    }

                                    arrangeBrandModels();

                                    /*HashMap<String, ArrayList<Product>> filteredList = new HashMap<String,ArrayList<Product>>();
                                    for (Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()) {
                                        if(entry.getValue().size() == 0){
                                            continue;
                                        }
                                        filteredList.put(entry.getKey(), entry.getValue());
                                    }

                                    if (mCategoryId.equals("-KX41ilBK4hjaSDIV419")) {  // mCatId = "-KX41ilBK4hjaSDIV419" name = "PRICE DROP"
                                        try{
                                            for (Map.Entry<String, ArrayList<Product>> entry : filteredList.entrySet()) {

                                                Collections.sort(entry.getValue(), new ProductListActivity.DateComparator());
                                                Collections.reverse(entry.getValue());
                                                Log.i("error", "error at: " + entry.getKey());
                                                //mList.put(entry.getKey(), entry.getValue());
                                                //Adding the tabs using addTab() method
//                                                tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                            }

                                            //Creating our pager adapter
                                            ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                    new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                            //Adding adapter to pager
                                            viewPager.setAdapter(adapter);


                                        }catch (Exception e){
                                            e.printStackTrace();
                                            for (Map.Entry<String, ArrayList<Product>> entry : filteredList.entrySet()) {
                                                //Adding the tabs using addTab() method
                                                if(entry.getValue().size() == 0){
                                                    continue;
                                                }
                                                tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                            }
                                            //Creating our pager adapter
                                            ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                    new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                            //Adding adapter to pager
                                            viewPager.setAdapter(adapter);
                                        }

                                    }
*/                                } else {
                                    mNoDataView.setText("There is no model in this Price Range, Swipe to check other categories");
                                    mNoDataView.setVisibility(View.VISIBLE);
                                }
                                dismissProgress();
                                isProductFetched = true;
                            }
                        }

                        @Override
                        public void onFailure(String response) {
                            dismissProgress();
                        }
                    });
        } catch (Exception e) {
            dismissProgress();
        }
    }

    private void getPriceDropProductList() {
        showProgress();
        String uid = mApp.getFirebaseAuth().getCurrentUser().getUid();
        System.out.println("Category:."+mCategoryId);
        try {
            ApiManager.getInstance(getApplicationContext()).getMyPriceDrop(mSupplier, uid, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            if (!isProductFetched) {
                                mProductList = (HashMap<String, ArrayList<Product>>) data;

                                for(Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()){
                                    ArrayList<Product> prods = entry.getValue();
                                    list.addAll(entry.getValue());
                                    categoryList.add(entry.getKey());
                                    priceDropCount.put(entry.getKey(), prods.size());
                                    for(Iterator<Product> iterator = prods.iterator(); iterator.hasNext();){
                                        Product p = iterator.next();
                                        if(ptype.equals("DP")) {
                                            if (p.getDpInt() > pmax || p.getDpInt() < pmin) {
                                                iterator.remove();
                                            }
                                        }
                                        else if(ptype.equals("MOP")){
                                            if (p.getMopInt() > pmax || p.getMopInt() < pmin) {
                                                iterator.remove();
                                            }
                                        }
                                    }
                                }

                                if (!mProductList.isEmpty()) {
                                    mNoDataView.setVisibility(View.GONE);
                                    if(!ptype.equals("")){
                                        /*for(Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()){
                                            ArrayList<Product> prods = entry.getValue();
                                            list = entry.getValue();
                                            for(Iterator<Product> iterator = prods.iterator(); iterator.hasNext();){
                                                Product p = iterator.next();
                                                if(ptype.equals("DP")) {
                                                    if (p.getDpInt() > pmax || p.getDpInt() < pmin) {
                                                        iterator.remove();
                                                    }
                                                }
                                                else if(ptype.equals("MOP")){
                                                    if (p.getMopInt() > pmax || p.getMopInt() < pmin) {
                                                        iterator.remove();
                                                    }
                                                }
                                            }
                                        }*/
                                    }

                                    arrangeBrandModels();

                                    /*HashMap<String, ArrayList<Product>> filteredList = new HashMap<String,ArrayList<Product>>();
                                    for (Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()) {
                                        if(entry.getValue().size() == 0){
                                            continue;
                                        }
                                        filteredList.put(entry.getKey(), entry.getValue());
                                    }

                                    if (mCategoryId.equals("-KX41ilBK4hjaSDIV419")) {  // mCatId = "-KX41ilBK4hjaSDIV419" name = "PRICE DROP"
                                        try{
                                            for (Map.Entry<String, ArrayList<Product>> entry : filteredList.entrySet()) {

                                                Collections.sort(entry.getValue(), new ProductListActivity.DateComparator());
                                                Collections.reverse(entry.getValue());
                                                Log.i("error", "error at: " + entry.getKey());
                                                //mList.put(entry.getKey(), entry.getValue());
                                                //Adding the tabs using addTab() method
//                                                tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                            }

                                            //Creating our pager adapter
                                            ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                    new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                            //Adding adapter to pager
                                            viewPager.setAdapter(adapter);


                                        }catch (Exception e){
                                            e.printStackTrace();
                                            for (Map.Entry<String, ArrayList<Product>> entry : filteredList.entrySet()) {
                                                //Adding the tabs using addTab() method
                                                if(entry.getValue().size() == 0){
                                                    continue;
                                                }
                                                tabLayout.addTab(tabLayout.newTab().setText(entry.getKey()));
                                            }
                                            //Creating our pager adapter
                                            ProductPagerFragment adapter = new ProductPagerFragment(getSupportFragmentManager(),
                                                    new ArrayList<>(filteredList.values()), mSupplier.isCartEnabled(),mSupplier);
                                            //Adding adapter to pager
                                            viewPager.setAdapter(adapter);
                                        }

                                    }
*/                                } else {
                                    mNoDataView.setText("There is no model in this Price Range, Swipe to check other categories");
                                    mNoDataView.setVisibility(View.VISIBLE);
                                }
                                dismissProgress();
                                isProductFetched = true;
                            }
                            else{
                                mNoDataView.setText("There is no model in this Price Range, Swipe to check other categories");
                                mNoDataView.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onFailure(String response) {
                            dismissProgress();
                        }
                    });
        } catch (Exception e) {
            dismissProgress();
        }
    }

    private void arrangeBrandModels(){
        for(int i=0; i<list.size(); i++){
            System.out.println("Category Id: "+list.get(i).getCategoryId());
            /*if(priceDropCount.containsKey(list.get(i))){
                priceDropCount.put(list.get(i).getCategoryId(), priceDropCount.get(list.get(i).getCategoryId()+1));
            }
            else{
                priceDropCount.put(list.get(i).getCategoryId(), 1);
            }*/
        }
        priceDropAdapter = new PriceDropAdapter(mProductList, PriceDropActivity.this, priceDropCount, categoryList, mSupplier, page);
        priceDropList.setLayoutManager(new LinearLayoutManager(this));
        priceDropList.setAdapter(priceDropAdapter);
    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    private class DateComparator implements Comparator<Product> {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     /*   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");*/

        public int compare(Product p1, Product p2) {
            //descending getPriceDrop().getStartdate();
            int result = 0;
            try {
                if ((p1.getPriceDrop() != null) && (p1.getPriceDrop() != null)) {
                    long mdate1 = p1.getPriceDrop().getStartdate();
                    long mdate2 = p2.getPriceDrop().getStartdate();
                    Date d1 = null;
                    Date d2 = null;
                    d1 = new Date(p1.getPriceDrop().getStartdate());
                    d2 = new Date(p2.getPriceDrop().getStartdate());

                    if (d1 == null || d2 == null)
                        return result;
                    result = d1.compareTo(d2);
                    //result = d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
             /*
            public String convertTime(long time){
            Date date = new Date(time);
            Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
            return format.format(date);
        }
               d1 = new Date(mdate1);
               d2 = new Date(mdate2);
               if ( d1 == null) {
                   result=-1;
                   return result ;
               }
               if (d2 == null) {
                   result=1;
                   return result ;
               }
               result= d1.compareTo(d2) * -1;
               return  compare(mdate1, mdate2);
               if (result != 0) {
                   return result;
               }else{
                   return 0;
               }

// if nothing is returned from the method this is returned by default
            return 0;*/
        }
    }
}
