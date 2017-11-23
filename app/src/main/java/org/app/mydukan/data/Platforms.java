package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by MyDukan_SHIVAYOGI on 23-11-2017.
 */
@SuppressWarnings("serial")
public class Platforms implements Serializable {

    private String amazon = "";
    private String flipkart = "";
    private String gadget360 = "";
    private String lockthedeal = "";
    private String paytm = "";
    private String shopclues = "";
    private String snapdeal = "";

    public String getAmazon() {
        return amazon;
    }

    public void setAmazon(String amazon) {
        this.amazon = amazon;
    }

    public String getFlipkart() {
        return flipkart;
    }

    public void setFlipkart(String flipkart) {
        this.flipkart = flipkart;
    }

    public String getGadget360() {
        return gadget360;
    }

    public void setGadget360(String gadget360) {
        this.gadget360 = gadget360;
    }

    public String getLockthedeal() {
        return lockthedeal;
    }

    public void setLockthedeal(String lockthedeal) {
        this.lockthedeal = lockthedeal;
    }

    public String getPaytm() {
        return paytm;
    }

    public void setPaytm(String paytm) {
        this.paytm = paytm;
    }

    public String getShopclues() {
        return shopclues;
    }

    public void setShopclues(String shopclues) {
        this.shopclues = shopclues;
    }

    public String getSnapdeal() {
        return snapdeal;
    }

    public void setSnapdeal(String snapdeal) {
        this.snapdeal = snapdeal;
    }


}
