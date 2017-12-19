package org.app.mydukan.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.AppStateContants;
import org.app.mydukan.data.User;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.AppPreference;
import org.app.mydukan.utils.NotificationUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import static org.app.mydukan.activities.IntentForwardingActivity.DEEP_LINK;

public class LaunchActivity extends BaseActivity {

    SharedPreferences sharedPreferences;
    HashMap<String, Object> notificationInfo;
    private String mUid;
    private User user;
    private int mAppState;
    private MyDukan mApp;

    //====================================delete cache data starts=========================
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        try {
            mApp = (MyDukan) getApplicationContext();

            /*try{
               int a = 7/0;
            }catch (Throwable ex){
                StringWriter errors = new StringWriter();
                ex.printStackTrace(new PrintWriter(errors));
                new SendEmail().sendEmail("Testing ",ex.toString());
            }*/

            try{
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(firebaseUser.getEmail() != null) {
                    if(firebaseUser.getEmail().equalsIgnoreCase("testisynergy@gmail.com")
                            || firebaseUser.getEmail().equalsIgnoreCase("charkhaamit@gmail.com")
                            || firebaseUser.getEmail().equalsIgnoreCase("sonamt@5fworld.com")
                            || firebaseUser.getEmail().equalsIgnoreCase("pzende@gmail.com")
                            || firebaseUser.getEmail().equalsIgnoreCase("viraj.isynergy@gmail.com")) {
                        Toast.makeText(getApplicationContext(), "19th Dec Release ", Toast.LENGTH_LONG).show();
                    }
                }
            }catch (Throwable ex){
                new SendEmail().sendEmail("Testing Toast ",ex.toString());
            }

        /*
        1.clear the cache for old user.
        2.this code is only execute only once . i.e at the Application is launching at first time only.

     sharedPreferences = getSharedPreferences("ShaPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        boolean  firstTime=sharedPreferences.getBoolean("first", true);
        if(firstTime) {
            editor.putBoolean("first",false);
            //For commit the changes, Use either editor.commit(); or  editor.apply();.
            //editor.commit();
            editor.apply();
            // now the calling the method to clear the cache data or app data
            deleteCache(this);
        }

TEsting

   */


            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                getNotificationData(bundle);
            }

            if (mApp.getFirebaseAuth().getCurrentUser() != null) {
                mUid = mApp.getFirebaseAuth().getCurrentUser().getUid();
                mAppState = mApp.getPreference().getAppState(LaunchActivity.this);

            }

            //Display the splash screen for 2 secs.
            new SplashScreenTimer(1000, 2000).start();
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

    //====================================delete cache data ends=========================
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Log.d(MyDukan.LOGTAG, "onNewIntent bundle:" + intent);
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                mAppState = 9;
                getNotificationData(bundle);
            }
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onNewIntent : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onNewIntent : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onNewIntent : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onNewIntent : ",ex.toString());
        }
    }

    private void openActivity() {

        try {
//        String mUserid=mApp.getFirebaseAuth().getCurrentUser().getUid();
            DatabaseReference referenceUser = FirebaseDatabase.getInstance().getReference().child("users/" + mUid);
            referenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (dataSnapshot != null) {
                            user = dataSnapshot.getValue(User.class);
                            if (user != null) {
                                Answers.getInstance().logCustom(new CustomEvent("Launcher_OpenPage")
                                        .putCustomAttribute("UserId", mUid));

                                mApp.checkAndSendToken();

                                String mob_vrify = user.getVrified_mobilenum();
                                String location_verfy = user.getVerified_location();
//                           String cmpnyInfo_vrify= user.getVerified_CompanyInfo();


                                if (user.getVrified_mobilenum() == null && mob_vrify.equals("false")) {

                                    Answers.getInstance().logCustom(new CustomEvent("Launcher_OpenPage")
                                            .putCustomAttribute("UserId_NewSignUpActivity", mUid));

                                    Intent intent = new Intent(LaunchActivity.this, NewSignUpActivity.class);
                                    intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                                    startActivity(intent);
                                    finish();
                                } else if (user.getVerified_location() == null && location_verfy.equals("false")) {

                                    Answers.getInstance().logCustom(new CustomEvent("Launcher_OpenPage")
                                            .putCustomAttribute("UserId_UsersLocationAddress", mUid));

                                    Intent intent = new Intent(LaunchActivity.this, UsersLocationAddress.class);
                                    intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                                    startActivity(intent);
                                    finish();
                                }


                      /* else if(cmpnyInfo_vrify.equals("false")){
                           Intent intent = new Intent(LoginActivity.this, UserProfile.class);
                           intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                           startActivity(intent);
                           finish();

                       }*/
                                else {

                                    Boolean checkIsSubscribe = new AppPreference().getSubscribing(getApplicationContext());
                                    if (!checkIsSubscribe) {
                                        Answers.getInstance().logCustom(new CustomEvent("Launcher_Page")
                                                .putCustomAttribute("UserId", mUid));
                                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                                        intent.putExtra(AppContants.NOTIFICATION, notificationInfo);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        new AppPreference().setSubscribing(getApplicationContext(), false);
                                    }

                                }
                            } else {

                                Answers.getInstance().logCustom(new CustomEvent("Launcher_Page")
                                        .putCustomAttribute("UserId_Null", mUid));

                                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - openActivity : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - openActivity : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - openActivity : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - openActivity : ",ex.toString());
        }

    }

    private void getNotificationData(Bundle payload) {
        Log.d(MyDukan.LOGTAG, "getNotificationData called:");

        if (payload.size() > 0) {
            NotificationUtils utils = new NotificationUtils(LaunchActivity.this);
            //mUid = mApp.getFirebaseAuth().getCurrentUser().getUid();
            String message = null;
            String command = payload.getString(utils.COMMAND);


            //String supplierId = null;

            if (!mApp.getUtils().isStringEmpty(command)) {
                //If command is refresh.

                if (command.equalsIgnoreCase(utils.RECORD)) {
                    //Else show the notifications and add it to the database.
                    message = payload.getString(utils.MESSAGE);

                    if (!mApp.getUtils().isStringEmpty(message)) {
                        if (mApp.getFirebaseAuth().getCurrentUser() != null) {
                            utils.saveNotification(mApp.getFirebaseAuth().getCurrentUser().getUid(), message);

                            String notificationTitle = String.valueOf(payload.get("title"));
                            String notificationMessage = String.valueOf(payload.get("message"));
                            String notificationImage = String.valueOf(payload.get("image"));


                            notificationInfo = new HashMap<>();
                            notificationInfo.put("notificationTitle", notificationTitle);
                            notificationInfo.put("notificationMessage", notificationMessage);
                            notificationInfo.put("notificationImage", notificationImage);
                            notificationInfo.put("mUid", mUid);
                          /*  Intent intent = new Intent(LaunchActivity.this, NotificationDescriptionActivity.class);
                            intent.putExtra(AppContants.NOTIFICATION,notificationInfo);
                            startActivity(intent);
                            finish();
                            */
                        }
                    }
                }
            }
        }
    }

    private class SplashScreenTimer extends CountDownTimer {
        public SplashScreenTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {

            if (mApp.getUtils().isStringEmpty(mUid)) {
                startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
                finish();
            } else {
                AppStateContants stateContants = new AppStateContants();
                if (mAppState == stateContants.HOME_SCREEN) {
                    // startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                    openActivity();

                    Answers.getInstance().logCustom(new CustomEvent("Launcher_Page")
                            .putCustomAttribute("UserId", mUid));
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
//                  intent.putExtra(AppContants.NOTIFICATION,notificationInfo);
                    intent.putExtra(DEEP_LINK, getIntent().getStringExtra(DEEP_LINK));
                    startActivity(intent);
                    finish();

                } else if (mAppState == stateContants.PROFILE_SCREEN) {
                    Intent intent = new Intent(LaunchActivity.this, UserProfile.class);
                    intent.putExtra(AppContants.VIEW_TYPE, AppContants.SIGN_UP);
                    startActivity(intent);
                    finish();
                } else if (mAppState == stateContants.Notification_SCREEN) {
                    Intent intent = new Intent(LaunchActivity.this, NotificationDescriptionActivity.class);
                    intent.putExtra(AppContants.NOTIFICATION, notificationInfo);
                    startActivity(intent);
                    finish();
                } else {
                    // startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                    openActivity();

                    Answers.getInstance().logCustom(new CustomEvent("Launcher_Page")
                            .putCustomAttribute("UserId", mUid));
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
//                    intent.putExtra(AppContants.NOTIFICATION,notificationInfo);
                    intent.putExtra(DEEP_LINK, getIntent().getStringExtra(DEEP_LINK));
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}

