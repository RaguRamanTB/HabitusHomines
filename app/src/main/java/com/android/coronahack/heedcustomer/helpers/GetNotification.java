package com.android.coronahack.heedcustomer.helpers;

public class GetNotification {

    public String type, shopName, status, timeSlot;

    public GetNotification(String type, String shopName, String status, String timeSlot) {
        this.type = type;
        this.shopName = shopName;
        this.status = status;
        this.timeSlot = timeSlot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
