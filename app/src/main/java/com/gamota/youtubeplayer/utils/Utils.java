package com.gamota.youtubeplayer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utils {
    public static final String API_KEY = "AIzaSyBsrViFNAWWmWs0tVJ5z221PfWlsNZa8OQ";
    public static final String CHANNEL_ID = "UCJrOtniJ0-NWz37R30urifQ";

    public static final Date currentDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+7")).getTime();
    public static Date RFC3339ToDate(String rfcTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date date = format.parse(rfcTime);
            return date;
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Date RFC3339ToDate1(String rfcTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            Date date = format.parse(rfcTime);
            return date;
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }
    public static String dateToString(Date date){
        SimpleDateFormat formatToDate = new SimpleDateFormat("MMM dd, yyyy");
        String dateTime = formatToDate.format(date);
        return dateTime;
    }
}
