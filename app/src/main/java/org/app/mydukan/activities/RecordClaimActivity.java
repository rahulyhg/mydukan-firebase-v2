package org.app.mydukan.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.Record;
import org.app.mydukan.data.RecordInfo;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.util.ArrayList;

public class RecordClaimActivity extends BaseActivity {

    ArrayList<Record> mRecordList;
    ArrayList<Record> claimsRecord;
    TextView mNoDataView;
    TextView total;
    RecyclerView mRecyclerView;
    int totalClaim = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_claim);

        //Initializing Widgets
        mRecordList = new ArrayList<>();
        mNoDataView = (TextView) findViewById(R.id.noData);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        total = (TextView) findViewById(R.id.total);

        fetchTheRecords();
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
                        ArrayList<RecordInfo> recordInfos = mRecordList.get(i).getRecordList();
                        for(int j=0; j<recordInfos.size(); j++){
                            RecordInfo info = new RecordInfo();
                            if(recordInfos.get(j).getStatus().equalsIgnoreCase("claim")){
                                totalClaim += Integer.parseInt(recordInfos.get(j).getPrice());
                            }
                        }
                    }
                    total.setText("Rs."+totalClaim);
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

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }
}
