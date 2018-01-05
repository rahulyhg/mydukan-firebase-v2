package org.app.mydukan.youtube;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.R;
import org.app.mydukan.application.MyDukan;
import org.app.mydukan.data.Videos;
import org.app.mydukan.emailSending.SendEmail;

import java.io.PrintWriter;
import java.io.StringWriter;


/***********************************************************************************

 ***********************************************************************************/

/**
 * A sample Activity hosting a YouTubeFragment. You could place the Fragment anywhere in the layout.
 */
public class YouTubeFragmentActivity extends ActionBarActivity {


    private MyDukan mApp;
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    public static final String KEY_VIDEO = "KEY_VIDEO";
    Videos mVideo=new Videos();
    TextView videoTitle; //video_title
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (MyDukan) getApplicationContext();
        try {
            setContentView(R.layout.activity_youtube_fragment);

            final Bundle bundle = getIntent().getExtras();
            videoTitle = (TextView) findViewById(R.id.video_title);

            if (bundle != null && bundle.containsKey(KEY_VIDEO_ID)) {
                final String videoId = bundle.getString(KEY_VIDEO_ID);
                final YouTubeFragment fragment = (YouTubeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_youtube);
                fragment.setVideoId(videoId);
            }
            if (bundle != null && bundle.containsKey(KEY_VIDEO)) {
                mVideo = (Videos) bundle.get(KEY_VIDEO);
                videoTitle.setText(mApp.getUtils().toCamelCase(mVideo.getVideoINFO()));

            }
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
}
