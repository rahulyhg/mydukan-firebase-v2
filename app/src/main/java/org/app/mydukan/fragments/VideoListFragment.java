package org.app.mydukan.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.app.mydukan.R;
import org.app.mydukan.content.YouTubeContent;
import org.app.mydukan.data.Videos;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***********************************************************************************

 ***********************************************************************************/

public class VideoListFragment extends ListFragment {

    /**
     * Empty constructor
     */
    /**
     * An array of YouTube videos
     */

    ArrayList<Videos> videosList;
/*    *//**
     * A map of YouTube videos, by ID.
     *//*
     *   public List<String> ITEMS = new ArrayList<String>();
    public   Map<String,YouTubeVideo> ITEM_MAP = new HashMap<>();*/

    public VideoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       fetchVideos();


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        final Context context = getActivity();
        final String DEVELOPER_KEY = getString(R.string.DEVELOPER_KEY);
        final String videoId =  videosList.get(position).getVideoID();
        //Opens in Custom Activity
       final Intent fragIntent = new Intent(context, YouTubeFragmentActivity.class);
        fragIntent.putExtra(YouTubeFragmentActivity.KEY_VIDEO_ID, videoId);
        fragIntent.putExtra(YouTubeFragmentActivity.KEY_VIDEO, videosList.get(position));
        startActivity(fragIntent);

      /*  switch (position) {
            case 0:
                //Check whether we can actually open YT
                if (YouTubeIntents.canResolvePlayVideoIntent(context)) {
                    //Opens the video in the YouTube app
                    startActivity(YouTubeIntents.createPlayVideoIntent(context, videoId));
                }
                break;
            case 1:
                if (YouTubeIntents.canResolvePlayVideoIntentWithOptions(context)) {
                    //Opens in the YouTube app in fullscreen and returns to this app once the video finishes
                    startActivity(YouTubeIntents.createPlayVideoIntentWithOptions(context, videoId, true, true));
                }
                break;
            case 2:
                //Issue #3 - Need to resolve StandalonePlayer as well
                if (YouTubeIntents.canResolvePlayVideoIntent(context)) {
                    //Opens in the StandAlonePlayer, defaults to fullscreen
                    startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                            DEVELOPER_KEY, videoId));
                }
                break;
            case 3:
                //Issue #3 - Need to resolve StandalonePlayer as well
                if (YouTubeIntents.canResolvePlayVideoIntentWithOptions(context)) {
                    //Opens in the StandAlonePlayer but in "Light box" mode
                    startActivity(YouTubeStandalonePlayer.createVideoIntent(getActivity(),
                            DEVELOPER_KEY, videoId, 0, true, true));
                }
                break;
            case 4:
                //Opens in the YouTubeSupportFragment
                final YouTubeFragment fragment = YouTubeFragment.newInstance(videoId);
                getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
                break;
            case 5:
                //Opens in Custom Activity
                final Intent fragIntent = new Intent(context, YouTubeFragmentActivity.class);
                fragIntent.putExtra(YouTubeFragmentActivity.KEY_VIDEO_ID, videoId);
                startActivity(fragIntent);
                break;
            case 6:
                //Opens in the YouTubePlayerView
                final Intent actIntent = new Intent(context, YouTubeActivity.class);
                actIntent.putExtra(YouTubeActivity.KEY_VIDEO_ID, videoId);
                startActivity(actIntent);
                break;
            case 7:
                //Opens in the the custom Lightbox activity
                final Intent lightboxIntent = new Intent(context, CustomLightboxActivity.class);
                lightboxIntent.putExtra(CustomLightboxActivity.KEY_VIDEO_ID,videoId);
                startActivity(lightboxIntent);
                break;
            case 8:
                //Custom player controls
                final Intent controlsIntent = new Intent(context, CustomYouTubeControlsActivity.class);
                controlsIntent.putExtra(CustomLightboxActivity.KEY_VIDEO_ID,videoId);
                startActivity(controlsIntent);
                break;


        }*/
    }
    private void fetchVideos() {
        ApiManager.getInstance(getActivity()).getVideosList(new ApiResult() {
            @Override
            public void onSuccess(Object data) {
                if(data!=null){
                   videosList = (ArrayList<Videos>) data;

                 /*   for (Videos vSnapshot : videosList ) {
                        addItem(new YouTubeVideo(vSnapshot.getVideoID(), vSnapshot.getVideoINFO()));
                    }*/
                    setListAdapter(new VideoListAdapter(getActivity(),videosList));
                }
            }

            @Override
            public void onFailure(String response) {


            }
        });

    }

 /*   private void addItem(YouTubeVideo youTubeVideo) {
        ITEMS.add(youTubeVideo.id);
        ITEM_MAP.put(youTubeVideo.id, youTubeVideo);
    }

    *//**
     * A POJO representing a YouTube video
     *//*
    public static class YouTubeVideo {
        public String id;
        public String title;

        public YouTubeVideo(String id, String content) {
            this.id = id;
            this.title = content;
        }
        @Override
        public String toString() {
            return title;
        }
    }*/

}
