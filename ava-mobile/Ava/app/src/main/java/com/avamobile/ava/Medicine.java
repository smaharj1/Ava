package com.avamobile.ava;

import java.util.Calendar;

/**
 * Created by Sujil on 12/3/2016.
 */

public class Medicine {
    private String medicineName;
    private final long ONE_DAY = 86400;

    /* Holds the time in seconds from midnight*/
    private int targetTime;

    Calendar rightNow = Calendar.getInstance();


    public Medicine(String name, int time){
        medicineName = name;
        targetTime = time;
    }

    public String getTime() {
        int hours = targetTime /3600;
        int minutes = (targetTime - (hours*3600))/60;
        int seconds = targetTime - (hours*3600) - (minutes*60);

        StringBuilder str = new StringBuilder();
        if (hours > 12 ) hours -= 12;
        String temp = hours < 10 ? "0"+hours : Integer.toString(hours);
        str.append(temp+":");
        temp = minutes < 10 ? "0"+ minutes : Integer.toString(minutes);
        str.append(temp);

        return str.toString();
    }

    public void setTargetTime(int currentTime) {
        targetTime = currentTime;
    }

    public long getTimeDifference(long givenTime) {
        if (targetTime - givenTime < 0) {
            //System.out.println("Substracting " + givenTime + " - " + targetTime+ " = " + (givenTime-targetTime));
            return targetTime+ONE_DAY-givenTime;
        }


        return targetTime - givenTime;
    }

    public int getTargetTime() {
        return targetTime;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void incrementAfterMedicine(int interval){
        targetTime += interval;
    }

    public void setCurrentTime(){
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);
        long sinceMid = (rightNow.getTimeInMillis() + offset) %
                (24 * 60 * 60 * 1000);
        targetTime = (int) sinceMid/1000;
    }

//    public static void main(String[] args){
//        Medicine med = new Medicine("DI", 900);
//        long morning =600;
//
//        System.out.println("The time is "+ med.getTimeDifference(morning)/60);
//
//    }
}
