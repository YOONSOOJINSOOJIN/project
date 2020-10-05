package com.example.smuocr;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

    /*
    Description : This is TessTwo Class.
                  TessTwo is initialized when the app is launched
                  (Default Options is English)
                  and works by calling ocr_run.
    */
public class TessTwo {


    public TessBaseAPI ocr;
    private String result = "";


    public TessTwo(String mDataPath, String lang){

        ocr = new TessBaseAPI();
        ocr.init(mDataPath, lang);
    }


    /*
        Description : ocr_run(Bitmap bit) takes a bitmap image as input
        and returns an OCR string.

        Arguments : Bitmap images
        Return : OCR string

     */
    public String ocr_run(Bitmap bit){
        ocr.setImage(bit);
        result = ocr.getUTF8Text();
        return result;
    }


}
