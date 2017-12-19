package org.app.mydukan.content;

import android.app.Activity;

import com.crashlytics.android.Crashlytics;

import org.app.mydukan.data.Videos;
import org.app.mydukan.emailSending.SendEmail;
import org.app.mydukan.server.ApiManager;
import org.app.mydukan.server.ApiResult;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/***********************************************************************************

 ***********************************************************************************/


/**
 * A Helper class for providing mock data to the app.
 * In a real world scenario you would either hard code the video ID's in the strings file or
 * retrieve them from a web service.
 */
public class YouTubeContent extends Activity {

    /**
     * An array of YouTube videos
     */
    public static List<YouTubeVideo> ITEMS = new ArrayList<>();

    /**
     * A map of YouTube videos, by ID.
     */
    public static Map<String, YouTubeVideo> ITEM_MAP = new HashMap<>();


    {
/*      addItem(new YouTubeVideo("CT3aWbdadOk", "Open in the YouTube App"));
        addItem(new YouTubeVideo("CT3aWbdadOk", "Open in the YouTube App in fullscreen"));
        addItem(new YouTubeVideo("ZPflrotUwgI", "Open in the Standalone player in fullscreen"));
        addItem(new YouTubeVideo("tttG6SdnCd4", "Open in the Standalone player in \"Light Box\" mode"));
        addItem(new YouTubeVideo("ZPflrotUwgI", "Open in the YouTubeFragment"));
        addItem(new YouTubeVideo("TTh_qYMzSZk", "Hosting the YouTubeFragment in an Activity"));
        addItem(new YouTubeVideo("ZPflrotUwgI", "Open in the YouTubePlayerView"));
        addItem(new YouTubeVideo("CT3aWbdadOk", "Custom \"Light Box\" player with fullscreen handling"));
        addItem(new YouTubeVideo("CT3aWbdadOk", "Custom player controls"));*/

        fetchVideos();
    }


    private void fetchVideos() {
        try {
            ApiManager.getInstance(this).getVideosList(new ApiResult() {
                @Override
                public void onSuccess(Object data) {
                    if (data != null) {
                        ArrayList<Videos> videosList = (ArrayList<Videos>) data;

                        for (Videos vSnapshot : videosList) {
                            addItem(new YouTubeVideo(vSnapshot.getVideoID(), vSnapshot.getVideoINFO()));
                        }
                    }
                }

                @Override
                public void onFailure(String response) {


                }
            });
        }catch (Exception e){
            new SendEmail().sendEmail("Exception - " + this.getClass().getSimpleName() + " - fetchVideos : ",e.toString());
            Crashlytics.log(0,"Exception - YouTubeContent - fetchVideos : ",e.toString());
        }catch (VirtualMachineError ex){
            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            new SendEmail().sendEmail(this.getClass().getSimpleName() + " - fetchVideos : ",ex.toString());
            Crashlytics.log(0,"1 - YouTubeContent - fetchVideos : ",ex.toString());
        }

    }

    private static void addItem(final YouTubeVideo item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A POJO representing a YouTube video
     */
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
    }
}