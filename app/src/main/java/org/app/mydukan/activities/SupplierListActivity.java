package org.app.mydukan.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.adapters.SupplierAdapter;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Supplier;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.SimpleDividerItemDecoration;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by arpithadudi on 7/27/16.
 */
public class SupplierListActivity extends BaseActivity implements SupplierAdapter.supplierAdapterListener {

    private static final String INTRO_CARD = "fab_intro";
    private static final String INTRO_SWITCH = "switch_intro";
    private static final String INTRO_RESET = "reset_intro";
    private static final String INTRO_REPEAT = "repeat_intro";
    private static final String INTRO_CHANGE_POSITION = "change_position_intro";
    private static final String INTRO_SEQUENCE = "sequence_intro";
    private boolean isRevealEnabled = true;


    private MyDukan mApp;
    private FloatingActionButton addsupplierbtn;
    private FloatingActionButton addsupplierbyinvitebtn;

    //Ui reference
    private RecyclerView mSupplierRecyclerView;
    private TextView mSupplierEmptyView;
    private LinearLayout subscribeAleartLayout;

    //Variables
    private SupplierAdapter mSupplierAdapter;
    private ArrayList<Supplier> mSupplierList = new ArrayList<>();

    View layout_promotional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mApp = (MyDukan) getApplicationContext();
            addsupplierbtn = (FloatingActionButton) findViewById(R.id.add_supplier_button);
            addsupplierbyinvitebtn = (FloatingActionButton) findViewById(R.id.whatsAppBtn);
            setupActionBar();

            layout_promotional = findViewById(R.id.viewFlipperWithBtn);
            layout_promotional.setVisibility(View.GONE);

            subscribeAleartLayout = (LinearLayout) findViewById(R.id.layout_subscribe);
            subscribeAleartLayout.setVisibility(View.GONE);

            addsupplierbtn.setImageResource(R.drawable.ic_action_arrowleft);
            addsupplierbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });


            /**
             * Using showcase view to show help content
             */

        /*new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Add Supplier")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Click here to add supplier, if you have INVITE CODE of supplier")
                .maskColor(Color.parseColor("#dc000060"))
                .target(addsupplierbyinvitebtn)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("1011") //UNIQUE ID
                .show();*/

     /*   SpotlightSequence.getInstance(this, null)
                .addSpotlight(addsupplierbyinvitebtn, "Add Supplier", "Click here to add supplier, if you have INVITE CODE of supplier", INTRO_CARD)
                .startSequence();*/


        /*ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200);
        // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "1011_id");
        config.setDismissTextColor(Color.parseColor("#64DD17"));
        config.setMaskColor(Color.parseColor("#dc4b4b4b"));
        sequence.setConfig(config);
        sequence.addSequenceItem(addsupplierbyinvitebtn, "Click here to add supplier, if you have INVITE CODE of supplier.", "NEXT");
        sequence.start();*/


            addsupplierbyinvitebtn.setImageResource(R.drawable.fab_add);
            addsupplierbyinvitebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final AlertDialog.Builder invitealert = new AlertDialog.Builder(SupplierListActivity.this);
                        final EditText edittext = new EditText(SupplierListActivity.this);
                        edittext.setHint(getString(R.string.hint_invitecode));
                        invitealert.setTitle(getString(R.string.invitecode_title));

                        invitealert.setView(edittext);

                        invitealert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //What ever you want to do with the value
                                String invitecode = edittext.getText().toString().trim();
                                //addProductToClaimList(product,imei_str);
                                showProgress();
                                ApiManager.getInstance(SupplierListActivity.this).getSupplierlistbyinvitecode(mApp.getFirebaseAuth().getCurrentUser().getUid(), invitecode, new ApiResult() {
                                    @Override
                                    public void onSuccess(Object data) {
                                        dismissProgress();
                                        showErrorToast(SupplierListActivity.this, getString(R.string.supplier_invite_success));
                                        finish();
                                    }

                                    @Override
                                    public void onFailure(String response) {
                                        dismissProgress();
                                    }
                                });

                            }
                        });

                        invitealert.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // what ever you want to do with No option.
                            }
                        });

                        invitealert.show();
                    } catch (Exception e) {
                        new SendEmail().sendEmail("2Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
                        Crashlytics.log(0, "Exception - SupplierListActivity - addsupplierbyinvitebtn - OnClick : ", e.toString());
                    } catch (VirtualMachineError ex) {
                        StringWriter errors = new StringWriter();
                        ex.printStackTrace(new PrintWriter(errors));
                        new SendEmail().sendEmail("2" +this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
                        Crashlytics.log(0, "2 - SupplierListActivity - addsupplierbyinvitebtn - OnClick : ", ex.toString());
                    }
                }
            });
            setupSupplierCard();
        }catch (Exception e){
            new SendEmail().sendEmail("1Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail("1"+this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void showProgress() {
        super.showProgress();
    }

    @Override
    public void dismissProgress() {
        super.dismissProgress();
    }

    private void setupActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(getString(R.string.add_supplier));
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

    private void setupSupplierCard() {
        View supplierView = findViewById(R.id.supplierlayout);
        mSupplierAdapter = new SupplierAdapter(SupplierListActivity.this, AppContants.ALL_SUPPLIER, this);

        //setup the recyclerview
        mSupplierRecyclerView = (RecyclerView) supplierView.findViewById(R.id.listview);
        mSupplierEmptyView = (TextView) supplierView.findViewById(R.id.nodata_view);
        mSupplierRecyclerView.setLayoutManager(new LinearLayoutManager(SupplierListActivity.this));
        mSupplierRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                SupplierListActivity.this.getApplicationContext(), true));
        mSupplierRecyclerView.setAdapter(mSupplierAdapter);
        fetchSupplierdata();
    }

    /**
     * This function clears the data.
     */
    private void clearTheData() {
        if (!mSupplierList.isEmpty()) {
            mSupplierList.clear();
            mSupplierAdapter.clearsupplier();
            mSupplierEmptyView.setVisibility(View.GONE);
            mSupplierRecyclerView.removeAllViews();
            mSupplierAdapter.notifyDataSetChanged();
        }
    }

    private void fetchSupplierdata() {
        try {
            clearTheData();
            showProgress();
            ApiManager.getInstance(SupplierListActivity.this).getSupplierlist(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    mSupplierList = (ArrayList<Supplier>) data;

                    if (mSupplierList.isEmpty()) {
                        mSupplierEmptyView.setVisibility(View.VISIBLE);
                        mSupplierEmptyView.setText(getString(R.string.add_supplier_empty_text));
                        dismissProgress();
                        return;
                    } else {
                        mSupplierEmptyView.setVisibility(View.GONE);
                    }

                    if (mSupplierAdapter != null) {
                        mSupplierAdapter.clearsupplier();
                    }

                    mSupplierAdapter.addsupplier(mSupplierList);
                    mSupplierAdapter.notifyDataSetChanged();
                    dismissProgress();
                }

                @Override
                public void onFailure(String response) {
                    mSupplierEmptyView.setVisibility(View.VISIBLE);
                    if (response.equals(getString(R.string.status_failed)))
                        dismissProgress();
                }
            });
        }catch (Exception e){
            dismissProgress();
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - fetchSupplierdata : ",e.toString());
            Crashlytics.log(0,"Exception - SupplierListActivity - fetchSupplierdata : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - fetchSupplierdata : ",ex.toString());
            Crashlytics.log(0,"1 - SupplierListActivity - fetchSupplierdata : ",ex.toString());
        }
    }

    public void addthissupplier(String supplierid) {
        try{
            showProgress();
            ApiManager.getInstance(SupplierListActivity.this).addSupplier(mApp.getFirebaseAuth().getCurrentUser().getUid(), supplierid, new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    Log.i(mApp.LOGTAG, "Success");
                    dismissProgress();
                    finish();
                }

                @Override
                public void onFailure(String response) {
                    showErrorToast(SupplierListActivity.this, response);
                    dismissProgress();
                }
            });
        } catch (Exception e){
            dismissProgress();
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - addthissupplier : ",e.toString());
            Crashlytics.log(0,"Exception - SupplierListActivity - addthissupplier : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - addthissupplier : ",ex.toString());
            Crashlytics.log(0,"1 - SupplierListActivity - addthissupplier : ",ex.toString());
        }
    }

    @Override
    public void OnSupplierAddClick(int position) {
        addthissupplier(mSupplierList.get(position).getId());
    }

    @Override
    public void OnSupplierOpenClick(int position) {

    }
}
