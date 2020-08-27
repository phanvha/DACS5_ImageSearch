package com.utt.zipapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.utt.zipapp.Fragments.HistoryFragment;
import com.utt.zipapp.Fragments.HomeFragment;
import com.utt.zipapp.Fragments.SettingFragment;
import com.utt.zipapp.Model.Classifier;
import com.wonderkiln.camerakit.CameraView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener, HistoryFragment.OnFragmentInteractionListener, SettingFragment.OnFragmentInteractionListener {

    private static final int RC_MAIN = 111;
    private static final int RC_SIGN_IN = 122;
    private static final String TAG = "HomeActivity";
    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    private Button btnMenu;
    private ImageView img_menu, imgSearch;

    private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final boolean QUANT = true;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();
    private TextView textViewResult, txtTitle;
    private LinearLayout rootLayout;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult;
    private CameraView cameraView;
    private Button btnTranslate;
    //detect color
    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;
    private int swatchNumber;

    private ChipNavigationBar chipNavigationBar;
    private Fragment fragment = null;
    private SharedPreferences pref;
    private TextView tvNav_Username, tvNav_Email;
    private Button btnLogin, btnLogout;
    private ImageView image_Avatar;
    public GoogleApiClient mGoogleApiClient;
    public GoogleSignInClient googleSignInClient;

    AlertDialog alertDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadLanguageSaved();

        setContentView(R.layout.activity_home);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        initLayout();
//        if (isNetworkConnected()){
//            Log.d("INTERNET", "Connected!");
//        }else{
//            Log.d("INTERNET", "Can't Connect! Please allow internet connection !!! ");
//            return;
//        }
        //login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(HomeActivity.this, gso);

        //Navigation
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);



        chipNavigationBar.setItemSelected(R.id.fgHome, true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fmContainer, new HomeFragment()).commit();
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i){
                    case R.id.fgHome:
                        fragment = new HomeFragment();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case R.id.fgHistory:
                        fragment = new HistoryFragment();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                    case R.id.fgSettings:
                        fragment = new SettingFragment();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        break;
                }
                if (fragment!=null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fmContainer,fragment).commit();
                }
            }
        });
        checkAccount();
    }

    public void  getchip (){
        chipNavigationBar.setItemSelected(R.id.fgHome, true);
    }

    private void checkAccount(){
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(this);
        if (gg!=null){
            getProfileGGAccount();
        }else{
            btnLogout.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            Log.d("Login", "null");
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void initLayout(){


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        tvNav_Username = (TextView)headerView.findViewById(R.id.tvNav_Username);
        tvNav_Email = (TextView) headerView.findViewById(R.id.tvNav_Email);
        image_Avatar = (ImageView) headerView.findViewById(R.id.image_Avatar);
        btnLogout = (Button) headerView.findViewById(R.id.btnLogout);
        btnLogout.setVisibility(View.GONE);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutAccountGoogle();
            }
        });
        btnLogin = (Button) headerView.findViewById(R.id.btnLogin);
        btnLogin.setVisibility(View.GONE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

        img_menu = (ImageView) findViewById(R.id.imgMenu);
        chipNavigationBar = (ChipNavigationBar) findViewById(R.id.chipNav);

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Log.e("this", "Login with google account!");
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
            recreate();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } catch (Exception ex) { }
    }

    @SuppressLint("SetTextI18n")
    public void loadLanguageSaved(){
        pref = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("myLanguage","");
        Log.e("lang", language+"");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor edit = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        edit.putString("myLanguage", language);
        edit.apply();
        edit.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
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
                    Log.d(TAG, "WIFI allowed!");

                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    showCustomDialog();
                    break;

            }
        }
    };

    private void showCustomDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                        Log.d(TAG, "Cancel");
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.ac) {
//
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_home:
                Intent intent1 = new Intent(this, HomeActivity.class);
                startActivity(intent1);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_introduce:
                Intent intent2 = new Intent(HomeActivity.this, IntroduceActivity.class);
                startActivity(intent2);
                drawer.closeDrawer(GravityCompat.START);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //gte google profile
    private void getProfileGGAccount(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            String idToken =acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();
            Log.d("get_data_from_gg",
                    "DisplayName: "+personName+
                            "GivenName: "+ personGivenName+
                            "FamilyName: "+personFamilyName+
                            "Email: "+personEmail+
                            "Id: "+personId+
                            "PhotoUrl: "+personPhoto.toString()+
                            "IdToken: "+idToken+
                            "ServerAuthCode: "+ serverAuthCode);

            //set layout
            tvNav_Username.setText(personName);
            tvNav_Email.setText(personEmail);
            Glide.with(this).load(String.valueOf(personPhoto)).into(image_Avatar);
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            //saveAccount(personEmail);
        }
    }

    private void signOutAccountGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        //Toast.makeText(getApplicationContext(), "Đăng xuất thành công !", Toast.LENGTH_SHORT).show();
                        recreate();
//                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                        startActivityForResult(intent, RC_MAIN);
//                        finish();
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                });

    }
}
