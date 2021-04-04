package com.mirae.shimpyo.helper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static int getDayOfYear() { return Calendar.getInstance().get(Calendar.DAY_OF_YEAR); }
    public static String byteArrayToString(byte[] ba) { return Base64.encodeToString(ba, Base64.DEFAULT); }
    public static byte[] stringToByteArray(String str) throws IllegalArgumentException { return Base64.decode(str, Base64.DEFAULT); }

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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static String convertDayOfYearToyyyyMMdd(int dayOfYear) {
        LocalDate currentDate = LocalDate.now();
        int y = currentDate.getYear();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        return Year.of(y).atDay(dayOfYear).format(formatters);
    }
}
