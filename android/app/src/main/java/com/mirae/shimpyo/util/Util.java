package com.mirae.shimpyo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class Util {
    public static int getDayOfYear() { return Calendar.getInstance().get(Calendar.DAY_OF_YEAR); }
    public static String byteArrayToString(byte[] ba) { return Base64.encodeToString(ba, Base64.DEFAULT); }
    public static byte[] stringToByteArray(String str) { return Base64.decode(str, Base64.DEFAULT); }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static Bitmap byteArrayToBitmap(byte[] byteArr) {
        return BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);
    }
}
