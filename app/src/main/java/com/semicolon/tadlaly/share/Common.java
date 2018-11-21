package com.semicolon.tadlaly.share;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.semicolon.tadlaly.R;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Common {

    public static void CloseKeyBoard(Context context,View view)
    {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(),0);

    }
    public static ProgressDialog createProgressDialog(Context context , String msg)
    {
        ProgressDialog  dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        ProgressBar bar = new ProgressBar(context);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog.setIndeterminateDrawable(drawable);
        return dialog;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImagePathFromUri(Context context,Uri uri)
    {
        int currentApiVersion;
        try
        {
            currentApiVersion = android.os.Build.VERSION.SDK_INT;
        }
        catch(NumberFormatException e)
        {
            //API 3 will crash if SDK_INT is called
            currentApiVersion = 3;
        }
        if (currentApiVersion >= Build.VERSION_CODES.KITKAT)
        {
           /* String filePath = "";
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array

            Log.e("wholeID",wholeID);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Images.Media.DATA};

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, new String[]{id}, null);

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst())
            {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;*/

            if ( DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/"
                                + split[1];
                    }
                }
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[] { split[1] };

                    return getDataColumn(context, contentUri, selection,
                            selectionArgs);
                }
            }
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }
        else if (currentApiVersion <= Build.VERSION_CODES.HONEYCOMB_MR2 && currentApiVersion >= Build.VERSION_CODES.HONEYCOMB)

        {
            String[] proj = {MediaStore.Images.Media.DATA};
            String result = null;

            CursorLoader cursorLoader = new CursorLoader(
                    context,
                    uri, proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            if (cursor != null)
            {
                int column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
            }
            return result;
        }
        else
        {

            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            int column_index
                    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }
    private static String getDataColumn(Context context, Uri uri,String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
    private static File getFileFromPath(String path)
    {
        File file = new File(path);
        return file;
    }

    public static MultipartBody.Part getMultiPartBody(Uri uri, Context context)
    {
        String path = getImagePathFromUri(context,uri);
        File file = getFileFromPath(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("user_photo",file.getName(),requestBody);
        return part;
    }

    public static MultipartBody.Part getListMultiPartBody(Uri uri, Context context)
    {
        String path = getImagePathFromUri(context,uri);
        File file = getFileFromPath(path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("images[]",file.getName(),requestBody);
        return part;
    }

    public static RequestBody getRequestBody(String content)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),content);
        return requestBody;
    }
}
