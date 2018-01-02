package org.app.mydukan.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.ChattUser;
import org.app.mydukan.data.Feed;
import org.app.mydukan.data.User;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzben.NULL;

public class YourAccountActivity extends AppCompatActivity {
    TextView UserName, CompanyName, Address, Phone, Gst, profession, email;
    User profileDetails = new User();
    ChattUser chattUser;
    String profileUID = "";
    FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
    private MyDukan mApp;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    Boolean mMyProfile=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_account);
        mApp = (MyDukan) getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar=(ProgressBar)findViewById(R.id.progress_bar_ya);
        UserName = (TextView) findViewById(R.id.name_card1);
        CompanyName = (TextView) findViewById(R.id.name_card2);
        Address = (TextView) findViewById(R.id.address_card3);
        Phone = (TextView) findViewById(R.id.phoneNumber_card1);
        Gst = (TextView) findViewById(R.id.gst_card2);
        email = (TextView) findViewById(R.id.email_card1);
        profession = (TextView) findViewById(R.id.profession_card2);
        linearLayout=(LinearLayout)findViewById(R.id.main_layout);
        Bundle mybundle = getIntent().getExtras();
        if (mybundle != null) {
            if (mybundle.containsKey(AppContants.CHAT_USER_PROFILE)) {
                chattUser = (ChattUser) mybundle.getSerializable(AppContants.CHAT_USER_PROFILE);
                if (chattUser != null) {
                    profileUID = chattUser.getuId();
                    getProfileData(profileUID);

                } else {
                    Toast.makeText(YourAccountActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }
            }
            if ((mybundle.containsKey(AppContants.IS_MY_PROFILE))){
                mMyProfile=(Boolean)mybundle.getSerializable(AppContants.IS_MY_PROFILE);
                if(mMyProfile==false){
                    Phone.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    Gst.setVisibility(View.GONE);
                }
            }
        }
        //getProfileData(mApp.getFirebaseAuth().getCurrentUser().getUid());
    }

    private void getProfileData(String profileUID) {
        ApiManager.getInstance(this).getUserProfile(profileUID, new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                profileDetails = (User) data;
                Log.e("YourAccount", "got data");
                if (profileDetails == null) {
                    return;
                }
                profileDetails.getId();
                if (profileDetails != null) {
                    UserName.setText(Html.fromHtml("Name : " + "<b>" + profileDetails.getUserinfo().getName() + "</b>"));
                    if (profileDetails.getUserinfo().getNumber() != NULL) {
                        Phone.setText(Html.fromHtml("Phone no. : " + "<b>" + profileDetails.getUserinfo().getNumber() + "</b>"));
                    }
                    if (profileDetails.getUserinfo().getEmailid() != NULL) {
                        email.setText(Html.fromHtml("Email : " + "<b>" + profileDetails.getUserinfo().getEmailid() + "</b>"));
                    }
                    if (profileDetails.getUserinfo().getAddressinfo().getStreet()!=NULL) {
                        if (mMyProfile==true) {
                            String addr = profileDetails.getUserinfo().getAddressinfo().getStreet() + "<br>" + profileDetails.getUserinfo().getAddressinfo().getCity() +
                                    ", " + profileDetails.getUserinfo().getAddressinfo().getState() + "<br>" + profileDetails.getUserinfo().getAddressinfo().getPincode() + "<br>" +
                                    profileDetails.getUserinfo().getAddressinfo().getCountry();
                            Address.setText(Html.fromHtml("<b>" + addr + "</b>"));
                        }else {
                            String addr = profileDetails.getUserinfo().getAddressinfo().getCity() +
                                    ", " + profileDetails.getUserinfo().getAddressinfo().getState() + "<br>" + profileDetails.getUserinfo().getAddressinfo().getPincode() + "<br>";
                            Address.setText(Html.fromHtml("<b>" + addr + "</b>"));
                        }
                    }
                    if (profileDetails.getCompanyinfo().getName() != NULL) {
                        CompanyName.setText(Html.fromHtml("Name : " + "<b>" + profileDetails.getCompanyinfo().getName() + "</b>"));
                    }
                    if (profileDetails.getOtherinfo().getRole() != NULL) {
                        profession.setText(Html.fromHtml("Profession : " + "<b>" + profileDetails.getOtherinfo().getRole() + "</b>"));
                    }
                    if(profileDetails.getCompanyinfo().getVatno()!=NULL){
                        Gst.setText(Html.fromHtml("GST : " + "<b>" + profileDetails.getCompanyinfo().getVatno() + "</b>"));
                    }
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(YourAccountActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(String response) {
                Toast.makeText(YourAccountActivity.this, "Unable to Get the User Profile", Toast.LENGTH_SHORT).show();
            }

        });
    }


}
