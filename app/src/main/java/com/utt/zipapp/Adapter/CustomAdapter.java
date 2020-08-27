package com.utt.zipapp.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.utt.zipapp.Fragments.HomeFragment;
import com.utt.zipapp.Model.CustomItemsSpinner;
import com.utt.zipapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomAdapter extends ArrayAdapter<CustomItemsSpinner> {

    public CustomAdapter(@NonNull Context context, @NonNull List<CustomItemsSpinner> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }
        CustomItemsSpinner customItemsSpinner = getItem(position);
        ImageView spinnerImage = convertView.findViewById(R.id.imgSpinner);
        TextView spinnerTitle = convertView.findViewById(R.id.txtSpinnerTitle);

        if (customItemsSpinner!=null){
            spinnerImage.setImageResource(customItemsSpinner.getSpinnerImage());
            spinnerTitle.setText(customItemsSpinner.getSpinnerTitle());
        }

        return convertView;
    }
}


//    public CustomAdapter(@NonNull HomeFragment context, ArrayList<CustomItemsSpinner> customListItems) {
//        super(context, 0, customListItems);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return customView(position, convertView, parent);
//    }
//
//    @Override
//    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return customView(position, convertView, parent);
//    }
//
//    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
//        if (convertView!=null){
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
//        }
//        CustomItemsSpinner customItemsSpinner = getItem(position);
//        ImageView spinnerImage = convertView.findViewById(R.id.imgSpinner);
//        TextView spinnerTitle = convertView.findViewById(R.id.txtSpinnerTitle);
//
//        if (customItemsSpinner!=null){
//            spinnerImage.setImageResource(customItemsSpinner.getSpinnerImage());
//            spinnerTitle.setText(customItemsSpinner.getSpinnerTitle());
//        }
//
//        return convertView;
//
//    }
//}
