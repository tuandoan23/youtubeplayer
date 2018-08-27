package com.gamota.youtubeplayer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final String API_KEY = "AIzaSyBsrViFNAWWmWs0tVJ5z221PfWlsNZa8OQ";
    public static final String CHANNEL_ID = "UCx6iGPCoUxb-7jAInKiql5g";

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
    public static String dateToString(Date date){
        SimpleDateFormat formatToDate = new SimpleDateFormat("MMM dd, yyyy");
        String dateTime = formatToDate.format(date);
        return dateTime;
    }

    public static String dateToTime(Date date){
        SimpleDateFormat formatToDate = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String dateTime = formatToDate.format(date);
        return dateTime;
    }
}
