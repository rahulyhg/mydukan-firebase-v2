package org.app.mydukan.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.app.mydukan.R;

public class MyRecordsActivity extends AppCompatActivity {

    RelativeLayout btn_records_my_pricedrops, btn_records_calculator, btn_records_earnings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_records);

        setupActionBar();

        btn_records_my_pricedrops = (RelativeLayout) findViewById(R.id.btn_records_my_pricedrops);
        btn_records_calculator = (RelativeLayout) findViewById(R.id.btn_records_calculator);
        btn_records_earnings = (RelativeLayout) findViewById(R.id.btn_records_earnings);


        btn_records_my_pricedrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_records_calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_records_earnings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.record_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
