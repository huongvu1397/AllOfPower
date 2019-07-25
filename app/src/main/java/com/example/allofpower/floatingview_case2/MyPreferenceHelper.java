package com.example.allofpower.floatingview_case2;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferenceHelper {
    private static final String NEWS_ADS_PREFERENCES = "bubble";

    public static final String PREF_WIDTH = "PREF_WIDTH";
    public static final String PREF_HEIGHT = "PREF_HEIGHT";
    public static final String REAL_WIDTH = "REAL_WIDTH";
    public static final String REAL_HEIGHT = "REAL_HEIGHT";
    public static final String PREF_X = "PREF_X";
    public static final String PREF_Y = "PREF_Y";
    public static final String PREF_GRAVITY = "PREF_GRAVITY";

    public static int getInt(String key, Context context) {
        return getIntValue(key, context);
    }
    public static long getLong(String key, Context context) {
        return getLongValue(key, context);
    }

    public static void setInt(String key, int value, Context context) {
        putIntValue(key, value, context);
    }
    public static void setLong(String key, long value, Context context) {
        putLongValue(key, value, context);
    }

    public static float getFloat(String key, Context context) {
        return getFloatValue(key, context);
    }

    public static void setFloat(String key, float value, Context context) {
        putFloatValue(key, value, context);
    }


    public static String getString(String key, Context context) {
        return getStringValue(key, context);
    }

    public static void setString(Context context, String key, String value) {
        putStringValue(key, value, context);
    }


    public static void putStringValue(String key, String s, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
    }

    public static void putBoolean(String key, boolean value, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putFloatValue(String key, Float s, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, s);
        editor.commit();
    }

    public static void putLongValue(String key, long l, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, l);
        editor.commit();
    }

    public static Long getLongValue(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        return pref.getLong(key, 0);
    }

    public static void putIntValue(String key, int value, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putBooleanValue(String key, Boolean s, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, s);
        editor.commit();
    }

    public static boolean getBooleanValue(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        return pref.getBoolean(key, false);
    }

    public static String getStringValue(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        return pref.getString(key, null);
    }

    public static Float getFloatValue(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        return pref.getFloat(key, (float) 0);
    }

    public static int getIntValue(String key, Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                NEWS_ADS_PREFERENCES, 0);
        return pref.getInt(key, 0);
    }

    public static int getDisplayWidth(Context context){
        return getInt(MyPreferenceHelper.PREF_WIDTH,context);
    }

    public static int getDisplayHeight(Context context){
        return getInt(MyPreferenceHelper.PREF_HEIGHT,context);
    }

    public static int getRealWidth(Context context){
        return getInt(MyPreferenceHelper.REAL_WIDTH,context);
    }

    public static int getRealHeight(Context context){
        return getInt(MyPreferenceHelper.REAL_HEIGHT,context);
    }

    public static int getLastX(Context context){
        return getInt(MyPreferenceHelper.PREF_X,context);
    }

    public static int getLastY(Context context){
        return getInt(MyPreferenceHelper.PREF_Y,context);
    }

    public static void setLastX(Context context,int value){
        putIntValue(MyPreferenceHelper.PREF_X,value,context);
    }

    public static void setLastY(Context context, int value){
        putIntValue(MyPreferenceHelper.PREF_Y,value,context); }

    public static void setPrefWidth(Context context, int value){
        putIntValue(MyPreferenceHelper.PREF_WIDTH,value,context);
    }

    public static void setPrefHeight(Context context, int value){
        putIntValue(MyPreferenceHelper.PREF_HEIGHT,value,context);
    }

    public static void setGravity(Context context,int value){
        putIntValue(MyPreferenceHelper.PREF_GRAVITY,value,context);

    }
}
