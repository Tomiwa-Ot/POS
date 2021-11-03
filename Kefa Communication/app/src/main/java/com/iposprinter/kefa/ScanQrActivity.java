package com.iposprinter.kefa;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


import java.io.IOException;


public class ScanQrActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_scan_qr);
        surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        if(ActivityCompat.checkSelfPermission(ScanQrActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(ScanQrActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, REQUEST_CAMERA_PERMISSION
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorAndSources();
    }

    private void initialiseDetectorAndSources() {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        if(!barcodeDetector.isOperational()){
            Toast.makeText(getApplicationContext(), "not working", Toast.LENGTH_SHORT).show();
        }
//        BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
//                .setBarcodeFormats(
//                        com.google.mlkit.vision.barcode.Barcode.FORMAT_QR_CODE,
//                        com.google.mlkit.vision.barcode.Barcode.FORMAT_CODE_39
//                ).build();
//        BarcodeScanner barcodeScanner = BarcodeScanning.getClient(options);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(screenHeight, screenWidth)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    if(ActivityCompat.checkSelfPermission(ScanQrActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        cameraSource.start(surfaceView.getHolder());
                    }else{
                        finish();
                    }
                }catch (IOException e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>(){
            @Override
            public void release() {
//                Toast.makeText(getApplicationContext(), "To prevent memory leaks qr code scanner has stopped", Toast.LENGTH_LONG).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() != 0){
                    Intent intent = new Intent();
                    intent.putExtra("address", barcodes.valueAt(0).rawValue);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }
}