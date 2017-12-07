package org.app.mydukan.data;

import java.io.Serializable;

/**
 * Created by MyDukan_SHIVAYOGI on 01-12-2017.
 */
@SuppressWarnings("serial")
public class PlatForm_Info extends Object implements Serializable {

    private String info = "";
    private String price= "";
    private String url = "";


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


}
