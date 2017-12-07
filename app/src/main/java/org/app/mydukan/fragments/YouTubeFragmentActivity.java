package org.app.mydukan.fragments;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import org.app.mydukan.R;
import org.app.mydukan.data.Videos;


/***********************************************************************************

 ***********************************************************************************/

/**
 * A sample Activity hosting a YouTubeFragment. You could place the Fragment anywhere in the layout.
 */
public class YouTubeFragmentActivity extends ActionBarActivity {

    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    public static final String KEY_VIDEO = "KEY_VIDEO";
    Videos mVideo=new Videos();
    TextView videoTitle; //video_title
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_youtube_fragment);

        final Bundle bundle = getIntent().getExtras();
        videoTitle= (TextView) findViewById(R.id.video_title);

        if (bundle != null && bundle.containsKey(KEY_VIDEO_ID)) {
            final String videoId = bundle.getString(KEY_VIDEO_ID);
            final YouTubeFragment fragment = (YouTubeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_youtube);
            fragment.setVideoId(videoId);
        }
        if (bundle != null && bundle.containsKey(KEY_VIDEO)) {
              mVideo = (Videos) bundle.get(KEY_VIDEO);
            videoTitle.setText(mVideo.getVideoINFO());

        }
    }
}
