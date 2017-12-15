package org.app.mydukan.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.adapters.PriceDropAdapter;
import org.app.mydukan.adapters.RecordPriceDropApapter;
import org.app.mydukan.data.Record;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecordPriceDropActivity extends BaseActivity {

    RecyclerView myPriceDropList;
    ImageView back;
    TextView heading;
    TextView mNoDataView;

    private ArrayList<Record> mRecordList;
    private HashMap<String, Integer> recordMap = new HashMap<>();
    private List<String> brandList = new ArrayList<>();
    private RecordPriceDropApapter priceDropApapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_price_drop);

        //Initializing Widgets
        myPriceDropList = (RecyclerView) findViewById(R.id.pricedrop_list);
        back = (ImageView) findViewById(R.id.back);
        heading = (TextView) findViewById(R.id.heading);
        mNoDataView = (TextView) findViewById(R.id.nodata_view);

        heading.setText("My Price Drop");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(RecordPriceDropActivity.this, MyRecordsActivity.class);
                startActivity(intent);*/
                onBackPressed();
            }
        });

        fetchTheRecords();
    }

    private void fetchTheRecords(){
        showProgress();
        ApiManager.getInstance(RecordPriceDropActivity.this).getRecordsList(new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                dismissProgress();
                if(recordMap != null && recordMap.size() > 0){
                    recordMap.clear();
                }
                if(brandList != null && brandList.size() > 0){
                    brandList.clear();
                }
                if(mRecordList != null && mRecordList.size() > 0){
                    mRecordList.clear();
                    mRecordList = (ArrayList<Record>)data;
                    if(mRecordList != null && mRecordList.size() > 0){
                        for(int i=0; i<mRecordList.size(); i++) {
                            Log.i("Record ID", mRecordList.get(i).getBrandName());
                            if(recordMap.containsKey(mRecordList.get(i).getBrandName())){
                                recordMap.put(mRecordList.get(i).getBrandName(), recordMap.get(mRecordList.get(i).getBrandName())+1);
                            }
                            else{
                                recordMap.put(mRecordList.get(i).getBrandName(), 1);
                                brandList.add(mRecordList.get(i).getBrandName());
                            }
                        }
                    }
                }else{
                    mRecordList = (ArrayList<Record>)data;
                    if(mRecordList != null && mRecordList.size() > 0){
                        for(int i=0; i<mRecordList.size(); i++) {
                            Log.i("Record ID", mRecordList.get(i).getBrandName());
                            if(recordMap.containsKey(mRecordList.get(i).getBrandName())){
                                recordMap.put(mRecordList.get(i).getBrandName(), recordMap.get(mRecordList.get(i).getBrandName())+1);
                            }
                            else{
                                recordMap.put(mRecordList.get(i).getBrandName(), 1);
                                brandList.add(mRecordList.get(i).getBrandName());
                            }
                        }
                    }
                }

               /* mRecordList = new ArrayList<>();
                ArrayList<Record> records = (ArrayList<Record>)data;
                for(int i=0; i<records.size(); i++) {
                    if (mRecordList != null && !mRecordList.contains(records.get(i))){
                        mRecordList.add(records.get(i));
                    }
                }*/

                if(mRecordList != null && mRecordList.isEmpty()){
                    mNoDataView.setVisibility(View.VISIBLE);
                    myPriceDropList.setVisibility(View.GONE);
                } else {
                    mNoDataView.setVisibility(View.GONE);
                    myPriceDropList.setVisibility(View.VISIBLE);
                    /*mAdapter.addItems(mRecordList);
                    mAdapter.notifyDataSetChanged();*/
                }

                setAdapter();
            }

            @Override
            public void onFailure(String response) {

            }
        });
    }

    private void setAdapter() {
        priceDropApapter = new RecordPriceDropApapter(recordMap, RecordPriceDropActivity.this, RecordPriceDropActivity.this, mRecordList, brandList);
        myPriceDropList.setLayoutManager(new LinearLayoutManager(this));
        myPriceDropList.setAdapter(priceDropApapter);
        priceDropApapter.notifyDataSetChanged();
    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    @Override
    public void onBackPressed() {
      /*  Intent intent = new Intent(RecordPriceDropActivity.this, MyRecordsActivity.class);
        startActivity(intent);*/
        super.onBackPressed();
        return;
    }
}
