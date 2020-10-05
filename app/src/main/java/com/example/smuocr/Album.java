package com.example.smuocr;

import android.content.Intent;
import android.provider.MediaStore;

public class Album {


    /*
        Description : This class is responsible
                      for importing the image file from the album.
    */

    public Album(){
    }

    /*
        Description : When goAlbum is executed,
                      it select an image from the album and pass to the activity.
     */
    public Intent goAlbum(){
        Constant.isCamera = false;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        return intent;
    }
}
