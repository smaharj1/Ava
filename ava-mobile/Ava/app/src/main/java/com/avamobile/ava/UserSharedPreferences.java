package com.avamobile.ava;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class will hold the userID of the logged in user so that
 * auto log in feature can be enabled.
 */

public class UserSharedPreferences {
    static final String PREF_USER_ID= "userid";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserID(Context ctx, String userID)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_USER_ID, userID);
        editor.commit();
    }

    public static String getUserID(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_USER_ID, "");
    }

    /**
     * Wipes the data when the user wants to log out.
     * @param ctx
     */
    public static void clearUserName(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear(); //clear all stored data
        editor.commit();
    }
}
