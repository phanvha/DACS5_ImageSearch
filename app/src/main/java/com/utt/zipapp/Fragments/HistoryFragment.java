package com.utt.zipapp.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.icu.util.LocaleData;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.utt.zipapp.Adapter.RecyclerViewAdapter;
import com.utt.zipapp.CameraActivity;
import com.utt.zipapp.Model.ItemsHistory;
import com.utt.zipapp.R;
import com.utt.zipapp.SQLiteOpenHelper.SQLite;
import com.utt.zipapp.SearchActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SQLite db;
    private ItemsHistory itemsHistory;
    private List<ItemsHistory> itemsHistoryList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;

    private OnFragmentInteractionListener mListener;

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;

    String person_id = null;
    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
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
        final View view = inflater.inflate(R.layout.fragment_history, container, false);

        showListLink(view);

        ImageButton imageButton = (ImageButton) view.findViewById(R.id.deleteHistory);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                openBottomSheet(view);

            }
        });
        return view;
    }

    private void openBottomSheet(View view){
        db = SQLite.getInstance(HistoryFragment.this.getActivity());
        bottomSheetDialog  = new BottomSheetDialog(HistoryFragment.this.getContext(), R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(HistoryFragment.this.getActivity())
                .inflate(
                        R.layout.bt_sheet_layout_del,
                        (LinearLayout) view.findViewById(R.id.bSheetHistoryDel)
                );

        if (db.getCountTotalLinkTB()!=0){
            TextView textView = bottomSheetView.findViewById(R.id.txtCountLink);
            textView.setText("("+ db.getCountTotalLinkTB() +")");
        }else{
            Log.d("SQL", "openBottomSheet: null");
        }

        bottomSheetView.findViewById(R.id.mDeleteAll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (db.getCountTotalLinkTB()!= 0){
                    if (db.deleteAllLink()!= -1){
                        Toast.makeText(HistoryFragment.this.getContext(), getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                        showListLink(view);
                    }
                }else{
                    Toast.makeText(HistoryFragment.this.getContext(), getString(R.string.no_record_found), Toast.LENGTH_SHORT).show();

                }

                bottomSheetDialog.dismiss();

            }
        });


        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    //showListRoute
    private void showListLink(View view){
        db = SQLite.getInstance(HistoryFragment.this.getActivity());
        if (db.getCountTotalLinkTB()!=0){
            itemsHistoryList = null;
            itemsHistoryList = db.getListLink(getIDToken());
            if (itemsHistoryList!=null){
//                Log.d("testssszz",itemsHistoryList.get(0).getId()+", "+itemsHistoryList.remove(0).getPerson_id());
                recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
                recyclerViewAdapter = new RecyclerViewAdapter(itemsHistoryList, HistoryFragment.this.getContext(),getActivity(), view);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
                recyclerViewAdapter.notifyDataSetChanged();
                itemsHistoryList = null;
            }else{
                Log.e("testssszz","Nulllll");
            }



        }else{
            Log.d("getTotalListRouteTB", "= null");
        }
    }
    private String getIDToken() {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(HistoryFragment.this.getContext());

        if (acct != null) {

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            person_id = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            String idtoken = acct.getIdToken();
            String serverAuthCode = acct.getServerAuthCode();

            return  person_id;

        }else{
            return person_id;
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
