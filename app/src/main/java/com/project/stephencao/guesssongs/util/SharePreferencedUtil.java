package com.project.stephencao.guesssongs.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferencedUtil {
    public static boolean getBoolean(Context context, String key, boolean defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences("guessSongInfo",Context.MODE_PRIVATE);
        boolean flag = sharedPreferences.getBoolean(key, defaultValue);
        return flag;
    }
    public static void putBoolean(Context context, String key, boolean value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("guessSongInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key,value);
        edit.commit();
    }
    public static int getInteger(Context context, String key, int defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences("guessSongInfo",Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt(key, defaultValue);
        return count;
    }
    public static void putInteger(Context context, String key, int value){
        SharedPreferences sharedPreferences = context.getSharedPreferences("guessSongInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key,value);
        edit.commit();
    }
}
