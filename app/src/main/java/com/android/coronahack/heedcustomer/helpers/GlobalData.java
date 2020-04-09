package com.android.coronahack.heedcustomer.helpers;

public class GlobalData {
    public static String name;
    public static String age;
    public static String address;
    public static String gender;
    public static String isVolunteer;

    private static final String PACKAGE_NAME = "com.android.coronahack.heedcustomer.activities";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    public static final int SUCCESS_RESULT = 1;
    static final int FAILURE_RESULT = 0;

    public static double latitude;
    public static double longitude;

    public static String medicalShopName = "";
    public static String medicalShopAddress = "";

    public static String groceryShopName = "";
    public static String groceryShopAddress = "";

}
