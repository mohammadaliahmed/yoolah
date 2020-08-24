package com.appsinventiv.yoolah.Utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.appsinventiv.yoolah.Database.Word;
import com.appsinventiv.yoolah.Database.WordRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by AliAh on 14/05/2018.
 */

public class CommonUtils {


    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    public static String getNameFromUrl(String url) {
        String abc = url.substring(url.length() - 10, url.length() - 1);
        return abc;
    }

    public static String repeatString(String s, int count) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < count; i++) {
            r.append(s);
        }
        return r.toString();
    }

    public static String getFullAddress(Context context, Double lat, Double lon) {
        String address = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);

            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return address;
    }


    public static String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = ApplicationClass.getInstance().getApplicationContext().getContentResolver().query(contentUri, proj,
                null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    public static void showToast(final String msg) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @SuppressLint("WrongConstant")
            public void run() {
                Toast.makeText(ApplicationClass.getInstance().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getFormattedPrice(Object price) {
        DecimalFormat formatter = new DecimalFormat("##,##,###");
        String formattedPrice = formatter.format(price);
        return formattedPrice;
    }


    public static Uri getVideoPic(String videoUrl) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //give YourVideoUrl below
        retriever.setDataSource(videoUrl, new HashMap<String, String>());
// this gets frame at 2nd second
        Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String recordingLocalUrl = Long.toHexString(Double.doubleToLongBits(Math.random()));

            String path = MediaStore.Images.Media.insertImage(ApplicationClass.getInstance().getApplicationContext().getContentResolver(), image, recordingLocalUrl, null);
            return Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
            return Uri.parse("");
        }
    }


    public static void getTimeZone() {
        Calendar cal = Calendar.getInstance();
        long milliDiff = cal.get(Calendar.ZONE_OFFSET);
// Got local offset, now loop through available timezone id(s).
        String[] ids = TimeZone.getAvailableIDs();
        String name = null;
        for (String id : ids) {
            TimeZone tz = TimeZone.getTimeZone(id);
            if (tz.getRawOffset() == milliDiff) {
                // Found a match.
                name = id;
                break;
            }
        }
        Constants.TIMEZONE = name;
    }

    public static String getMimeType(File file, Context context) {
        String parts[] = file.toString().split("\\.");
        String extension = parts[parts.length - 1];
        String type = null;

        return "." + extension;
    }

//    public static String getFormattedTimeFromCompleteTime(long date) {
////        Calendar c = Calendar.getInstance();
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm aa", Locale.ENGLISH);
////        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
////        c.getTime();
////        return sdf.format(date);
//        Calendar mCalendar = Calendar.getInstance();
//        TimeZone mTimeZone = mCalendar.getTimeZone();
//        int mGMTOffset = mTimeZone.getRawOffset();
//        SimpleDateFormat df = new SimpleDateFormat("hh:ss aa MM/dd/yyyy");
//        df.setTimeZone(TimeZone.getTimeZone("GMT+" + TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)));
//        String result = df.format(date);
//        return result;
//    }

    public static String getFormattedDate(long smsTimeInMilis) {

        Calendar smsTime = Calendar.getInstance();

        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();


        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "dd MMM ";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM , HH:mm", smsTime).toString();
        }
    }

    public static String getDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);


        return DateFormat.format("dd MMM yyyy", smsTime).toString();

    }

    public static void insertWord(Word word, WordRepository mRepository) {
        mRepository.insert(word);

    }

    public static String getHour(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);


        return DateFormat.format("h", smsTime).toString();

    }

    public static String getAMPM(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);


        return DateFormat.format("aa", smsTime).toString();

    }

    public static String getMinute(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);


        return DateFormat.format("mm", smsTime).toString();

    }


    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static String getLocalTime(long smsTimeInMilis, String gmt) {
        Calendar smsTime = Calendar.getInstance(TimeZone.getTimeZone(gmt));
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm";
        final String dateTimeFormatString = "dd MMM ";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return "Yesterday ";
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMM , h:mm", smsTime).toString();
        }
    }

    public static String getFormattedDateOnly(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        return DateFormat.format("dd-MMM-yyyy", smsTime).toString();

    }


    public static String getFormattedTime(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        return DateFormat.format("h:mm ", smsTime).toString();

    }


    public static void shareUrl(Context context, String postType, String postId) {
        String append = "";
        if (postType.equalsIgnoreCase("post")) {
            append = "p";
        } else if (postType.equalsIgnoreCase("profile")) {
            append = "u";
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, AppConfig.BASE_URL + append + "/" + postId);
        context.startActivity(Intent.createChooser(shareIntent, "Share via.."));
    }

    public static String getDuration(long seconds) {
        seconds = (seconds / 1000);
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%2d:%02d", m, s);
    }

    public static String uri2filename(Uri uri) {

        String ret = null;
        String scheme = uri.getScheme();

        if (scheme.equals("file")) {
            ret = uri.getLastPathSegment();
        } else if (scheme.equals("content")) {
            Cursor cursor = ApplicationClass.getInstance().getApplicationContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                ret = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return ret;
    }

    public static String getUniqueId() {
//        String android_id = UUID.randomUUID().toString();

        @SuppressLint("HardwareIds") String android_id = android.provider.Settings.Secure.getString(ApplicationClass.getInstance().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

}
