package com.utt.zipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanQRcodeActivity extends AppCompatActivity {
    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    private TextView resultsData;
    private final int REQUEST_CODE_QR = 123;
    private String stringURL, LINK = "https://google.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qrcode);

        scannerView = (CodeScannerView) findViewById(R.id.scannerView);
        resultsData = (TextView)findViewById(R.id.txtResults);
        codeScanner = new CodeScanner(this,scannerView);
        final Intent intent = getIntent();
        stringURL = intent.getStringExtra("url");



        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getApplicationContext(), ""+result.getText(),Toast.LENGTH_SHORT).show();
                        Log.e("scanner", ""+result.getText());
                        resultsData.setText(result.getText());
                        Pattern pattern = Pattern.compile("http");
                        Matcher m = pattern.matcher(result.getText());
                        boolean b = m.find();
                        if (b == true){
                            Intent intent = new Intent(ScanQRcodeActivity.this, SearchActivity.class);
                            intent.putExtra("url",  result.getText());
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            finish();
                        }else{
                            Intent intent = new Intent(ScanQRcodeActivity.this, SearchActivity.class);
                            intent.putExtra("url",  stringURL + result.getText());
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            finish();
                        }



//                        Intent intent = new Intent(result.getText());
//                        startActivityForResult(intent, REQUEST_CODE_QR);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            switch (requestCode){
                case REQUEST_CODE_QR:
                    String a = data.getDataString();
                    Intent intent = new Intent(this, SearchActivity.class);
                    intent.putExtra("url", a);
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);


            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestForCamera();
    }

    private void requestForCamera() {
        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                codeScanner.startPreview();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                finish();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}
