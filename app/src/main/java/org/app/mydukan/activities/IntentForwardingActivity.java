package org.app.mydukan.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import org.app.mydukan.emailSending.SendEmail;

import java.io.PrintWriter;
import java.io.StringWriter;

import static android.content.ContentValues.TAG;

public class IntentForwardingActivity extends Activity {

    public static final String DEEP_LINK = "deep_link";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(AppInvite.API)
                    .build();
            AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, true)
                    .setResultCallback(
                            new ResultCallback<AppInviteInvitationResult>() {
                                @Override
                                public void onResult(@NonNull AppInviteInvitationResult result) {
                                    if (result.getStatus().isSuccess()) {
                                        Log.d("Deep", "Status True");
                                        //not working, needs to update library version and change to firebaseInvite
                                    /*
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    Intent intent2=new Intent(IntentForwardingActivity.this,LaunchActivity.class);
                                    intent2.putExtra(DEEP_LINK,deepLink.toString());
                                    Log.e("Deep Link", deepLink);
                                    startActivity(intent2);
                                    IntentForwardingActivity.this.finish();
*/

                                    } else {
                                        Log.d(TAG, "getInvitation: no deep link found.");
                                    }
                                }
                            });

            //temporary fix

            String deepLink = getIntent().getDataString();
            Intent intent2 = new Intent(IntentForwardingActivity.this, LaunchActivity.class);
            intent2.putExtra(DEEP_LINK, deepLink);
            Log.e("Deep Link", deepLink);
            startActivity(intent2);
            IntentForwardingActivity.this.finish();
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

    private void handleIntent(Intent intent) {

    }
}
