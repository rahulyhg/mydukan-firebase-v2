package org.app.mydukan.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                finish();
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
                mRecordList = (ArrayList<Record>)data;
                if(mRecordList.size() > 0){
                    for(int i=0; i<mRecordList.size(); i++) {
                        Log.i("Record ID", mRecordList.get(i).getRecordId());
                        Log.i("Record Id Catergory", "KU2hE65nFcTF0eYa0u_");
                        if(recordMap.containsKey(mRecordList.get(i).getProductCategoryID())){
                            recordMap.put(mRecordList.get(i).getProductCategoryID(), recordMap.get(mRecordList.get(i).getProductCategoryID())+1);
                        }
                        else{
                            recordMap.put(mRecordList.get(i).getProductCategoryID(), 1);
                            brandList.add(mRecordList.get(i).getProductCategoryID());
                        }
                    }
                }
                if(mRecordList.isEmpty()){
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
        priceDropApapter = new RecordPriceDropApapter(recordMap, RecordPriceDropActivity.this, mRecordList, brandList);
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
        finish();
    }
}
