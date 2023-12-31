package com.example.fridgebuddy.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converters {
    // date converters
    @TypeConverter
    public static Date longToDate(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String dateToMonthDay(Date date) {
        if (date == null) {
            return null;
        }

        // Define the desired format, e.g., "MMM dd" (abbreviated month name and day)
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy", Locale.US);

        // Format the date to a string
        return sdf.format(date);
    }

    @TypeConverter
    @SuppressLint("SimpleDateFormat")
    public static Date stringToDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the exception according to your application's needs
            return null; // Return null or throw a custom exception to indicate parsing failure
        }
    }

    // bitmap converters
    @TypeConverter
    public static byte[] fromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        return stream.toByteArray();
    }

    @TypeConverter
    public static Bitmap toBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public static int dpToPixels(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
