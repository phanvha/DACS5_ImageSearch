package com.utt.zipapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.utt.zipapp.Fragments.HomeFragment;
import com.utt.zipapp.R;
import com.utt.zipapp.Service.ConnectionReceiver;

public class LoginActivity extends AppCompatActivity {

    String URL;
    WebView webView;
    SignInButton signInButton;
    public GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 101;
    private String TAG="LoginActivity";
    TextView txtSkip, txtLogin;
    ConnectionReceiver connectionReceiver;
    AlertDialog alertDialog;
    private ImageView imgLogoLogin;
    private TextView txtTitleLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtLogin = findViewById(R.id.txtLogin);
        txtSkip = findViewById(R.id.txtSkip);
        txtTitleLogin = findViewById(R.id.txtTitleLogin);
        imgLogoLogin = findViewById(R.id.imgLogoLogin);
        //login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View,String>(imgLogoLogin,"trans_logo");
                pairs[1] = new Pair<View,String>(txtTitleLogin,"trans_title");

                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivity(intent, activityOptions.toBundle());
            }
        });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


    }
    private void checkInternetConnect(){
        WifiManager wifiManager = (WifiManager)  getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()){
            checkAccount();
        }else{
            showCustomDialog();
        }

//        if (!ConnectionReceiver.isConnected()) {
//            showCustomDialog();
//        }else{
//            checkAccount();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver,intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(wifiReceiver);
    }

    private BroadcastReceiver wifiReceiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState){
                case WifiManager.WIFI_STATE_ENABLED:
                    checkAccount();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    showCustomDialog();
                    break;

            }
        }
    };

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Please connect to internet to process further!")
                .setIcon(R.drawable.ic_check)
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                recreate();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void checkAccount(){
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(this);
        if (gg!=null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }else{
//            Toast.makeText(getApplicationContext(), "Name: Null",Toast.LENGTH_SHORT).show();
            Log.d("Login", "logged!");
        }
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG, "Login with google account!");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    //handle sign in gg
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.e("request","Đăng nhập thành công!");
            updateUI_AfterLoginGG(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Error", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    //update ui after login to GG
    private void updateUI_AfterLoginGG(GoogleSignInAccount account) {
        try {
            String strData = "" + account.getDisplayName();
            //Toast.makeText(getApplicationContext(),strData,Toast.LENGTH_LONG).show();
            Log.e("Name",strData);
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
//            startActivityForResult(intent, RC_MAIN);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } catch (Exception ex) { }
    }
}
