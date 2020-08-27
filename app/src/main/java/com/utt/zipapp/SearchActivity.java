package com.utt.zipapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.utt.zipapp.Fragments.HomeFragment;
import com.utt.zipapp.Model.ItemsHistory;
import com.utt.zipapp.SQLiteOpenHelper.SQLite;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Callback;

public class SearchActivity extends AppCompatActivity {

//    private static String URL = "https://shopee.vn/search?keyword=";
//    private static String keyword = "gas";
    private String URL;
    private WebView webView;
    SharedPreferences pref;
    ProgressBar progressBar;
    LinearLayout linearLayout;
    SQLite db;
    private String personID;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initLayout();

        progressBar.setMax(100);


//        receive data url
        Intent getIntent = getIntent();
        URL = "";
        URL = getIntent.getStringExtra("url");
        Log.e("URL",""+URL);
        if (URL!=null){
            db = SQLite.getInstance(SearchActivity.this);
            Log.d("testss",getPersonID()+"");
            db.insertListHistory(new ItemsHistory(0,URL, gettime(),getPersonID()));

        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            webSettings.setDomStorageEnabled(true);
        }

        webView.setWebViewClient(new WebViewClient());
        if (URL!=null){
            webView.loadUrl(URL);
        }


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                linearLayout.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                linearLayout.setVisibility(View.GONE);

                super.onPageFinished(view, url);
            }


//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                Log.d("load", url+" is loaded!");
//                return true;
//
//            }
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(getApplicationContext(), "No Internet connection!", Toast.LENGTH_SHORT).show();
//
//            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                getSupportActionBar().setTitle(title);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        });

    }
    private String getPersonID() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(SearchActivity.this);
        if (acct != null) {

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            personID = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            String idToken = acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();
            Log.d("personID", personID+" yesss");
            return  personID;

        }else{
            Log.d("idToken", personID+"noooo");
            return personID;
        }
    }
    private void initLayout(){
        linearLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webview);

    }

    private String gettime(){
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String dt = dateFormatter.format(today);
        return dt;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_back:
                onBackPressed();
                break;
            case R.id.action_forward:
                onForwardPressed();
                break;
            case R.id.action_refresh:
                webView.reload();
                break;
            case R.id.action_copy_link:
                setClipboard(getApplicationContext(),URL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), ""+text, Toast.LENGTH_SHORT).show();
        }
    }

    private void onForwardPressed(){
        if (webView.canGoForward()){
            webView.goForward();
        }else{
            Log.d("onForwardPressed","Can't go forward!!!");
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else {
            finish();
        }
    }
}
