package org.app.mydukan.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.emailSending.SendEmail;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BroadcastActivity extends AppCompatActivity {


    private MyDukan mApp;
    TextView emptyVideoList;
    ImageView img_BackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setContentView(R.layout.activity_broadcast);

            //Only add the toolbar if we are on Honeycomb and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
            }

            mApp = (MyDukan) getApplicationContext();
            emptyVideoList= (TextView) findViewById(R.id.textNODATA);
            img_BackBtn= (ImageView) findViewById(R.id.back_button);

            //Check for any issues
            final YouTubeInitializationResult result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(this);

            if (result != YouTubeInitializationResult.SUCCESS) {
                //If there are any issues we can show an error dialog.
                result.getErrorDialog(this, 0).show();
            }else{
                emptyVideoList.setVisibility(View.VISIBLE);
            }
            img_BackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
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
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

}
