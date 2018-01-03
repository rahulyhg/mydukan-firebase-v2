package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by MyDukan_SHIVAYOGI on 28-11-2017.
 */

@SuppressWarnings("serial")
public class Videos extends Object implements Serializable {

    String videoID;
    String videoINFO;

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getVideoINFO() {
        return videoINFO;
    }

    public void setVideoINFO(String videoINFO) {
        this.videoINFO = videoINFO;
    }


}
