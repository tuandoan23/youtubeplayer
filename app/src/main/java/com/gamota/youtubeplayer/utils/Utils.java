package com.gamota.youtubeplayer.utils;

import com.apkfuns.logutils.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static final String API_KEY = "AIzaSyBsrViFNAWWmWs0tVJ5z221PfWlsNZa8OQ";
    public static final String CHANNEL_ID = "UCx6iGPCoUxb-7jAInKiql5g";
    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1) );
    public static final List<String> timesString = Arrays.asList("year","month","day","hour","minute","second");

    public static String getTimeAgo(Date date) {
        long duration = (System.currentTimeMillis() - date.getTime());
        StringBuffer res = new StringBuffer();
        for(int i = 0; i < Utils.times.size(); i++) {
            Long current = Utils.times.get(i);
            long temp = duration/current;
            if(temp > 0) {
                res.append(temp).append(" ").append( Utils.timesString.get(i) ).append(temp != 1 ? "s" : "").append(" ago");
                break;
            }
        }
        if("".equals(res.toString()))
            return "Just now";
        else
            return res.toString();
    }

    public static Date RFC3339ToDate(String rfcTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        try {
            Date date = format.parse(rfcTime);
            return date;
        } catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }
//    public static String dateToString(Date date){
//        SimpleDateFormat formatToDate = new SimpleDateFormat("MMM dd, yyyy");
//        String dateTime = formatToDate.format(date);
//        return dateTime;
//    }

    public static String RFC3339ToDateString(String rfcTime){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            date = format.parse(rfcTime);
        } catch (ParseException e){
            e.printStackTrace();
        }
        SimpleDateFormat formatToDate = new SimpleDateFormat("MMM dd, yyyy");
        String dateTime = formatToDate.format(date);
        return dateTime;
    }
}
