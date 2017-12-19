package org.app.mydukan.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.digits.sdk.android.Digits;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.moe.pushlibrary.MoEHelper;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.AppPreference;
import org.app.mydukan.utils.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

public class MyDukan extends Application {
    public static final String LOGTAG = "MyDukan";

    private Context mContext;
    private Utils mUtils;
    private AppPreference mPreference;
    private AppContants mAppContants;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Realm.init(this);
        MoEHelper.getInstance(getApplicationContext()).autoIntegrate(this);
//        MultiDex.install(getBaseContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(AppContants.TWITTER_KEY, AppContants.TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(),new TwitterCore(authConfig), new Digits.Builder().build(), new Answers());
        getFirebaseAuth();
        checkAndSendToken();

    }

    public Context getApplicationContext(){
        return mContext;
    }

    public Utils getUtils() {
        if (mUtils == null) {
            mUtils = new Utils();
        }
        return mUtils;
    }

    public AppPreference getPreference() {
        if (mPreference == null) {
            mPreference = new AppPreference();
        }
        return mPreference;
    }

    public AppContants getContants() {
        if (mAppContants == null) {
            mAppContants = new AppContants();
        }
        return mAppContants;
    }

    public FirebaseAuth getFirebaseAuth() {
        try {
            if (mFirebaseAuth == null) {
                mFirebaseAuth = FirebaseAuth.getInstance();
            }
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - getFirebaseAuth : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - getFirebaseAuth : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - getFirebaseAuth : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - getFirebaseAuth : ",ex.toString());
        }
        return mFirebaseAuth;
    }

    public String getUserId() {
        String UserId = null;
        try {
            if (mFirebaseAuth == null) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                UserId = String.valueOf(mFirebaseAuth.getInstance().getCurrentUser());
            }
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - getUserId : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - getUserId : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - getUserId : ",ex.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - getUserId : ",ex.toString());
        }
        return UserId;
    }

    public void checkAndSendToken(){
        try {
            if (mFirebaseAuth.getCurrentUser() != null) {
                ApiManager.getInstance(getApplicationContext()).checkAndSubscribeForTopic();
                String token = FirebaseInstanceId.getInstance().getToken();
                if (token != null) {
                    Log.d(MyDukan.LOGTAG, "TOKEN_ID:" + token);
                    // Send the Instance ID token to your app server.
                    ApiManager.getInstance(this).sendRegistrationId(mFirebaseAuth.getCurrentUser().getUid(), token, new ApiResult() {
                        @Override
                        public void onSuccess(Object data) {

                        }

                        @Override
                        public void onFailure(String response) {

                        }
                    });
                } else {
                    Log.d(MyDukan.LOGTAG, "TOKEN_ID:" + token);
                    Answers.getInstance().logCustom(new CustomEvent("Launcher_Page")
                            .putCustomAttribute("TOKENID_NOT_UPDATED", mFirebaseAuth.getCurrentUser().getUid()));
                }

            }
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - checkAndSendToken : ",e.toString());
            Crashlytics.log(0,"Exception - MyDukan - checkAndSendToken : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - checkAndSendToken : ",ex.toString());
            Crashlytics.log(0,"1 - MyDukan - checkAndSendToken : ",ex.toString());
        }
    }







}
