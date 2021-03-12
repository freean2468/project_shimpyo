package com.mirae.shimpyo.util;

import android.util.Base64;

import java.util.Calendar;

public class Util {
    public static int getDayOfYear() { return Calendar.getInstance().get(Calendar.DAY_OF_YEAR); }
    public static String byteArrayToString(byte[] ba) { return Base64.encodeToString(ba, Base64.DEFAULT); }
    public static byte[] stringToByteArray(String str) { return Base64.decode(str, Base64.DEFAULT); }
}
