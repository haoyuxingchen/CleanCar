package com.example.justdoit.wificar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Justdoit on 2018/5/3.
 */

public class SwitchListView extends ArrayAdapter<SwitchAdapter> {
    private int resourceId;
    public SwitchListView(Context context, int textViewResourceId, List<SwitchAdapter> objects){
        super(context,textViewResourceId,objects);
        resourceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        SwitchAdapter s=getItem(position);   //获取当前项的实例
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView text=(TextView)view.findViewById(R.id.tv);
        Switch switch1=(Switch) view.findViewById(R.id.aSwitch);
        text.setText(s.getName());
        switch1.setChecked(s.getOnoff());
        switch1.setTextOff(s.getTextoff());
        switch1.setTextOn(s.getTextOn());
        return view;
    }

}