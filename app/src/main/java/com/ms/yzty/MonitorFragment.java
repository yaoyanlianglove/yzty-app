package com.ms.yzty;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MonitorFragment extends Fragment {
    private TextView textViewMemeryGear;
    private TextView textViewTyNum;
    private TextView textViewAlarmNum;
    private TextView textViewRebootNum;
    private TextView textViewDeviceStat;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_monitor,container,false);
        textViewMemeryGear = view.findViewById(R.id.textView_memery_gear);
        textViewTyNum = view.findViewById(R.id.textView_ty_num);
        textViewAlarmNum = view.findViewById(R.id.textView_alarm_num);
        textViewRebootNum = view.findViewById(R.id.textView_reboot_num);
        textViewDeviceStat =view.findViewById(R.id.textView_device_stat);
        return view;
    }

        void setData(byte[] buf) {
        String  s= buf[18] + "";
        textViewMemeryGear.setText(s);
        int a,b,c,d;
        if(buf[8] < 0)
            a = buf[8] + 256;
        else
            a = buf[8];
        if(buf[9] < 0)
            b = buf[9] + 256;
        else
            b = buf[9];
        if(buf[10] < 0)
            c= buf[10] + 256;
        else
            c = buf[10];
        if(buf[11] < 0)
            d = buf[11] + 256;
        else
            d = buf[11];
        s = (long)(d + c*256 + b*256*256 + a*256*256*256) + "";
        textViewTyNum.setText(s);

        if(buf[12] < 0)
            a = buf[12] + 256;
        else
            a = buf[12];
        if(buf[13] < 0)
            b = buf[13] + 256;
        else
            b = buf[13];
        s = (long)( b + a*256) + "";
        textViewAlarmNum.setText(s);

        if(buf[14] < 0)
            a = buf[14] + 256;
        else
            a = buf[14];
        if(buf[15] < 0)
            b = buf[15] + 256;
        else
            b = buf[15];
        if( (b + a*256) == 0)
            textViewDeviceStat.setText("正常");
        else
            textViewDeviceStat.setText("告警");

        if(buf[16] < 0)
            a = buf[16] + 256;
        else
            a = buf[16];
        if(buf[17] < 0)
            b = buf[17] + 256;
        else
            b = buf[17];
        s = (long)( b + a*256)+ "";
        textViewRebootNum.setText(s);
    }
}
