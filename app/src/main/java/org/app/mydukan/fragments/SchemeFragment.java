package org.app.mydukan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.activities.Schemes.SchemeDetailsActivity;
import org.app.mydukan.adapters.SchemesAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Scheme;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by arpithadudi on 9/11/16.
 */
public class SchemeFragment extends android.support.v4.app.Fragment implements SchemesAdapter.SchemesAdapterListener {

    //Ui reference
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;

    //Variables
    private String mSupplierId;
    private String mSupplierName;
    private ArrayList<Scheme> mSchemeList = new ArrayList<>();
    private SchemesAdapter mSchemesAdapter;
    private MyDukan mApp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_scheme, container, false);
        try {
            mApp = (MyDukan) getActivity().getApplicationContext();

            Bundle bundle = getArguments();
            if (bundle.containsKey(AppContants.SUPPLIER_ID)) {
                mSupplierId = bundle.getString(AppContants.SUPPLIER_ID);
            }
            if (bundle != null && bundle.containsKey(AppContants.SUPPLIER_NAME)) {
                mSupplierName = bundle.getString(AppContants.SUPPLIER_NAME);
            }
            setupSchemeCard(v);
            setTheSchemes();
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
        }
        return v;
    }

    private void setupSchemeCard(View v) {
        View supplierView = v.findViewById(R.id.schemelayout);
        mSchemesAdapter = new SchemesAdapter(getActivity(), this);

        //setup the recyclerview
        mRecyclerView = (RecyclerView) supplierView.findViewById(R.id.listview);
        mEmptyView = (TextView) supplierView.findViewById(R.id.nodata_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mSchemesAdapter);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getActivity().getApplicationContext(), false));
    }

    public void setData(ArrayList<Scheme> schemeList){
        mSchemeList = schemeList;
    }

    private void setTheSchemes() {
        if (mSchemeList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            return;
        } else {
            mEmptyView.setVisibility(View.GONE);
        }

        Collections.sort(mSchemeList,new DateComparator());
        Collections.reverse(mSchemeList);

        mSchemesAdapter.addItems(mSchemeList);
        mSchemesAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnClick(int position) {
        Intent intent = new Intent(getActivity(), SchemeDetailsActivity.class);
        intent.putExtra(AppContants.SCHEME, mSchemeList.get(position));
        intent.putExtra(AppContants.SUPPLIER_NAME, mSupplierName);
        intent.putExtra(AppContants.SUPPLIER_ID,mSupplierId);
        startActivity(intent);
    }

    private class DateComparator implements Comparator<Scheme> {
        public int compare(Scheme s1, Scheme s2) {
            return s1.getStartdate()<s2.getStartdate()?-1:
                    s1.getStartdate()>s2.getStartdate()?1:0;
        }
    }
}
