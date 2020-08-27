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

import com.utt.zipapp.Model.ItemsLanguage;
import com.utt.zipapp.R;

import java.util.List;

public class CustomAdapterLanguage extends ArrayAdapter<ItemsLanguage> {

    public CustomAdapterLanguage(@NonNull Context context, @NonNull List<ItemsLanguage> objects) {
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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custome_spinner_language, parent, false);
        }
        ItemsLanguage itemsLanguage = getItem(position);
        TextView spinnerTitle = convertView.findViewById(R.id.txtSpinnerLanguage);

        if (itemsLanguage!=null){
            spinnerTitle.setText(itemsLanguage.getLanguage());
        }

        return convertView;
    }
}