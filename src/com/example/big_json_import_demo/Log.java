package com.example.big_json_import_demo;

public class Log {
    public static final String TAG="hhh";

    public static void d(String text){
        android.util.Log.d(TAG,text);
    }

    public static void e(String text){
        android.util.Log.e(TAG,text);
    }
}
