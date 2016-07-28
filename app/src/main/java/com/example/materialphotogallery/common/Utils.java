package com.example.materialphotogallery.common;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.materialphotogallery.R;
import com.example.materialphotogallery.event.ModelLoadedEvent;
import com.example.materialphotogallery.model.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

public class Utils {

    private Utils() {
        throw new AssertionError();
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    // hide the keyboard on executing search
    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    public static void showSnackbarSticky(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
    }

    // Check that a network connection is available
    public  static boolean isClientConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return  activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static Long generateCustomId() {
        // define an id based on the date time stamp
        Locale locale = new Locale("en_US");
        Locale.setDefault(locale);
        String pattern = "yyyyMMddHHmmssSS"; // pattern used to sort objects
        SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.getDefault());

        return Long.valueOf(formatter.format(new Date()));
    }

    // camera related utilities
    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isCameraAppInstalled(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> apps = pm.queryIntentActivities(intent, 0);
        return apps.size() > 0;
    }

    public static Uri generatePhotoFileUri(Context context) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getString(R.string.app_name ));

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Timber.i("%s: failed to create directory", Constants.LOG_TAG);
                return null;
            }
        }

        // Create a media file name
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    public static String generatePreviewImage(String filePath, int viewWidth, int viewHeight) {

        // generate scaled image path
        String temp = filePath.substring(0, filePath.length() - 4); // strip off file ext
        String previewPath = temp + "_preview.jpg";

        // get the bitmap's dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        // set default scale factor
        int scaleFactor = 1;

        // determine scale factor
        if (imageHeight > viewHeight || imageWidth > viewWidth) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            while ((halfHeight / scaleFactor) > viewHeight && (halfWidth / scaleFactor) > viewWidth) {
                scaleFactor *= 2;
            }

            // where you have images with unique aspect ratios, eg pano's
            long totalPixels = imageWidth * imageHeight / scaleFactor;

            // Anything more than 2x the requested pixels we'll sample down further
            final long totalReqPixelsCap = viewWidth * viewHeight * 2;
            while (totalPixels > totalReqPixelsCap) {
                scaleFactor *= 2;
                totalPixels /= 2;
            }
        }

        // decode the image file into a bitmap sized to fill the view
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        // write the bitmap to disk
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(previewPath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
        } catch (Exception e) {
            Timber.e("%s Failed to save thumbnail to disk: %s", Constants.LOG_TAG, e.getMessage());
        }
        return previewPath;
    }

    public static String generateThumbnailImage(String filePath, int targetWidth, int targetHeight) {

        // generate thumbnail path
        String temp = filePath.substring(0, filePath.length() - 4); // strip off file ext
        String thumbnailPath = temp + "_thumb.jpg";


        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetWidth, photoH/targetHeight);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);

        // write the bitmap to disk
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(thumbnailPath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
        } catch (Exception e) {
            Timber.e("%s Failed to save thumbnail to disk: %s", Constants.LOG_TAG, e.getMessage());
        }
        return thumbnailPath;
    }

    // database related utilities
    public static void queryAllItems(Context context) {
        Timber.i("%s: QUERY THE DATABASE", Constants.LOG_TAG);
        try {
            Cursor results = DatabaseHelper.getInstance(context).loadItems(context);
            EventBus.getDefault().postSticky(new ModelLoadedEvent(results));
        } catch (Exception e) {
            Timber.e("%s: error loading items from dbase, %s", Constants.LOG_TAG, e.getMessage());
        }
    }

    public static ContentValues setContentValues(long id, String title, String description,
            String filePath, String previewPath, String thumbnailPath, int favourite, double latitude, double longitude) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.PHOTO_ID, id);
        cv.put(Constants.PHOTO_TITLE, title);
        cv.put(Constants.PHOTO_DESCRIPTION, description);
        cv.put(Constants.PHOTO_FILE_PATH, filePath);
        cv.put(Constants.PHOTO_PREVIEW_PATH, previewPath);
        cv.put(Constants.PHOTO_THUMBNAIL_PATH, thumbnailPath);
        cv.put(Constants.PHOTO_FAVOURITE, favourite);
        cv.put(Constants.PHOTO_LATITUDE, latitude);
        cv.put(Constants.PHOTO_LONGITUDE, longitude);
        return cv;
    }

    public static ContentValues updateContentValues(long id, int favourite) {
        ContentValues cv = new ContentValues();
        cv.put(Constants.PHOTO_ID, id);
        cv.put(Constants.PHOTO_FAVOURITE, favourite);
        return cv;
    }

    public static void loadPreviewWithPicasso(Context context, String previewPath, ImageView view) {
        Picasso.with(context)
                .load(new File(previewPath))
                .fit() // scale image to fit image view element - also scales the placeholder
                .centerInside()
                .into(view);
    }

    public static void loadPreviewWithGlide(Context context, String previewPath, ImageView view) {
        Glide.with(context)
                .load(previewPath)
                .crossFade()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view);
    }

}
