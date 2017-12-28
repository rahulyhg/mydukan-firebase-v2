package org.app.mydukan.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.PriceDropAdapter;
import org.app.mydukan.adapters.RecordInfoAdapter;
import org.app.mydukan.adapters.RecordsClaimAdapter;
import org.app.mydukan.data.Record;
import org.app.mydukan.data.RecordInfo;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;

public class RecordClaimActivity extends BaseActivity {

    Context context;
    ArrayList<Record> mRecordList;
    ArrayList<Record> claimsRecord;
    TextView mNoDataView;
    TextView total;
    TextView heading;
    ImageView back;
    RecyclerView mRecyclerView;
    int totalClaim = 0;
    ArrayList<String> brandName = new ArrayList<>();
    String brand = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_claim);

        //Initializing Widgets
        mRecordList = new ArrayList<>();
        mNoDataView = (TextView) findViewById(R.id.noData);
        mRecyclerView = (RecyclerView) findViewById(R.id.list_recordsClaims);
        back = (ImageView) findViewById(R.id.back);
        total = (TextView) findViewById(R.id.total);
        heading = (TextView) findViewById(R.id.heading);

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        if(bundle != null && bundle.containsKey(AppContants.RECORD)){
            mRecordList = (ArrayList<Record>) bundle.get(AppContants.RECORD);
        }
        if(bundle != null && bundle.containsKey(AppContants.BRAND_NAME)){
            brand = bundle.getString(AppContants.BRAND_NAME);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(brand != null && brand.equalsIgnoreCase("")) {
            fetchTheRecords();
        }
        else{
            setAdapter();
        }
    }

    private void setAdapter() {

        totalClaim = 0;
        heading.setText(brand + " Claims");
        claimsRecord = new ArrayList<>();

        for(int i=0; i<mRecordList.size(); i++){
            if(mRecordList.get(i).getBrandName().equalsIgnoreCase(brand)){
                claimsRecord.add(mRecordList.get(i));
                totalClaim += mRecordList.get(i).getRecordList().size() * Integer.parseInt(mRecordList.get(i).getRecordList().get(0).getPrice());
            }
        }

        total.setText("₹ " + String.valueOf(totalClaim));

        if (claimsRecord!=null && claimsRecord.size()>0){
            try{

                /*mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                        RecordClaimActivity.this.getApplicationContext(), false));*/

                mRecyclerView.setLayoutManager(new LinearLayoutManager(RecordClaimActivity.this));

                RecordsClaimAdapter claimadapter = new RecordsClaimAdapter(RecordClaimActivity.this, claimsRecord, "model");

                mRecyclerView.setAdapter(claimadapter);

            }catch (Exception e){
                e.printStackTrace();

               /* mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                        RecordClaimActivity.this.getApplicationContext(), false));*/

                mRecyclerView.setLayoutManager(new LinearLayoutManager(RecordClaimActivity.this));

                RecordsClaimAdapter adapter = new RecordsClaimAdapter(RecordClaimActivity.this, mRecordList, "brand");
                mRecyclerView.setAdapter(adapter);
            }


        }else{
            /*mOthersTextView.setVisibility(View.VISIBLE);
            mOthersTextView.setText("Not available");*/
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void fetchTheRecords(){
        showProgress();
        ApiManager.getInstance(RecordClaimActivity.this).getRecordsList(new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                mRecordList = (ArrayList<Record>)data;
                claimsRecord = new ArrayList<>();
                if(mRecordList.size() > 0){
                    for(int i=0; i<mRecordList.size(); i++) {
                        Record record = new Record();
                        ArrayList<RecordInfo> recordInfos = mRecordList.get(i).getRecordList();
                        ArrayList<RecordInfo> infos = new ArrayList<>();
                        for(int j=0; j<recordInfos.size(); j++){
                            if(recordInfos.get(j).getStatus().equalsIgnoreCase("claim") ||
                                    recordInfos.get(j).getStatus().equalsIgnoreCase("pending by distributor")){
                                totalClaim += Integer.parseInt(recordInfos.get(j).getPrice());
                                infos.add(recordInfos.get(j));
                            }
                        }
                        if(infos.size() > 0){
                            record = mRecordList.get(i);
                            record.setRecordList(infos);
//                            record.setBrandName(mRecordList.get(i).getBrandName());
//                            record.setRecordList(infos);
//                            record.setProductBrand(mRecordList.get(i).getProductCategoryID());
//                            record.setRecordId(mRecordList.get(i).getRecordId());
                            claimsRecord.add(record);
                            if(brandName != null && !brandName.contains(mRecordList.get(i).getBrandName())) {
                                brandName.add(mRecordList.get(i).getBrandName());
                            }
                        }
                    }
                    total.setText("₹ "+totalClaim);
                 setUpRecordsClaims();
                }



                /*if(mRecordList.isEmpty()){
                    mNoDataView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
//                    mAdapter.addItems(mRecordList);
//                    mAdapter.notifyDataSetChanged();
                }*/
            }

            @Override
            public void onFailure(String response) {

            }
        });
    }



 private void setUpRecordsClaims() {

        if (claimsRecord!=null && claimsRecord.size()>0){
            try{

                /*mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                        RecordClaimActivity.this.getApplicationContext(), false));*/

                mRecyclerView.setLayoutManager(new LinearLayoutManager(RecordClaimActivity.this));

                RecordsClaimAdapter claimadapter = new RecordsClaimAdapter(RecordClaimActivity.this, claimsRecord, brandName, "brand");

                mRecyclerView.setAdapter(claimadapter);

            }catch (Exception e){
                e.printStackTrace();

               /* mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                        RecordClaimActivity.this.getApplicationContext(), false));*/

                mRecyclerView.setLayoutManager(new LinearLayoutManager(RecordClaimActivity.this));

                RecordsClaimAdapter adapter = new RecordsClaimAdapter(RecordClaimActivity.this, mRecordList, brandName, "brand");
                mRecyclerView.setAdapter(adapter);
            }


        }else{
            /*mOthersTextView.setVisibility(View.VISIBLE);
            mOthersTextView.setText("Not available");*/
        }
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
