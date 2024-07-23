package com.ardiewp.simak;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {
    String dateTime;
    String TAG = "Utility";

    public Boolean isConnected(Context context) {
        Boolean status = false;
//        boolean haveConnectedWifi = false;
//        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();

        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
//                    haveConnectedWifi = true;
                    status = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
//                    haveConnectedMobile = true;
                    status = true;
        }
//        return haveConnectedWifi || haveConnectedMobile;
        return status;
    }

    public String getDuration(String dateTime){

//        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = null;
//        try {
//            date = (Date)formatter.parse(dateTime);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        System.out.println("try date: " +date);
//
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        String formatedDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" +
//                cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE) + ":"
//                + cal.get(Calendar.SECOND);
//        System.out.println("formatedDate : " + formatedDate);

        String datePost = dateTime;
        String dateCurrent = getCurrentDateTime();
        String dtime = "";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(datePost);
            d2 = format.parse(dateCurrent);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

//            System.out.print(diffDays + " days, ");
//            System.out.print(diffHours + " hours, ");
//            System.out.print(diffMinutes + " minutes, ");
//            System.out.print(diffSeconds + " seconds.");

            if (diffDays > 1) {
                dtime = dateTime;
            } else if (diffDays > 0){
                dtime = String.valueOf(diffDays) + " hari lalu";
            } else if (diffHours > 0){
                dtime = String.valueOf(diffHours) + " jam lalu";
            } else if (diffMinutes > 0){
                dtime = String.valueOf(diffMinutes) + " menit lalu";
            } else if (diffSeconds > 0){
                dtime = String.valueOf(diffSeconds) + " detik lalu";
            } else if (diffDays == 0) {
                dtime = "1 detik lalu";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("Resulted date: " + dtime);
        return dtime;
    }

    public String getCurrentDateTime(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dateTime = dateFormat.format(new Date());
        return dateTime;
    }

    public String getLastActiveTime(long timeStamp){
        String prefix = "";
        String lastActiveDate = "";
        String lastDate = "";
        Date dLastDate = null;

        TimeZone timezone = TimeZone.getDefault();
        DateFormat objFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        objFormatter.setTimeZone(timezone);

        Calendar objCalendar =
                Calendar.getInstance(timezone);
        objCalendar.setTimeInMillis(timeStamp);
        String result = objFormatter.format(objCalendar.getTime());
//        Log.d(TAG, "result: " + result);
        objCalendar.clear();

        String datePost = result;
        String dateCurrent = getCurrentDateTime();
        String dtime = "";

//        System.out.println("datePost dan dateCurrent: " + datePost + ", " + dateCurrent);

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(datePost);
            d2 = format.parse(dateCurrent);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

//            System.out.print(diffDays + " days, ");

            DateFormat formatter = new SimpleDateFormat ("DD MMM");

            String firstDate = datePost.substring(0, 10);
            String secondDate = dateCurrent.substring(0, 10);
//            System.out.println("first & second: " + firstDate + ", " + secondDate);

            if (!firstDate.equals(secondDate)) {
                SimpleDateFormat lastDateDf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                dLastDate = lastDateDf.parse(result);
                SimpleDateFormat dLastDateDf = new SimpleDateFormat("dd MMM, HH:mm");
                lastActiveDate = dLastDateDf.format(dLastDate);

            } else if (diffDays < 1) {
                String lastActive = result.substring(11,16);
                prefix = "hari ini, ";
                lastActiveDate = prefix + lastActive;
//                Log.d(TAG, "lastActiveTime: " + lastActive);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.toString());
        }

        return lastActiveDate;
    }

    public String getPostTime(long timeStamp){
        String prefix = "";
        String lastActiveDate = "";
        String lastDate = "";
        Date dLastDate = null;

        TimeZone timezone = TimeZone.getDefault();
        DateFormat objFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        objFormatter.setTimeZone(timezone);

        Calendar objCalendar =
                Calendar.getInstance(timezone);
        objCalendar.setTimeInMillis(timeStamp);
        String result = objFormatter.format(objCalendar.getTime());
//        Log.d(TAG, "result: " + result);
        objCalendar.clear();

        String datePost = result;
        String dateCurrent = getCurrentDateTime();
        String dtime = "";

//        System.out.println("datePost dan dateCurrent: " + datePost + ", " + dateCurrent);

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(datePost);
            d2 = format.parse(dateCurrent);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

//            System.out.print(diffDays + " days, ");

            DateFormat formatter = new SimpleDateFormat ("DD MMM");

            String firstDate = datePost.substring(0, 10);
            String secondDate = dateCurrent.substring(0, 10);
//            System.out.println("first & second: " + firstDate + ", " + secondDate);

            if (!firstDate.equals(secondDate)) {
                SimpleDateFormat lastDateDf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                dLastDate = lastDateDf.parse(result);
                SimpleDateFormat dLastDateDf = new SimpleDateFormat("dd MMM yy, HH:mm");
                lastActiveDate = dLastDateDf.format(dLastDate);
//                Log.d(TAG, "lastActiveTime: " + lastActiveDate);
            }
            else if (diffDays < 1) {
                if (diffHours > 0){
                    lastActiveDate = String.valueOf(diffHours) + " jam lalu";
                } else if (diffMinutes > 0){
                    lastActiveDate = String.valueOf(diffMinutes) + " menit lalu";
                } else if (diffSeconds > 0){
                    lastActiveDate = String.valueOf(diffSeconds) + " detik lalu";
                } else if (diffDays == 0) {
                    lastActiveDate = "1 detik lalu";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "error: " + e.toString());
        }

        return lastActiveDate;
    }

    public String formatDate(String datetime){
        Locale id = new Locale("en", "EN");
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(
                pattern, id);
        Date myDate = null;
        try {
            myDate = sdf.parse(datetime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

//        SimpleDateFormat tanggaldf = new SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault());
        SimpleDateFormat tanggaldf = new SimpleDateFormat("EEEE, dd MMM yyyy");
        String finalDate = tanggaldf.format(myDate);

//        System.out.println(finalDate);
        return finalDate;
    }

    public String getDate(long timeStamp){
        TimeZone timezone = TimeZone.getDefault();
//        DateFormat objFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        DateFormat objFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        objFormatter.setTimeZone(timezone);

        Calendar objCalendar =
                Calendar.getInstance(timezone);
        objCalendar.setTimeInMillis(timeStamp);
        String result = objFormatter.format(objCalendar.getTime());
        objCalendar.clear();
//        System.out.println("util result: " + result);

        return result;
    }
}
