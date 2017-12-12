package org.app.mydukan.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class PriceDropActivity extends BaseActivity {

    RecyclerView priceDropList;
    private TextView mNoDataView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_drop);

        //Initializing Widgets..
        priceDropList = (RecyclerView) findViewById(R.id.pricedrop_list);
        mApp = (MyDukan) getApplicationContext();
        mNoDataView = (TextView) findViewById(R.id.nodata_view);

        //Getting Bundle..
        Bundle bundle = getIntent().getExtras();
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
            if (bundle.containsKey(AppContants.PRICE_RANGE)){
                Log.i("PRICERANGE", "Found");
                priceRange = (HashMap<String, Integer>) bundle.getSerializable(AppContants.PRICE_RANGE);
                pmin = priceRange.get("Min");
                pmax = priceRange.get("Max");
            }
        }

        getProductList();

    }

    private void getProductList() {
        showProgress();
        String uid = mApp.getFirebaseAuth().getCurrentUser().getUid();
        try {
            ApiManager.getInstance(getApplicationContext()).getSupplierProductList(mSupplier, mCategoryId,
                    mSerachText, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {
                            if (!isProductFetched) {
                                mProductList = (HashMap<String, ArrayList<Product>>) data;

                                for(Map.Entry<String, ArrayList<Product>> entry : mProductList.entrySet()){
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

    private void arrangeBrandModels(){
        List<String> categoryList = new ArrayList<>();
        for(int i=0; i<list.size(); i++){
            if(priceDropCount.containsKey(list.get(i).getCategoryId())){
                priceDropCount.put(list.get(i).getCategoryId(), priceDropCount.get(list.get(i).getCategoryId()+1));
            }
            else{
                priceDropCount.put(list.get(i).getCategoryId(), 1);
                categoryList.add(list.get(i).getCategoryId());
            }
        }
        priceDropAdapter = new PriceDropAdapter(list, PriceDropActivity.this, priceDropCount, categoryList);
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
}
