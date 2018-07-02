package com.example.shrey.attendance.Activity;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.shrey.attendance.Pojo.DetailPojo;
import com.example.shrey.attendance.R;

import java.util.List;

/**
 * Created by Shrey on 6/20/2018.
 */

public class AttendanceDetailsAdapter extends ArrayAdapter<DetailPojo> {
    private Context mcontext;
    private int mresource;
public AttendanceDetailsAdapter(@NonNull Context context, int resource, @NonNull List<DetailPojo> objects) {
        super(context, resource, objects);
        mcontext = context;
        mresource = resource;
  }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String date = getItem(position).getDate();
        String value = getItem(position).getValue();
        String time = getItem(position).getTime();
        Log.v("tag", "DATE,VALUE,LEAVES" + date + "," + value);
        DetailPojo detailPojo = new DetailPojo(date, value,time);
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mresource, parent, false);
        TextView tvDate = convertView.findViewById(R.id.tvdate);
        TextView tvValue = convertView.findViewById(R.id.tvvalue);
        TextView tvTime = convertView.findViewById(R.id.tvtime);
        int darkgreen=getContext().getResources().getColor(R.color.darkgreen);
        tvDate.setText(date);
        tvValue.setText(value);
        tvTime.setText(time);
        if (value.matches("Late") && Integer.parseInt(time.substring(0, 2)) <= 14){
                tvDate.setTextColor(Color.RED);
                tvValue.setTextColor(Color.RED);
                tvTime.setTextColor(Color.RED);
            } else if (value.matches("Late") && Integer.parseInt(time.substring(0, 2)) >14){
                tvDate.setTextColor(darkgreen);
                tvValue.setTextColor(darkgreen);
                tvTime.setTextColor(darkgreen);
            }
            return convertView;

    }
}
