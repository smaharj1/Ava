package com.avamobile.ava;

import java.util.Calendar;

/**
 * This class holds the information of a single medicine.
 */

public class Medicine {
    // It holds the medicine name.
    private String medicineName;
    private final long ONE_DAY = 86400;

    /* Holds the time in seconds from midnight*/
    private int targetTime;

    // Holds the current times.
    Calendar rightNow = Calendar.getInstance();

    /**
     * Constructor.
     * @param name It holds the name of the medicine.
     * @param time It holds the target time.
     */
    public Medicine(String name, int time){
        medicineName = name;
        targetTime = time;
    }

    /**
     * Returns current time in the form of formatted string.
     * @return
     */
    public String getTime() {
        int hours = targetTime /3600;
        int minutes = (targetTime - (hours*3600))/60;
        int seconds = targetTime - (hours*3600) - (minutes*60);

        // Makes a string in a proper format
        StringBuilder str = new StringBuilder();
        if (hours > 12 ) hours -= 12;
        String temp = hours < 10 ? "0"+hours : Integer.toString(hours);
        str.append(temp+":");
        temp = minutes < 10 ? "0"+ minutes : Integer.toString(minutes);
        str.append(temp);

        return str.toString();
    }

    /**
     * Sets the target time.
     * @param currentTime It holds the target time.
     */
    public void setTargetTime(int currentTime) {
        targetTime = currentTime;
    }

    /**
     * Gets the time difference from the target time to the provided time.
     * @param givenTime It holds the given time.
     * @return Gives the time difference.
     */
    public long getTimeDifference(long givenTime) {
        if (targetTime - givenTime < 0) {
            //System.out.println("Substracting " + givenTime + " - " + targetTime+ " = " + (givenTime-targetTime));
            return targetTime+ONE_DAY-givenTime;
        }


        return targetTime - givenTime;
    }

    /**
     * Returns the target time.
     * @return Returns the target time.
     */
    public int getTargetTime() {
        return targetTime;
    }

    /**
     * Returns the name of the medicine.
     * @return Returns the name of the medicine.
     */
    public String getMedicineName() {
        return medicineName;
    }

    /**
     * Increases the target time after medicine.
     * @param interval It holds the total interval.
     */
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

}
