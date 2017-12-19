package org.app.mydukan.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.User;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MyAccountActivity extends AppCompatActivity {

    private MyDukan mApp;
    private String myDukhan_UserId;
    private User userdetails;
    TextView profileDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                //  userdetails =  getIntent().getExtras().getParcelable("UserDetails"); //The key argument here must match that used in the other activity
                myDukhan_UserId = extras.getString("MyDhukhan_UserId");
                userdetails = (User) getIntent().getExtras().getSerializable("UserDetails");
                if (userdetails != null) {
                    //Initialise the Activity View.
                    initAccountView(myDukhan_UserId, userdetails);

                }
            } else {
                getUserProfile();
            }
            //init and declare the ViewParts

            profileDetails = (TextView) findViewById(R.id.tv_profileDetails);
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onCreate : ",ex.toString());
        }

    }

    private void initAccountView(String myDukhan_userId, User userdetails) {
        String userName=userdetails.getUserinfo().getName();
        String userEmailID=userdetails.getUserinfo().getEmailid();
        String userNunmer=userdetails.getUserinfo().getNumber();
        String userAddress=userdetails.getUserinfo().getAddressinfo().getStreet()+"\n"+userdetails.getUserinfo().getAddressinfo().getCity()+userdetails.getUserinfo().getAddressinfo().getCountry()+userdetails.getUserinfo().getAddressinfo().getPincode();
        String mDetailHTML="";
        profileDetails.setText(userName);

    }

    //get the User Details From the FIreBase.
    private void getUserProfile() {
        try {
            if (mApp.getFirebaseAuth().getCurrentUser() == null) {
                return;
            }
            ApiManager.getInstance(this).getUserProfile(mApp.getFirebaseAuth().getCurrentUser().getUid(), new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    if (data != null) {
                        userdetails = (User) data;
                        myDukhan_UserId = mApp.getFirebaseAuth().getCurrentUser().getUid();
                        if (userdetails != null) {
                            //  initView(this, myDukhan_UserId, userdetails);//Initialise the Activity View.
                        }

                        return;
                    }
                }

                @Override
                public void onFailure(String response) {
                    //Do when there is no data present in firebase

                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - getUserProfile : ",e.toString());
            Crashlytics.log(0,"Exception - MyAccountActivity - getUserProfile : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - getUserProfile : ",ex.toString());
            Crashlytics.log(0,"1 - MyAccountActivity - getUserProfile : ",ex.toString());
        }
    }


}
