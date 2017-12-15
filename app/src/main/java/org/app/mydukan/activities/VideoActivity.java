package org.app.mydukan.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

import org.app.mydukan.R;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.youtube.Config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import static org.app.mydukan.application.MyDukan.LOGTAG;

public class VideoActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    String path = "";
    String remoteUrl;
    ImageButton closeButton;
    AdView mAdView;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_video);
        try {
            Intent intent = getIntent();
            String action = intent.getAction();
            Uri data = intent.getData();
            youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            youTubeView.initialize(Config.DEVELOPER_KEY, this);

            closeButton = (ImageButton) findViewById(R.id.closeButton);

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.containsKey(AppContants.REMOTECONFIG_VID)) {
                    remoteUrl = bundle.getString(AppContants.REMOTECONFIG_VID);
                    if (remoteUrl == null) {
                        try {
                            path = new URL(data.toString()).getPath();
                            path = path.substring(1);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        Log.d("Path", path);
                    } else {
                        path = remoteUrl;
                        // path="D8Ert5yjMV4";
                    }
                }
            }

            //initialization of adview in this activity//

            //  MobileAds.initialize(this,"ca-app-pub-1640690939729824/3548733794");
            //initialize ads for the app
     /*     MobileAds.initialize(VideoActivity.this ,"ca-app-pub-1640690939729824/3548733794");
        mAdView = (AdView) findViewById(R.mCatId.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


       try {
            path = new URL(data.toString()).getPath();
            path = path.substring(1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d("Path", path);
        */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(AppInvite.API)
                    .build();
            boolean autoLaunchDeepLink = true;
            AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                    .setResultCallback(
                            new ResultCallback<AppInviteInvitationResult>() {
                                @Override
                                public void onResult(@NonNull AppInviteInvitationResult result) {
                                    if (result.getStatus().isSuccess()) {
                                        Log.d("Deep", "Status True");
                                        // Extract deep link from Intent

                                        Intent intent = result.getInvitationIntent();
                                        String deepLink = AppInviteReferral.getDeepLink(intent);

                                        path = deepLink;
                                        Log.d("Deep Link", deepLink);
                                        // Handle the deep link. For example, open the linked
                                        // content, or apply promotional credit to the user's
                                        // account.

                                        // ...
                                    } else {
                                        Log.d(LOGTAG, "getInvitation: no deep link found.");
                                    }
                                }
                            });


            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    //startActivity(new Intent(VidPlayer.this, MainActivity.class));
                    //finish();
                }
            });

            // Initializing v/home/rk/Desktop/Photos.zipideo player with developer key
            youTubeView.initialize(Config.DEVELOPER_KEY, this);
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
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
        super.onBackPressed();
        return;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.DEVELOPER_KEY, this);
        }
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        /** if needed add listeners to YouTubePlayer instance **/

        /** add listeners to YouTubePlayer instance **/
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);

        if (!wasRestored) {

            // loadVideo() will auto play video
            // Use cueVideo() method, if you don't want to play it automatically
            if (path == "") {
                player.cueVideo(Config.YOUTUBE_VIDEO_CODE);
            } else {
                player.cueVideo(path);
            }
            // Hiding player controls
            /*
            YouTubePlayer.PlayerStyle.DEFAULT – The default style, showing all interactive player controls.
            YouTubePlayer.PlayerStyle.MINIMAL – The minimal style displays only a time bar and play/pause controls.
            YouTubePlayer.PlayerStyle.CHROMELESS – A style that shows no interactive player controls. When you use CHROMELESS,
            you need to write your own controls for play, pause or seek operation.
             */
            // player.setPlayerStyle( YouTubePlayer.PlayerStyle.CHROMELESS);
            //  player.setPlayerStyle( YouTubePlayer.PlayerStyle.MINIMAL);
              player.setPlayerStyle( YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
            onBackPressed();
        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
            onBackPressed();
        }

        @Override
        public void onVideoStarted() {
        }
    };


}
