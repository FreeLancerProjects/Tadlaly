package com.semicolon.tadlaly.share;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
    public static String getImagePathFromUri(Context context , Uri uri)
    {
        int current_version ;
        try {
            current_version = android.os.Build.VERSION.SDK_INT;
        }catch (NumberFormatException e)
        {
            current_version = 3;
        }

        if (current_version>= Build.VERSION_CODES.KITKAT)
        {
            String image_path="";
            String ID = DocumentsContract.getDocumentId(uri);
            ID = ID.split(":")[1];
            String selection = MediaStore.Images.Media._ID+"=?";
            String [] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,projection,selection,new String[]{ID},null);
            if (cursor!=null&&cursor.moveToFirst()){

                image_path = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                cursor.close();

            }
            return image_path;

        }else if (current_version<=Build.VERSION_CODES.HONEYCOMB_MR2&&current_version>=Build.VERSION_CODES.HONEYCOMB)
        {
            String image_path = "";
            String [] projection = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(context,uri,projection,null,null,null);
            Cursor cursor = cursorLoader.loadInBackground();
            if (cursor!=null&& cursor.moveToFirst())
            {
                image_path = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                cursor.close();
            }
            return image_path;
        }else
            {
                String image_path = "";
                String [] projection ={MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);
                if (cursor!=null && cursor.moveToFirst())
                {
                    image_path = cursor.getString(cursor.getColumnIndexOrThrow(projection[0]));
                    cursor.close();
                }
                return image_path;
            }
    }


    public static File getFileFromPath(String path)
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

    public static RequestBody getRequestBody(String content)
    {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),content);
        return requestBody;
    }
}
