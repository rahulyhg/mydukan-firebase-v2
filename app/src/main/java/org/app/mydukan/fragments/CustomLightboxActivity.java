package org.app.mydukan.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.app.mydukan.R;
import org.app.mydukan.emailSending.SendEmail;

import java.io.PrintWriter;
import java.io.StringWriter;

/***********************************************************************************

 ***********************************************************************************/


public class CustomLightboxActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    //Keys
    public static final String KEY_VIDEO_ID = "KEY_VIDEO_ID";
    private static final String KEY_VIDEO_TIME = "KEY_VIDEO_TIME";

    private YouTubePlayer mPlayer;
    private boolean isFullscreen;

    private int millis;
    private String mVideoId;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            setContentView(R.layout.activity_youtube_lightbox);

            final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_youtube_activity);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            final YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);
            playerView.initialize(getString(R.string.DEVELOPER_KEY), this);

            if (bundle != null) {
                millis = bundle.getInt(KEY_VIDEO_TIME);
            }

            final Bundle extras = getIntent().getExtras();
            if (extras != null && extras.containsKey(KEY_VIDEO_ID)) {
                mVideoId = extras.getString(KEY_VIDEO_ID);
            } else {
                finish();
            }
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
            Crashlytics.log(0,"Exception - " + this.getClass().getSimpleName() + " - onCreate : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - onCreate : ",errors.toString());
            Crashlytics.log(0,this.getClass().getSimpleName() + " - onCreate : ",errors.toString());
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        mPlayer = youTubePlayer;
        youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
        youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
            @Override
            public void onFullscreen(boolean b) {
                isFullscreen = b;
            }
        });

        if (mVideoId != null && !wasRestored) {
            youTubePlayer.loadVideo(mVideoId);
        }

        if (wasRestored) {
            youTubePlayer.seekToMillis(millis);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Unable to load video", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPlayer != null) {
            outState.putInt(KEY_VIDEO_TIME, mPlayer.getCurrentTimeMillis());
        }
    }

    @Override
    public void onBackPressed() {
        //If the Player is fullscreen then the transition crashes on L when navigating back to the MainActivity
        boolean finish = true;
        try {
            if (mPlayer != null) {
                if (isFullscreen) {
                    finish = false;
                    mPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                        @Override
                        public void onFullscreen(boolean b) {
                            //Wait until we are out of fullscreen before finishing this activity
                            if (!b) {
                                finish();
                            }
                        }
                    });
                    mPlayer.setFullscreen(false);
                }
                mPlayer.pause();
            }
        } catch (final IllegalStateException e) {
            e.printStackTrace();
        }

        if (finish) {
            super.onBackPressed();
        }
    }

}
