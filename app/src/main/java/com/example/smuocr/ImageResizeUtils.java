package com.example.smuocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageResizeUtils {


    /*
        Desciption : This class contains functions for processing photos taken with the camera.
                     These functions are taken from the Android camera example project.
    */
    public static void resizeFile(File file, File newFile, int newWidth, Boolean isCamera) {

        String TAG = "smuOCR";

        Bitmap original_Bm = null;
        Bitmap resized_Bitmap = null;

        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPurgeable = true;
            options.inDither = true;

            original_Bm = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            if(isCamera) {

                try {

                    ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                    int exifOrientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    int exifDegree = exifOrientationToDegrees(exifOrientation);
                    Log.d(TAG,"exifDegree : " + exifDegree);

                    original_Bm = rotate(original_Bm, exifDegree);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if(original_Bm == null) {
                //Log.e(TAG,("파일 에러"));
                return;
            }

            int width = original_Bm.getWidth();
            int height = original_Bm.getHeight();

            float aspect, scaleWidth, scaleHeight;
            if(width > height) {
                if(width <= newWidth) return;

                aspect = (float) width / height;

                scaleWidth = newWidth;
                scaleHeight = scaleWidth / aspect;

            } else {

                if(height <= newWidth) return;

                aspect = (float) height / width;

                scaleHeight = newWidth;
                scaleWidth = scaleHeight / aspect;

            }

            // create a matrix for the manipulation
            Matrix matrix = new Matrix();

            // resize the bitmap
            matrix.postScale(scaleWidth / width, scaleHeight / height);

            // recreate the new Bitmap
            resized_Bitmap = Bitmap.createBitmap(original_Bm, 0, 0, width, height, matrix, true);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                resized_Bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(newFile));

            } else {

                resized_Bitmap.compress(Bitmap.CompressFormat.PNG, 80, new FileOutputStream(newFile));

            }


        } catch (FileNotFoundException e) {

            e.printStackTrace();

        } finally {

            if(original_Bm != null){
                original_Bm.recycle();
            }

            if (resized_Bitmap != null){
                resized_Bitmap.recycle();
            }
        }

    }


    public static int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
            }
        }
        return bitmap;
    }
}