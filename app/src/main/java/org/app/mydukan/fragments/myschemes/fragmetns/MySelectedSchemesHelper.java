package org.app.mydukan.fragments.myschemes.fragmetns;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import org.app.mydukan.activities.CategoryListActivity;
import org.app.mydukan.activities.ProductListActivity;
import org.app.mydukan.data.Category;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.data.SchemeRecord;
import org.app.mydukan.data.Supplier;
import org.app.mydukan.data.SupplierBindData;
import org.app.mydukan.data.User;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rojesharunkumar on 14/11/17.
 */

public class MySelectedSchemesHelper {

    private ArrayList<ArrayList<Scheme>> mAllProductsList;
    private List<List<Scheme>> mySelectedList;
    private Scheme myCurrentBrand;
    private ArrayList<SchemeRecord> recordList;

    private static MySelectedSchemesHelper instance;

    private ArrayList<Category> categoryList;
    private  SupplierBindData  currentSupplier;


    private MySelectedSchemesHelper() {
        mySelectedList = new ArrayList<>();
    }


    public static MySelectedSchemesHelper getInstance() {
        if (instance == null) {
            instance = new MySelectedSchemesHelper();
        }
        return instance;
    }

/*
    public List<Scheme> getList() {
        return mySelectedList;
    }
*/


    public void addBrand(Scheme selectedBrand) {

        if (!checkIsMyBrandAdded(selectedBrand)) {
            myCurrentBrand = selectedBrand;
            // mySelectedList.add(selectedBrand);
        }
    }

    private boolean checkIsMyBrandAdded(Scheme selectedBrand) {
        if (mySelectedList != null && selectedBrand != null) {
            for (int i = 0; i < mySelectedList.size(); i++) {
                /*if (mySelectedList.get(i).getSchemeId().equals(selectedBrand.getSchemeId())) {
                    return true;
                }*/
            }
            return false;
        }
        return false;
    }


    private int getPosition(String brandId) {
        if (mySelectedList != null) {
            for (int i = 0; i < mySelectedList.size(); i++) {
              /*  if (brandId.equals(mySelectedList.get(i).getSchemeId()))
                    return i;*/
            }
        }
        return 0;
    }

    public Scheme getCurrentBrand() {
        return myCurrentBrand;
    }

    public void reset() {
        mySelectedList = new ArrayList<>();
        myCurrentBrand = null;
    }

/*
    public void setList(List<Scheme> mySelectedList) {
        this.mySelectedList = mySelectedList;
    }
*/

    public void setAllProductsList(ArrayList<ArrayList<Scheme>> mySelectedList) {
        this.mAllProductsList = mySelectedList;
    }

    public List<ArrayList<Scheme>> getAllProductList() {
        return mAllProductsList;
    }


    public List<List<Scheme>> getAllSelectedSchemesList() {
        return mySelectedList;
    }

    public void updateMySelectedList(ArrayList<SchemeRecord> mRecordList) {
        if(mRecordList == null)
            return;
        // Set already selected list
        if (mySelectedList == null) {
            mySelectedList = new ArrayList<>();
        } else {
            mySelectedList.clear();
        }
        List<Scheme> schemesList;
        Scheme scheme;
        List<Scheme> mySchemesList, selectedSchemesList = new ArrayList<>();

        for(int i =0;i<mAllProductsList.size();i++){
            selectedSchemesList = new ArrayList<>();
            schemesList = mAllProductsList.get(i);
            for(int j =0;j<schemesList.size();j++){
                scheme = schemesList.get(j);
                for(int k =0;k<mRecordList.size();k++){
                    if (mRecordList.get(k).getSchemeinfo().getId().equalsIgnoreCase(scheme.getSchemeId())) {
                        mAllProductsList.get(i).get(j).setHasEnrolled(true);
                        selectedSchemesList.add(scheme);
                    }
                }
            }
            if (selectedSchemesList != null & selectedSchemesList.size() > 0) {
                mySelectedList.add(selectedSchemesList);
            }
        }
    }


    public void updateAt(ArrayList<Scheme> mSchemeList, int pos) {
        mAllProductsList.set(pos, mSchemeList);

        // Checking for the category
        for (int i = 0; i < mySelectedList.size(); i++) {
            if (mySelectedList.get(i).get(0).getCategory().equals(mSchemeList.get(0))) {
                mySelectedList.set(i, mSchemeList);
                break;
            }
        }
    }

    public ArrayList<SchemeRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<SchemeRecord> recordList) {
        this.recordList = recordList;
    }

    public void addSchemeAt(int brandPos, int pos, boolean isChecked) {
        //TODO
        Scheme scheme =mAllProductsList.get(brandPos).get(pos);

        // Check that item is present or not, if present
        if(isChecked){
            // Add
          //  scheme.setHasEnrolled(isChecked);
            mAllProductsList.get(brandPos).get(pos).setHasEnrolled(isChecked);


           // updateAt(mAllProductsList.get(brandPos),pos);

        }else{
            // Remove if present
            mAllProductsList.get(brandPos).get(pos).setHasEnrolled(isChecked);


        }

    }

    public void storeCategoryList(ArrayList<Category> mCategoryList) {
        this.categoryList = mCategoryList;
    }


    public String getCategoryId(String category){
        if(categoryList != null && categoryList.size() >0 && category != null) {
            for (int i = 0; i < categoryList.size(); i++) {
                if(categoryList.get(i).getName().contains(category)){
                    return categoryList.get(i).getId();
                }
            }
        }
        return null;
    }

    public void saveCurrentSupplier(SupplierBindData supplier) {
        this.currentSupplier = supplier;
    }

    public SupplierBindData getCurrentSupplier(){
        return currentSupplier;
    }
/*
    public void saveCurrentUser(User supplier) {
        this.currentSupplier = supplier;
    }

    public Supplier getCurrentSupplier(){
        return currentSupplier;
    }*/

}
