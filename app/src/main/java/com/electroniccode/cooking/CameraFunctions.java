package com.electroniccode.cooking;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class CameraFunctions {

    public static String getImagePathFromUri(Activity activity, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        try {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(index);

        } catch (NullPointerException e) {
            CustomToast.error(activity, e.getMessage(), Toast.LENGTH_LONG).show();
            cursor.close();
            return null;
        }

    }

}
