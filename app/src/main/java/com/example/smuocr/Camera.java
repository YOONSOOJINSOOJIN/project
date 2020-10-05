package com.example.smuocr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


    /*
        Description : This class is responsible
                      for importing the image file from the album.
    */


public class Camera {
    Context c_context;
    File tempFile;


    public Camera(Context context, File temp){
        c_context = context;
        this.tempFile = temp;
    }

    /*
        Description : take photo from album and save to tempfile.
        Arguments : None
        Return : None
    */
    public Intent takePhoto(){
        Constant.isCamera = true;

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (this.tempFile != null) {


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                Uri photoUri = FileProvider.getUriForFile(c_context,
                        "com.example.smuocr.provider", tempFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            } else {
                Uri photoUri = Uri.fromFile(tempFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            }
        }
        return intent;
    }



}

