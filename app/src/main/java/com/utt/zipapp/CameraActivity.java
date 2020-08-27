package com.utt.zipapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.palette.graphics.Palette;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.utt.zipapp.Model.Classifier;
import com.utt.zipapp.Model.TensorflowImageClassifier;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class CameraActivity extends AppCompatActivity {

    private static final String MODEL_PATH = "mobilenet_v1_1.0_224_quant.tflite";
    private static final boolean QUANT = true;
    private static final String LABEL_PATH = "labels_mobilenet_quant_v1_224.txt";
    private static final int INPUT_SIZE = 224;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSION_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int REQUEST_CODE_CAMERA = 123;
    int REQUEST_CODE_FOLDER = 456;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult, imgBack, imgChooseImage;
    private CameraView cameraView;
    LinearLayout layout;
    String stringURL;

    private Palette.Swatch vibrantSwatch;

    private String imagePath;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private Uri urii;
    private ImageView imgScanner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        initLayout();


        final Intent intent = getIntent();
        stringURL = intent.getStringExtra("url");


        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {

                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);
                setModelPath(bitmap);


            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });

        btnToggleCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.toggleFacing();
            }
        });

        btnDetectObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.captureImage();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                Intent result = Intent.createChooser(intent,"Choose Image");
                startActivityForResult(result,REQUEST_CODE_FOLDER);
            }
        });

        imgScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(CameraActivity.this, ScanQRcodeActivity.class);
                intent1.putExtra("url", stringURL);
                startActivity(intent1);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


        initTensorFlowAndLoadModel();
    }

    private void initLayout() {
        cameraView = findViewById(R.id.cameraView);
        imageViewResult = findViewById(R.id.imageViewResult);
        textViewResult = findViewById(R.id.textViewResult);
        layout = (LinearLayout)findViewById(R.id.layout2);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        btnToggleCamera = findViewById(R.id.btnToggleCamera);
        btnDetectObject = findViewById(R.id.btnDetectObject);
        imgChooseImage = (ImageView)findViewById(R.id.imgChooseImage);
        imgScanner = (ImageView)findViewById(R.id.imgScanner);
    }

    //open bottom dialog
    private void openBottomDialog(final Uri uri){
        bottomSheetDialog  = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(CameraActivity.this)
                .inflate(
                        R.layout.layout_bottom_sheet,
                        (LinearLayout) findViewById(R.id.bottomSheetLayout)
                );

        ImageView imageView = bottomSheetView.findViewById(R.id.imgBottomSheet);
        imageView.setImageURI(uri);
        urii = uri;
        bottomSheetView.findViewById(R.id.btnCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (urii!=null){
                    Bitmap bitmapp = decodeUriToBitmap(getApplicationContext(), urii);
                    if (bitmapp!=null){
                        setModelPath(bitmapp);
                    }else{
                        Log.e("bitmapp","null");
                    }
//                    detectImageColor(loadBitmap(imagePath));
                }else{
                    Log.e("uri","xyz");

                }



            }
        });
        bottomSheetView.findViewById(R.id.imgBackBottomSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void setModelPath(Bitmap bitmap){
        try {
            Log.e("results","helll");
            Bitmap selectedImage = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
            List<Classifier.Recognition> results = classifier.recognizeImage(selectedImage);
            Log.e("results",""+results);
            String obj = results.toString().replaceAll("\\[|\\]", "");
            String obj2 = obj.replaceAll(",","+");
            String a = stringURL.concat(obj2);
//            Toast.makeText(getApplicationContext(),""+a,Toast.LENGTH_SHORT).show();
            if (a.equals("")) {
                Toast.makeText(getApplicationContext(), "No objects found. Please check again !", Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(CameraActivity.this, SearchActivity.class);
                intent.putExtra("url", a);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            try {
                final Uri uri = data.getData();
                openBottomDialog(uri);
            }catch (Exception e){
                e.printStackTrace();
            }
//                imageViewResult.setImageURI(uri);

        }

    }
        private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this,contentUri,proj,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return  result;
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
                cameraView.start();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                Toast.makeText(getApplicationContext(), "Camera Permission is required",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();

    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorflowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnDetectObject.setVisibility(View.VISIBLE);
            }
        });
    }

    private void detectImageColor(Bitmap bitmap){
        if (bitmap!=null) {

            Palette.from(bitmap).maximumColorCount(32).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    vibrantSwatch = palette.getVibrantSwatch();
                    Log.e("vibrantSwatch", ""+vibrantSwatch.toString());
                    Toast.makeText(getApplicationContext(), "color: "+vibrantSwatch.toString(),Toast.LENGTH_SHORT).show();
                    nextSwatch(vibrantSwatch);
                }
            });
        }
    }

    public Bitmap loadBitmap(String url){
        Bitmap bm = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(url).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            bm = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return bm;
    }

    public void nextSwatch(Palette.Swatch vibrantSw) {
        Palette.Swatch currentSwatch = null;
        currentSwatch = vibrantSw;


        if (currentSwatch != null) {
//            rootLayout.setBackgroundColor(currentSwatch.getRgb());
//            txtTitle.setTextColor(currentSwatch.getRgb());
//            txtTitle.setText("Vibrant: " + currentSwatch.toString());
            Log.e("color: ",currentSwatch.toString());
//            textViewBody.setTextColor(currentSwatch.getBodyTextColor());
        } else {

//            txtTitle.setTextColor(Color.RED);
        }
    }
}
