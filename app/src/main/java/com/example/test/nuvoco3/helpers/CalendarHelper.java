package com.example.test.nuvoco3.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarHelper {
    /**
     * This class is a generally a collection of data that used to convert date to different formats
     * and to make it easier to understand
     */

    private static final String TAG = "Calendar Helper";

    /**
     * This function is used to convert simple yyyy-mm-dd format to JSON format kind of Date
     * and at the same time removes those GMT and other Stuffs
     */
    public static String convertSmallDateToJson(String start_dt) {
//        String pattern = "E, dd MMM yyyy HH:mm:ss 'GMT'";
        String pattern = "E, dd MMM yyyy";
        DateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
        Date date = null;
        try {
            date = formatter.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat(pattern);
        String finalString = newFormat.format(date);
        return finalString;


    }

    public static String convertJsonTimToStandardDateTime(String start_dt) {
//        String pattern = "E, dd MMM yyyy HH:mm:ss 'GMT'";
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'");
        Date date = null;
        try {
            date = formatter.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat(pattern);
        String finalString = newFormat.format(date);
        return finalString;


    }


    /**
     * This function just performs the opposite of above function
     * It converts the JSON formatted recieved date to simple format
     */

    public static String convertJsonDateToSmall(String start_dt) {
//        Log.i(TAG, "convertJsonDateToSmall: " + start_dt);
        String pattern = "E, dd MMM yyyy HH:mm:ss 'GMT'";
        DateFormat formatter = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = formatter.parse(start_dt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd");
        String finalString = newFormat.format(date);
//        Log.i("lol", "convertSmallDateToJson: " + finalString);
        return finalString;


    }


    /**
     * This function is used to Obtain current Date and Time
     */
    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * This function is used to obtain only today's date
     */

    public static String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * This function is used to obtain time
     */
    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * This function is used to convert obtained in the specified format into long
     */
    public static long getLongDate(String string) {
        String pattern = "E, dd MMM yyyy HH:mm:ss 'GMT'";
        long mDate = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date date = sdf.parse(string);

            mDate = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mDate;
    }

    public static String convertClickedDate(Date clickedDate) {
        String pattern = "E, dd MMM yyyy";
        SimpleDateFormat newFormat = new SimpleDateFormat(pattern);
        String finalString = newFormat.format(clickedDate);
        return finalString;


    }

    /**
     * This function just removes that extra portion of GMT+00 from the date
     */

    public static boolean compareSmallDate(String stringDate1, String stringDate2) {
        String pattern = "yyyy-MM-dd";
        DateFormat formatter = new SimpleDateFormat(pattern);
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = formatter.parse(stringDate1);
            date2 = formatter.parse(stringDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.after(date2);


    }

    public static boolean compareSmallDateEquality(String stringDate1, String stringDate2) {
        String pattern = "yyyy-MM-dd";
        DateFormat formatter = new SimpleDateFormat(pattern);
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = formatter.parse(stringDate1);
            date2 = formatter.parse(stringDate2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1.equals(date2);


    }





}
