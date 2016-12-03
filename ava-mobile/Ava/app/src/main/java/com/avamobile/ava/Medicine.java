package com.avamobile.ava;

import java.util.Calendar;

/**
 * Created by Sujil on 12/3/2016.
 */

public class Medicine {
    private String medicineName;
    private final long ONE_DAY = 86400;

    /* Holds the time in seconds from midnight*/
    private long targetTime;

    Calendar rightNow = Calendar.getInstance();


    public Medicine(String name, long time){
        medicineName = name;
        targetTime = time;
    }

    public void setTargetTime(long currentTime) {
        targetTime = currentTime;
    }

    public long getTimeDifference(long givenTime) {
        if (targetTime - givenTime < 0) {
            //System.out.println("Substracting " + givenTime + " - " + targetTime+ " = " + (givenTime-targetTime));
            return targetTime+ONE_DAY-givenTime;
        }


        return targetTime - givenTime;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void incrementAfterMedicine(long interval){
        targetTime += interval;
    }

    public void setCurrentTime(){
        long offset = rightNow.get(Calendar.ZONE_OFFSET) +
                rightNow.get(Calendar.DST_OFFSET);
        long sinceMid = (rightNow.getTimeInMillis() + offset) %
                (24 * 60 * 60 * 1000);
        targetTime = sinceMid/1000;
    }

//    public static void main(String[] args){
//        Medicine med = new Medicine("DI", 900);
//        long morning =600;
//
//        System.out.println("The time is "+ med.getTimeDifference(morning)/60);
//
//    }
}
