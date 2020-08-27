package com.utt.zipapp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.utt.zipapp.Adapter.CustomAdapter;
import com.utt.zipapp.Adapter.CustomAdapterLanguage;
import com.utt.zipapp.HomeActivity;
import com.utt.zipapp.LoginActivity;
import com.utt.zipapp.MainActivity;
import com.utt.zipapp.Model.CustomItemsSpinner;
import com.utt.zipapp.Model.ItemsLanguage;
import com.utt.zipapp.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context mcontext;
    private Spinner spinner;
    private Button btnReloadActivity, btnChangeLanguage;
    private TextView tvLanguage;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private String stringLanguage;
    SharedPreferences pref;
    private int REQUEST_CODE_LANGUAGE = 111;
    private ProgressDialog progressDialog;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        btnChangeLanguage = (Button)view.findViewById(R.id.btnChangeLanguage);
        tvLanguage = (TextView)view.findViewById(R.id.tvLanguage);
        getLang(view);

        btnChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                openChangeLanguageDialog(view);
            }
        });
//        getSpinner(view);
        return view;
    }

    private void openChangeLanguageDialog(View view) {
        final String[] listItems = {"Vietnamese","English", "French", "Korean"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingFragment.this.getContext());
        builder.setTitle(getString(R.string.select_language));
        builder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    setLocale("vi");
                    openDialogSys();
//                    SettingFragment.this.getActivity().recreate();
                }else if (i==1){
                    setLocale("en");
                    openDialogSys();
//                    SettingFragment.this.getActivity().recreate();
                }else if (i==2){
                    setLocale("fr");
                    openDialogSys();
//                    SettingFragment.this.getActivity().recreate();
                }else if (i==3){
                    setLocale("ko");
                    openDialogSys();
//                    SettingFragment.this.getActivity().recreate();
                }
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openDialogSys(){
        final ProgressDialog dialog = new ProgressDialog(SettingFragment.this.getActivity());
        dialog.setMessage("Khởi động lại ứng dụng...");
        dialog.setCancelable(true);
//        dialog.setTitle("");
        dialog.setIcon(R.drawable.ic_info);
        dialog.setMax(100);
        dialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dialog.dismiss();
                        closeSys();
                    }
                }, 3000);
    }

    private void closeSys(){
        Intent mStartActivity = new Intent(getContext(), MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(getContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)getActivity().getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

//    private void getSpinner(View view){
//        ArrayList<ItemsLanguage> itemsLanguageArrayList = new ArrayList<>();
//        itemsLanguageArrayList.add(new ItemsLanguage("Vietnamese"));
//        itemsLanguageArrayList.add(new ItemsLanguage("English"));
//        itemsLanguageArrayList.add(new ItemsLanguage("French"));
//        itemsLanguageArrayList.add(new ItemsLanguage("Korean"));
//
//        SpinnerAdapter spinnerAdapter = new CustomAdapterLanguage(getContext(),itemsLanguageArrayList);
//        pref = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
//        String language = pref.getString("myLanguage","");
//        if (language!=null && spinner != null){
//            spinner.setAdapter(spinnerAdapter);
//            spinner.setOnItemSelectedListener(this);
//
//        }
//
//    }

//    private void checkLanguage(String language){
//        Log.e("stringLanguage", language+"");
//        pref = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
//        String lang = pref.getString("myLanguage","");
//        if (lang!=language){
//            setLocale(lang);
//        }else{
//
//        }
//    }

    private void setLocale(String stringLanguage) {
        Locale locale = new Locale(stringLanguage);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration, getActivity().getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor edit = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        edit.putString("myLanguage", stringLanguage);
        edit.apply();
        edit.commit();
    }
    @SuppressLint("SetTextI18n")
    public void loadLanguageSaved(){
        pref = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("myLanguage","");
        Log.e("lang", language+"");
        setLocale(language);
    }
    private void getLang(View view){
        pref = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = pref.getString("myLanguage","");

        if (language!=null){
            switch (language) {
                case "vi":
                    tvLanguage.setText("Vietnamese");
                    break;
                case "en":
                    tvLanguage.setText("English");
                    break;
                case "fr":
                    tvLanguage.setText("French");
                    break;
                case "ko":
                    tvLanguage.setText("Korean");
                    break;
            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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
    }

//    @Override
//    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        ItemsLanguage itemsLanguage = (ItemsLanguage)adapterView.getSelectedItem();
//        String lg = itemsLanguage.getLanguage();
//        switch (lg) {
//            case "Vietnamese":
//                stringLanguage = "vi-rVN";
//                break;
//            case "English":
//                stringLanguage = "en-rUS";
//                break;
//            case "French":
//                stringLanguage = "fr-rFR";
//                break;
//            case "Korean":
//
//                stringLanguage = "ko";
//                break;
//            default:
//                stringLanguage = "en-rUS";
//                Log.e("Language", stringLanguage + "");
//                break;
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> adapterView) {
//
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
