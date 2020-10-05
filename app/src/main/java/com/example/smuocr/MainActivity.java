package com.example.smuocr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    /* TessTwo variable */
    private String path = "";
    private String mDataPath = "";
    String lang = "";
    TessTwo tess;
    /*                  */


    /* Temp file variable */
    private File tempFile;
    /*                    */

    /* */
    ProgressDialog progressDialog;
    Handler handler_ocr;
    Handler handler_thresholding;
    Bitmap originalBm;
    Bitmap originalBm_Thresholding;
    String OCRresult = "";
    /* */

    /* initialize Opnecv Module */
    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("opencv", "OpenCV is Configured or Connected");

        } else {
            Log.d("opencv", "Opencv not Working or Loaded");
        }
    }
    /*                           */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Tesseract OCR initialize */
        mDataPath = getFilesDir() + "/tesseract/";
        FileChecking();
        tess = new TessTwo(mDataPath, lang);
        /*                          */

        /* get Permission */
        Permission ted = new Permission(this);
        ted.tedPermission();
        /*            */

        /* create Temp file for image processing */
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*                  */


        Button CameraScanButton = (Button) findViewById(R.id.scanbtn);
        Button TranslateButton = (Button) findViewById(R.id.translatebtn);
        ImageButton WordBookButton = (ImageButton) findViewById(R.id.wordbookbtn);



        CameraScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Camera cam = new Camera(getApplicationContext(), tempFile);
                Intent intent = cam.takePhoto();
                startActivityForResult(intent, Constant.PICK_FROM_CAMERA_SCAN);
            }
        });


        TranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog();
            }
        });




        /*
            Go WordBookActivity.
         */
        WordBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, VocaList.class));
            }
        });




    }
    /*
        Description : this is a dialog that
                      appears when the Scan button or OCR button is pressed.
                      it handles branch instructions according to the user select.
     */
    public void Dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final String str[]={"카메라","기기에서 불러오기"};
        builder.setTitle("Select Mode")
                .setNegativeButton("취소",null)
                .setItems(str,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){

                            case 0 :
                                Toast.makeText(MainActivity.this,"카메라",Toast.LENGTH_SHORT).show();
                                Camera cam = new Camera(getApplicationContext(), tempFile);
                                Intent intent = cam.takePhoto();
                                startActivityForResult(intent, Constant.PICK_FROM_CAMERA_OCR);
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this,"기기에서 불러오기",Toast.LENGTH_SHORT).show();
                                Album alb = new Album();
                                Intent intent_2 = alb.goAlbum();
                                startActivityForResult(intent_2, Constant.PICK_FROM_ALBUM_OCR);
                                break;
                        }
                    }
                });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        //Log.e(Constant.TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }

            return;
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //Uri savingUri = Uri.fromFile(tempFile);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    FileOutputStream out = new FileOutputStream(tempFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                } catch (Exception e) {
                }
                if (Constant.isScan == false && Constant.isOCR == true) {
                    setImage_OCR();
                } else if (Constant.isScan == true && Constant.isOCR == false) {
                    try {
                        setImage_Scan();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        switch (requestCode) {
            case Constant.PICK_FROM_ALBUM_OCR: {

                Constant.isScan = false;
                Constant.isOCR = true;

                Uri photoUri = data.getData();

                cropImage(photoUri);

                break;
            }
            case Constant.PICK_FROM_CAMERA_OCR: {


                Constant.isScan = false;
                Constant.isOCR = true;

                Uri photoUri = Uri.fromFile(tempFile);

                cropImage(photoUri);

                break;
            }

            case Constant.PICK_FROM_CAMERA_SCAN: {

                Constant.isScan = true;
                Constant.isOCR = false;

                Uri photoUri = Uri.fromFile(tempFile);

                cropImage(photoUri);
                break;
            }

        }

    }
    /*
        Description : setImage_OCR is pass the image to ocr engine then
                      obtain a ocr string
    */

    private void setImage_OCR() {

        /* select image view */
        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, Constant.isCamera);
        BitmapFactory.Options options = new BitmapFactory.Options();
        originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        //Log.d(Constant.TAG, "setImage : " + tempFile.getAbsolutePath());


        class OCRThread extends Thread {


            @Override
            public void run() {

                        OCRresult = tess.ocr_run(originalBm);
                        handler_ocr.sendEmptyMessage(217);

            }
        }
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("잠시만 기다려 주세요");
        progressDialog.show();
        final OCRThread ocr_threads = new OCRThread();
        ocr_threads.start();


        handler_ocr = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 217) {
                    progressDialog.dismiss();
                    ocr_threads.interrupt();
                    Intent intent = new Intent(MainActivity.this, TranslateActivity.class);
                    intent.putExtra("OCR_STRING",OCRresult);

                    if (Constant.isCamera == true) {
                        /**/
                        tempFile = null;
                        try {
                            tempFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        /**/
                    }
                    startActivity(intent);


                }
            }
        };


    }

    /*
        Description : setImage_OCR is pass the image to thresholding thread then
                      obtain a thresholding image
    */

    private void setImage_Scan() throws IOException {

        ImageResizeUtils.resizeFile(tempFile, tempFile, 1280, Constant.isCamera);
        BitmapFactory.Options options = new BitmapFactory.Options();
        originalBm_Thresholding = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        //Log.d(Constant.TAG, "setImage : " + tempFile.getAbsolutePath());




        class ThresholdingThread extends Thread {


            @Override
            public void run() {

                originalBm_Thresholding = Integral_Thresholding(originalBm_Thresholding);
                handler_thresholding.sendEmptyMessage(117);

            }
        }
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("잠시만 기다려 주세요");
        progressDialog.show();
        final ThresholdingThread thresholding_threads = new ThresholdingThread();
        thresholding_threads.start();



        handler_thresholding = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 117) {
                    progressDialog.dismiss();


                    String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
                    String imageFileName = "Scan_" + timeStamp + "_"+"mu"+".jpg";



                    String FilePath = Environment.getExternalStorageDirectory() +"/Scan/";
                    File storageDir = new File(Environment.getExternalStorageDirectory() + "/Scan/");
                    if (!storageDir.exists()) storageDir.mkdirs();



                    SaveBitmapToFileCache(originalBm_Thresholding,FilePath,imageFileName);


                    if (Constant.isCamera == true) {
                        /**/
                        tempFile = null;
                        try {
                            tempFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    thresholding_threads.interrupt();

                }
            }
        };



    }

    /* bitmap save to temp file */
    public static void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath, String filename) {


        File file = new File(strFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        File fileCacheItem = new File(strFilePath + filename);
        OutputStream out = null;

        try {

            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /*
        Description : integral_thresholding creates an integral image from the given image
        and through integral image, creates and returns a binary image.
        more detail,
        see Derek Bradley, Gerhard Roth "Adaptive Thresholding Using the Integral Image".
    */

    private Bitmap Integral_Thresholding(Bitmap bitmap) {


        Mat image = new Mat();
        org.opencv.android.Utils.bitmapToMat(bitmap, image);

        Mat dst = new Mat();
        Imgproc.cvtColor(image, dst, Imgproc.COLOR_BGR2GRAY);

        Mat thresholding = new Mat();
        Imgproc.cvtColor(image, thresholding, Imgproc.COLOR_BGR2GRAY);



        int rows = thresholding.rows();
        int cols = thresholding.cols();

        Mat integral = new Mat();
        Imgproc.integral(thresholding, integral);

        int s = Math.max(rows, cols) / 8;
        double T = 0.15;

        int s2 = s / 2;

        int x1, x2, y1, y2, cnt, sum;

        for (int i = 0; i < rows; ++i) {
            y1 = i - s2;
            y2 = i + s2;

            if (y1 < 0) {
                y1 = 0;
            }
            if (y2 >= rows) {
                y2 = rows - 1;
            }

            for (int j = 0; j < cols; ++j) {
                x1 = j - s2;
                x2 = j + s2;

                if (x1 < 0) {
                    x1 = 0;
                }
                if (x2 >= cols) {
                    x2 = cols - 1;
                }
                cnt = (x2 - x1) * (y2 - y1);

                double[] data1 = integral.get(y2, x2);
                double[] data2 = integral.get(y2, x1);
                double[] data3 = integral.get(y1, x2);
                double[] data4 = integral.get(y1, x1);

                sum = (int) (data1[0] - data2[0] - data3[0] + data4[0]);


                double[] temp = thresholding.get(i, j);



                if ((int) (temp[0] * cnt) < (int) (sum * (1.0 - T))) {
                    dst.put(i, j, 0);
                } else {
                    dst.put(i, j, 255);
                }

            }
        }

        org.opencv.android.Utils.matToBitmap(dst, bitmap);
        return bitmap;

    }


    /* Creating image file & image crop */
    private File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "smuOCR_" + timeStamp + "_";


        File storageDir = new File(Environment.getExternalStorageDirectory() + "/smuOCR/");
        if (!storageDir.exists()) storageDir.mkdirs();

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    private void cropImage(Uri photoUri) {


        if (tempFile == null) {
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                finish();
                e.printStackTrace();
            }
        }

        CropImage.activity(photoUri)
                .start(this);
    }
    /* Creating image file & image crop */


    /* TessTwo initialize */
    private void checkFile(File dir, String Language) {

        if (!dir.exists() && dir.mkdirs()) {
            copyFiles(Language);
        }

        if (dir.exists()) {
            String datafilepath = mDataPath + "tessdata/" + Language + ".traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles(Language);
            }
        }
    }

    private void copyFiles(String Language) {
        try {
            String filepath = mDataPath + "/tessdata/" + Language + ".traineddata";
            AssetManager assetManager = getAssets();
            InputStream instream = assetManager.open("tessdata/" + Language + ".traineddata");
            OutputStream outstream = new FileOutputStream(filepath);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void FileChecking() {

        for (String Language : Constant.mLanguageList) {
            checkFile(new File(mDataPath + "tessdata/"), Language);
            lang += Language + "+";
        }
    }
    /* TessTwo initialize */






}