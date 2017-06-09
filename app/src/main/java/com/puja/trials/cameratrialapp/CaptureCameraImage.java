package com.puja.trials.cameratrialapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

public class CaptureCameraImage extends Activity {

        private static String TAG = "CaptureCameraImage";
        public static int cameraID = 0;
        public static ImageView image;
        private final int firstInterval = 3000; //5 sec
        private final int secondInterval = 15000; //15 sec
        private final int thirdInterval = 30000; //30 sec
        private final int fourthInterval = 60000; //60 sec
        static int picCount = 0;
        private Handler cameraHandler = new Handler();

        private Runnable cameraRunnable = new Runnable(){
                public void run() {

                        Log.d(TAG, picCount + " - Switching Camera Modes");

                        if (picCount <= 10) {
                                if (picCount > 0 && picCount % 2 == 0) {
                                        backCamera();

                                }else if (picCount % 2 == 1){
                                        frontCamera();
                                }
                                if (picCount == 10)
                                        cameraHandler.postDelayed(cameraRunnable, secondInterval);
                                else
                                        cameraHandler.postDelayed(cameraRunnable, firstInterval);
                        } else if (picCount <= 20) {
                                if (picCount > 0 && picCount % 2 == 0) {
                                        backCamera();

                                }else if (picCount % 2 == 1){
                                        frontCamera();
                                }
                                if (picCount == 20)
                                        cameraHandler.postDelayed(cameraRunnable, thirdInterval);
                                else
                                        cameraHandler.postDelayed(cameraRunnable, secondInterval);

                        } else if (picCount <= 30) {
                                if (picCount > 0 && picCount % 2 == 0) {
                                        backCamera();

                                }else if (picCount % 2 == 1){
                                        frontCamera();
                                }
                                if (picCount == 30){
                                        cameraHandler.postDelayed(cameraRunnable, fourthInterval);
                                     //   cameraHandler.removeCallbacks(cameraRunnable);
                                }else
                                        cameraHandler.postDelayed(cameraRunnable, thirdInterval);
                        } else if (picCount < 50) {
                                if (picCount > 0 && picCount % 2 == 0) {
                                        backCamera();

                                }else if (picCount % 2 == 1){
                                        frontCamera();
                                }
                                cameraHandler.postDelayed(cameraRunnable, fourthInterval);
                        }
                        if (picCount == 50){
                                cameraHandler.removeCallbacks(cameraRunnable);
                        }
                }
        };


        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_capture_camera_image);
                image = (ImageView) findViewById(R.id.imgView);

                backCamera();
                cameraHandler.postDelayed(cameraRunnable, firstInterval);
        }

        public void frontCamera(){
                cameraID = 1;
                picCount++;
                Intent i = new Intent(CaptureCameraImage.this,CameraView.class);
                startActivityForResult(i, 999);
        }

        public void backCamera() {
                cameraID = 0;
                picCount++;
                Intent i = new Intent(CaptureCameraImage.this,CameraView.class);
                startActivityForResult(i, 999);
        }


}