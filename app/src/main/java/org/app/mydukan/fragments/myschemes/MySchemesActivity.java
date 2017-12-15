package org.app.mydukan.fragments.myschemes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import org.app.mydukan.R;
import org.app.mydukan.activities.BaseActivity;
import org.app.mydukan.fragments.myschemes.calculator.CalculatorForm;
import org.app.mydukan.fragments.myschemes.fragmetns.DashBoardFragment;

public class MySchemesActivity extends BaseActivity {

    // Fragment Variablse
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private RadioButton mMySchemesTab, mSchemesTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_schemes_fragment);

        // Set My Schemes Tab
        mMySchemesTab = (RadioButton) findViewById(R.id.tab_myschemes);
        mSchemesTab = (RadioButton) findViewById(R.id.tab_schemes);

        mSchemesTab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gotoSchemesFragment();
                }
            }
        });
        setupActionBar();
        setDashBoardFragment();
    }

    private void gotoSchemesFragment() {/*
        Intent schemesInent = new Intent(MySchemesActivity.this,SchemeListActivity.class);
        startActivity(schemesInent);*/
        finish();
    }

    private void setDashBoardFragment() {
        DashBoardFragment fragment = DashBoardFragment.newInstance();
        addFragment(fragment, false,DashBoardFragment.class.getSimpleName());
    }


    public void addFragment(Fragment frag, boolean hasToAdd,String tag) {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, frag);
        if (hasToAdd)
            fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();


    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.myschemes_title));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(fragmentManager != null && fragmentManager.getBackStackEntryCount() >0){
                    popCurrentFragment();
                }else {
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void popCurrentFragment() {
        fragmentManager.popBackStack();
        fragmentManager.executePendingTransactions();
    }

    public void notifyListDataChanged() {
        fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment != null && fragment instanceof  CalculatorForm) {
            ((CalculatorForm) fragment).notifyData();
        }
    }
}
