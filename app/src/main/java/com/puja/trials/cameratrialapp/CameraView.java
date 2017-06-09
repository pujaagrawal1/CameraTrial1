package com.puja.trials.cameratrialapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraView extends Activity implements SurfaceHolder.Callback, View.OnClickListener {
    private static final String TAG = "CameraView";
    Camera mCamera;
    boolean mPreviewRunning = false;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera_view);
        ImageView img = (ImageView) findViewById(R.id.blankImage);

        img.setBackgroundResource(android.R.color.transparent);

        mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
        mSurfaceView.setOnClickListener(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
            super.onRestoreInstanceState(savedInstanceState);
    }

    Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            if (data != null){
                mCamera.stopPreview();
                mPreviewRunning = false;
                mCamera.release();
                try {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    Bitmap bitmap= BitmapFactory.decodeByteArray(data, 0, data.length,opts);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, false);

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int newWidth = 300;
                    int newHeight = 300;
                    // calculate the scale - in this case = 0.4f
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    matrix.postRotate(-90);
                    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

                    //String partFilename = currentDateFormat();
                    String partFilename = "";
                    if (CaptureCameraImage.cameraID == 0) {
                        partFilename = "" ;
                    } else if (CaptureCameraImage.cameraID == 1) {
                        partFilename = "frontCapture_" + String.valueOf(System.currentTimeMillis()) + "_" ;
                    }
                    storeCameraPhotoInSDCard(resizedBitmap, partFilename);

//                    String storeFilename = "photo_" + partFilename + ".jpg";
//                    Bitmap mBitmap = getImageFileFromSDCard(storeFilename);

                    CaptureCameraImage.image.setImageBitmap(resizedBitmap);  // ResizedBitmap is the IMAGE Captured

                }catch(Exception e){
                    e.printStackTrace();
                }
                setResult(585);
                finish();
                }
        }
    };

    @TargetApi(9)
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated");
        mCamera = Camera.open(CaptureCameraImage.cameraID);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Log.e(TAG, "surfaceChanged");

// XXX stopPreview() will crash if preview is not running
        if (mPreviewRunning){
            mCamera.stopPreview();
        }

        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewSize(300, 300);
        if(CaptureCameraImage.cameraID == 0){
            String stringFlashMode = p.getFlashMode();
            if (stringFlashMode.equals("torch"))
                p.setFlashMode("on"); //Light is set off, flash is set to normal 'on' mode
            else
                p.setFlashMode("torch");
        }
        mCamera.setParameters(p);

        try {
            mCamera.setPreviewDisplay(holder);
        }catch (Exception e){
            e.printStackTrace();
        }
        mCamera.startPreview();
        mPreviewRunning = true;
     //   mCamera.takePicture(null, mPictureCallback, mPictureCallback);
    }


    private String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    private void storeCameraPhotoInSDCard(Bitmap bitmap, String currentDate) {
        File outputFile = new File(Environment.getExternalStorageDirectory(), "/appdownloads/frontCapture_" + currentDate + ".jpg");
     //   File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "photo_" + currentDate + ".jpg");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Bitmap getImageFileFromSDCard(String filename){
        Bitmap bitmap = null;
        File imageFile = new File(Environment.getExternalStorageDirectory() + filename);
        try {
            FileInputStream fis = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    Log.e(TAG, "surfaceDestroyed");
    }

    public void onClick(View v) {
        mCamera.takePicture(null, mPictureCallback, mPictureCallback);
    }

    protected void onResume(){
        Log.e(TAG, "onResume");
        super.onResume();
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    protected void onStop(){
        Log.e(TAG, "onStop");
        super.onStop();
    }
}
