package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by MyDukan_SHIVAYOGI on 28-11-2017.
 */

@SuppressWarnings("serial")
public class PriceDropHistory extends Object implements Serializable {


    String date;
    String dp;
    String drop;
    String mop;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }

    public String getMop() {
        return mop;
    }

    public void setMop(String mop) {
        this.mop = mop;
    }


}
