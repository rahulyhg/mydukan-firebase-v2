package org.app.mydukan.fragments.myschemes.model;

/**
 * Created by rojesharunkumar on 20/10/17.
 */

public class Device {
    String model;
    String target;
    String quantity;
    String dp;
    String value;
    boolean type;
    String percent;
    String incentiveAmount;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getIncentiveAmount() {
        return incentiveAmount;
    }

    public void setIncentiveAmount(String incentiveAmount) {
        this.incentiveAmount = incentiveAmount;
    }

}
