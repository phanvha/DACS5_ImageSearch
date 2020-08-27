package com.utt.zipapp.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.utt.zipapp.CameraActivity;
import com.utt.zipapp.Fragments.HistoryFragment;
import com.utt.zipapp.Model.ItemsHistory;
import com.utt.zipapp.R;
import com.utt.zipapp.SQLiteOpenHelper.SQLite;
import com.utt.zipapp.SearchActivity;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    private List<ItemsHistory> dataList = new ArrayList<>();
    Context context;
    private Boolean aBoolean = true;
    boolean check = false;
    Activity activity;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    SQLite db;
    View v;
    Adapter adapter;

    public RecyclerViewAdapter(List<ItemsHistory> data, Context context, Activity activity, View view ) {
        this.dataList = data;
        this.context = context;
        this.activity = activity;
        this.v = view;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_list, parent, false);
        return new RecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.txtlink.setText( dataList.get(position).getLink());
        holder.txtDatetime.setText(dataList.get(position).getDatetime());
        holder.txtlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchActivity.class);
                Log.d("txtLink", dataList.get(position).getLink());
                intent.putExtra("url", dataList.get(position).getLink());
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                notifyDataSetChanged();

            }
        });
        holder.txtlink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                bottomSheetDialog  = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                bottomSheetView = LayoutInflater.from(activity)
                        .inflate(
                                R.layout.layout_bottom_sheet_history,
                                (LinearLayout) v.findViewById(R.id.bottomSheetHistory)
                        );
                bottomSheetView.findViewById(R.id.mCopy).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setClipboard(context,dataList.get(position).getLink());
                        bottomSheetDialog.dismiss();

                    }
                });
                bottomSheetView.findViewById(R.id.mDelete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db = SQLite.getInstance(activity);
                        if (db.deleteSimpleLink(dataList.get(position).getId()) != -1) {
                            Toast.makeText(activity.getApplicationContext(),""+activity.getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                            dataList.remove(position);
                            notifyDataSetChanged();
                            bottomSheetDialog.dismiss();

                        } else {
                            Toast.makeText(activity.getApplicationContext(), ""+activity.getString(R.string.can_not_delete_link), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                return true;
            }
        });
    }

    public void updateList(List<ItemsHistory> list){
        dataList.clear();
        dataList.addAll(list);
        this.notifyDataSetChanged();
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(activity.getApplicationContext(), ""+text, Toast.LENGTH_SHORT).show();
        }
    }

    //open bottom dialog
    private void openBottomDialog(View view){
        bottomSheetDialog  = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        bottomSheetView = LayoutInflater.from(activity)
                .inflate(
                        R.layout.layout_bottom_sheet,
                        (LinearLayout) view.findViewById(R.id.bottomSheetLayout)
                );

        LinearLayout linearCopy = bottomSheetView.findViewById(R.id.mCopy);
        LinearLayout linearDelete = bottomSheetView.findViewById(R.id.mDelete);
        linearCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        linearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }
//    private void ShowDialog(){
//        dialog = new Dialog(RecyclerViewAdapter.this.context);
//        dialog.setTitle("Thông tin trạm dừng");
//        dialog.setContentView(R.layout.dialoginfo);
//        dialog.show();
//
//    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtlink, txtDatetime;

        public RecyclerViewHolder(final View itemView) {
            super(itemView);
            txtlink = (TextView)itemView.findViewById(R.id.txtLink);
            txtDatetime = (TextView)itemView.findViewById(R.id.txtDateTime);


        }
    }
}
