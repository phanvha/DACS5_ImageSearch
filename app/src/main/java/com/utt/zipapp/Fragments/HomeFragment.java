package com.utt.zipapp.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.BaseColumns;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.utt.zipapp.Adapter.CustomAdapter;
import com.utt.zipapp.CameraActivity;
import com.utt.zipapp.HomeActivity;
import com.utt.zipapp.LoginActivity;
import com.utt.zipapp.Model.CustomItemsSpinner;
import com.utt.zipapp.R;
import com.utt.zipapp.SearchActivity;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    private ImageView imgSmallLogo, imageViewResult;
    private Button btnOpenDialog;

    private Context mContext;
    private Spinner spinner;
    private String stringURL;
    private SearchView searchView;
    private SharedPreferences pref;
    private ImageView imgSpeech;
    private ImageButton imgButton;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private SimpleCursorAdapter simpleCursorAdapter;
    private static final String[] SUGGESTIONS = {
            "Mouse", "Apple", "Laptop",
            "Banana", "Computer", "Human",
            "Tomato", "Smart phone","Marketing", "Ananas", "Hulk",
            "Iron Man", "Company", "Healthy",
            "Coffee", "Smart home"

    };
    public GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 101;
    private TextView tvNav_Username, tvNav_Email;
    private ImageView image_Avatar;
    private Button btnLogin, btnLogout;
    private GoogleApiClient mGoogleApiClient;

    NavigationView navigationView;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private int currentApiVersion;


    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loadLanguageSaved();

//        getActivity().getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorTransparent));
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        initLayout(view);
        //checkAccount(view);
        getSpinner(view);
        imgSmallLogo.setOnClickListener(this);
        btnOpenDialog.setOnClickListener(this);
        startAnimationLogo(imgSmallLogo);

        final String[] from = new String[] {"Keyword"};
        final int[] to = new int[] {android.R.id.text1};
        simpleCursorAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                openURL(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                populateAdapter(s);
                return false;
            }
        });

        searchView.setSuggestionsAdapter(simpleCursorAdapter);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {

                return true;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                Cursor cursor = (Cursor) simpleCursorAdapter.getItem(i);
                String txt = cursor.getString(cursor.getColumnIndex("Keyword"));
                searchView.setQuery(txt, true);
                return true;
            }
        });
        imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        return view;
    }

    //open bottom dialog
    private void openBottomDialog(View view){
        bottomSheetDialog  = new BottomSheetDialog(HomeFragment.this.getContext(), R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(HomeFragment.this.getActivity())
                .inflate(
                        R.layout.layout_bottom_sheet,
                        (LinearLayout) view.findViewById(R.id.bottomSheetLayout)
                );


        bottomSheetView.findViewById(R.id.imgBackBottomSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void checkAccount(View view){
        GoogleSignInAccount gg = GoogleSignIn.getLastSignedInAccount(HomeFragment.this.getContext());
        if (gg!=null){
            getProfileGGAccount();
        }else{
            openBottomDialog(view);
        }
//        if (gg!=null){
//            String name = gg.getDisplayName();
//            Toast.makeText(HomeFragment.this.getContext(), name+"Name: ",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(HomeFragment.this.getContext(), "Name: Null",Toast.LENGTH_SHORT).show();
//        }
    }

    //gte google profile
    private void getProfileGGAccount(){
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(HomeFragment.this.getContext());
        if (acct != null) {

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            String idToken =acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();
            Log.e("get_data_from_gg",
                    "DisplayName: "+personName+
                            "GivenName: "+ personGivenName+
                            "FamilyName: "+personFamilyName+
                            "Email: "+personEmail+
                            "Id: "+personId+
                            "PhotoUrl: "+personPhoto.toString()+
                            "IdToken: "+idToken+
                            "ServerAuthCode: "+ serverAuthCode);
            Toast.makeText(HomeFragment.this.getContext(),""+personName,Toast.LENGTH_SHORT).show();

//            //set layout
//            tvNav_Username.setText(personName);
//            tvNav_Email.setText(personEmail);
//            Glide.with(this).load(String.valueOf(personPhoto)).into(image_Avatar);
//            btnLogout.setVisibility(View.VISIBLE);
//            btnLogout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    signOutAccountGoogle();
//                }
//            });
//            //saveAccount(personEmail);
        }
    }

    private void signOutAccountGoogle() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(HomeFragment.this.getContext(), "Đăng xuất thành công !", Toast.LENGTH_SHORT).show();
                        getActivity().recreate();

                    }
                });

    }

    private void showDialogSignIn() {
        final Dialog dialog = new Dialog(HomeFragment.this.getActivity());
        dialog.setContentView(R.layout.dialog_sign_in);
        ImageView imgCancel = dialog.findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        SignInButton signInButton = dialog.findViewById(R.id.btn_GoogleSignIn);
        //login with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(HomeFragment.this.getActivity(), gso);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
//            String strData = "" + account.getDisplayName();
//            //Toast.makeText(getApplicationContext(),strData,Toast.LENGTH_LONG).show();
//            Log.e("Name",strData);
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
////            startActivityForResult(intent, RC_MAIN);
//            finish();
//            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);

        } catch (Exception ex) { }
    }

    // You must implements your logic to get data using OrmLite
    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "Keyword" });
        for (int i=0; i<SUGGESTIONS.length; i++) {
            if (SUGGESTIONS[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, SUGGESTIONS[i]});
        }
        simpleCursorAdapter.changeCursor(c);
    }

    private void initLayout(View view){
        btnOpenDialog = view.findViewById(R.id.btnOpenCamera);
        imgSmallLogo = (ImageView) view.findViewById(R.id.imgSmallLogo);
        searchView = (SearchView)view.findViewById(R.id.searchTextView);
        spinner = (Spinner)view.findViewById(R.id.spinner);
        imgButton = (ImageButton) view.findViewById(R.id.imgBtSpeech);
//        rootLayout = (LinearLayout) view.findViewById(R.id.root_layout);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech prompt");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity().getBaseContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    public void loadLanguageSaved(){
        pref = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("myLanguage","");
        Log.e("lang", language+"");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity().getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor edit = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        edit.putString("myLanguage", language);
        edit.apply();
        edit.commit();
    }

    private void getSpinner(View view){
        ArrayList<CustomItemsSpinner> customItemsSpinnerArrayList = new ArrayList<>();
        customItemsSpinnerArrayList.add(new CustomItemsSpinner("Google", R.drawable.ic_google));
        customItemsSpinnerArrayList.add(new CustomItemsSpinner("Lazada", R.drawable.lazada_icon));
        customItemsSpinnerArrayList.add(new CustomItemsSpinner("Shopee", R.drawable.shopee_icon));
        customItemsSpinnerArrayList.add(new CustomItemsSpinner("Tiki", R.drawable.tiki_icon));

        SpinnerAdapter spinnerAdapter = new CustomAdapter(mContext,customItemsSpinnerArrayList);
        if (spinner!=null){
            spinner.setAdapter(spinnerAdapter);
            spinner.setOnItemSelectedListener(this);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContext = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imgSmallLogo:
                startAnimationLogo(imgSmallLogo);
                break;
            case R.id.btnOpenCamera:
                Intent intent = new Intent(getActivity().getBaseContext(), CameraActivity.class);
                intent.putExtra("url", getURLToSearch(stringURL));
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

        }
    }

    private void openURL(String url){

        Intent intent = new Intent(getActivity().getBaseContext(), SearchActivity.class);
        String obj2 = url.replaceAll(",","+");
        String a = getURLToSearch(stringURL).concat(obj2);
        intent.putExtra("url", a);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    //set url to search
    private String getURLToSearch(String urlPage){
        Log.e("urlPage", urlPage);
        String url = "";
        switch (urlPage){
            case "Google":
                url = "https://www."+urlPage+".com/search?q=";

                break;
            case "Lazada":
                url = "https://"+urlPage+".vn/catalog/?q=";

                break;
            case "Shopee":
                url = "https://www."+urlPage+".vn/search?keyword=";
                break;
            case "Tiki":
                url = "https://www."+urlPage+".vn/search?q=";
                break;
            default:
                Log.e("ERROR", "Please check to urlPage again!");
        }
        Log.e("sourceUrl", url);
        return url;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String b = result.toString().replaceAll("\\[|\\]","");
                    searchView.setQuery(b,true);

                }
                break;
            }
            case RC_SIGN_IN:
                Log.e("TAG", "Login with google account!");
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);

        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        CustomItemsSpinner customItemsSpinner = (CustomItemsSpinner)adapterView.getSelectedItem();
        stringURL = customItemsSpinner.getSpinnerTitle();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void startAnimationLogo(final ImageView imageVie){
        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                long longAnimationRotate = 1000;
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageVie, "rotation",0, 360);
                objectAnimator.setDuration(longAnimationRotate);

                AnimatorSet animator = new AnimatorSet();
                animator.playTogether(objectAnimator);
                animator.start();

                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(r, 5000);



    }
}
