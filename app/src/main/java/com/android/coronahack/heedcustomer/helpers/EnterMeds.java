package com.android.coronahack.heedcustomer.helpers;

public class EnterMeds {

    private String tabName, tabQuantity;

    public EnterMeds(String tabName, String tabQuantity) {
        this.tabName = tabName;
        this.tabQuantity = tabQuantity;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTabQuantity() {
        return tabQuantity;
    }

    public void setTabQuantity(String tabQuantity) {
        this.tabQuantity = tabQuantity;
    }
}
