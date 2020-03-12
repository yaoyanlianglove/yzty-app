package com.ms.yzty;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ConfigFragment extends Fragment {

    private EditText timeEdit;
    private TimePickerView timePick;
    private MyApplication myApp;

    private EditText downTh;
    private EditText upTh;
    private EditText motionSpace;
    private EditText tyDelay;
    private EditText ctRatio;
    private EditText overCurrent;
    private EditText lockUpTh;
    private EditText lockDownTh;
    private EditText lockHigh;
    private EditText lockLow;
    private EditText trTh;
    private EditText trDelay;
    private EditText alarmTempTh;
    private EditText tranCapacity;
    private EditText deviceCode;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_config,container,false);
        downTh       = view.findViewById(R.id.editText_down_th);
        upTh         = view.findViewById(R.id.editText_up_th);
        motionSpace  = view.findViewById(R.id.editText_motion_space);
        tyDelay      = view.findViewById(R.id.editText_ty_delay);
        ctRatio      = view.findViewById(R.id.editText_ct_ratio);
        overCurrent  = view.findViewById(R.id.editText_over_current);
        lockUpTh     = view.findViewById(R.id.editText_lock_up_th);
        lockDownTh   = view.findViewById(R.id.editText_lock_down_th);
        lockHigh     = view.findViewById(R.id.editText_lock_high);
        lockLow      = view.findViewById(R.id.editText_lock_low);
        trTh         = view.findViewById(R.id.editText_tr_th);
        trDelay      = view.findViewById(R.id.editText_tr_delay);
        alarmTempTh  = view.findViewById(R.id.editText_alarm_temp_th);
        tranCapacity     = view.findViewById(R.id.editText_tran_cap);
        deviceCode   = view.findViewById(R.id.editText_device_code);
        myApp = (MyApplication)mActivity.getApplication();

        timeEdit = view.findViewById(R.id.editText_time);
        timeEdit.setOnClickListener(new EditText.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                timePick.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                timePick.show();
            }
        });
        timePick = new TimePickerView.Builder(this.getContext(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                timeEdit.setText(getTime(date));
            }
        }).build();
        Button timeGetBtn;
        Button timeSetBtn;
        Button configGetBtn;
        Button configSetBtn;
        timeGetBtn = view.findViewById(R.id.button_time_get);
        timeGetBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myApp.setSendDataType(0x22);
            }
        });
        timeSetBtn = view.findViewById(R.id.button_time_set);
        timeSetBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(timeEdit.getText().toString()))
                {
                    Toast.makeText(mActivity, "请选择时间", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    byte[] timeData = new byte[64];
                    String times = timeEdit.getText().toString();
                    timeData[0] = (byte)Integer.parseInt(times.substring(0, 2));
                    timeData[1] = (byte)Integer.parseInt(times.substring(3, 5));
                    timeData[2] = (byte)Integer.parseInt(times.substring(6, 8));
                    timeData[3] = (byte)Integer.parseInt(times.substring(9, 11));
                    timeData[4] = (byte)Integer.parseInt(times.substring(12, 14));
                    timeData[5] = (byte)Integer.parseInt(times.substring(15, 17));
                    myApp.setSendByte(timeData);
                    myApp.setSendDataType(0x23);
                }
            }
        });
        configGetBtn = view.findViewById(R.id.button_config_get);
        configGetBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myApp.setSendDataType(0x11);
            }
        });
        configSetBtn = view.findViewById(R.id.button_config_set);
        configSetBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                byte[] configData = new byte[64];
                String s = upTh.getText().toString();
                if (TextUtils.isEmpty(s) || numberStat(s))
                {
                    Toast.makeText(mActivity, "降压阈值参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[0] = (byte)(Integer.parseInt(s) >> 8);
                configData[1] = (byte)(Integer.parseInt(s) & 0xFF);
                s = downTh.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "升压阈值参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[2] = (byte)(Integer.parseInt(s) >> 8);
                configData[3] = (byte)(Integer.parseInt(s) & 0xFF);

                s = motionSpace.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "动作间隔参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[4] = (byte)(Integer.parseInt(s) >> 8);
                configData[5] = (byte)(Integer.parseInt(s) & 0xFF);
                s = tyDelay.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "调压延时参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[6] = (byte)(Integer.parseInt(s) >> 8);
                configData[7] = (byte)(Integer.parseInt(s) & 0xFF);
                s = ctRatio.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "CT变比参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[8] = (byte)(Integer.parseInt(s) >> 8);
                configData[9] = (byte)(Integer.parseInt(s) & 0xFF);
                s = overCurrent.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "过流保护参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[10] = (byte)(Integer.parseInt(s) >> 8);
                configData[11] = (byte)(Integer.parseInt(s) & 0xFF);
                s = lockUpTh.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "闭锁调压上限参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[12] = (byte)(Integer.parseInt(s) >> 8);
                configData[13] = (byte)(Integer.parseInt(s) & 0xFF);
                s = lockDownTh.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "闭锁调压下限参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[14] = (byte)(Integer.parseInt(s) >> 8);
                configData[15] = (byte)(Integer.parseInt(s) & 0xFF);
                s = lockHigh.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "高压保护参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[16] = (byte)(Integer.parseInt(s) >> 8);
                configData[17] = (byte)(Integer.parseInt(s) & 0xFF);
                s = lockLow.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "低压保护参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[18] = (byte)(Integer.parseInt(s) >> 8);
                configData[19] = (byte)(Integer.parseInt(s) & 0xFF);
                s = trTh.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "调容阈值参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[20] = (byte)(Integer.parseInt(s) >> 8);
                configData[21] = (byte)(Integer.parseInt(s) & 0xFF);
                s = trDelay.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "调容延时参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[22] = (byte)(Integer.parseInt(s) >> 8);
                configData[23] = (byte)(Integer.parseInt(s) & 0xFF);
                s = alarmTempTh.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "告警温度阈值参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[24] = (byte)(Integer.parseInt(s) >> 8);
                configData[25] = (byte)(Integer.parseInt(s) & 0xFF);
                s = tranCapacity.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "变压器容量参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[26] = (byte)(Integer.parseInt(s) >> 8);
                configData[27] = (byte)(Integer.parseInt(s) & 0xFF);
                s = deviceCode.getText().toString();
                if (TextUtils.isEmpty(s)|| numberStat(s))
                {
                    Toast.makeText(mActivity, "设备编码参数不能为空，或者参数中有非法字符", Toast.LENGTH_SHORT).show();
                    return;
                }
                configData[28] = (byte)(Integer.parseInt(s) >> 8);
                configData[29] = (byte)(Integer.parseInt(s) & 0xFF);
                myApp.setSendByte(configData);
                myApp.setSendDataType(0x21);
            }
        });
        return view;
    }

    private static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return format.format(date);
    }
    void setData(byte[] buf)
    {
        String s;
        int a,b;
        switch(buf[3])
        {
            case 0x11:
                a = buf[6];
                if(buf[7] < 0)
                    b = buf[7] + 256;
                else
                    b = buf[7];
                s = (a*256 + b) + "";
                upTh.setText(s);
                a = buf[8];
                if(buf[9] < 0)
                    b = buf[9] + 256;
                else
                    b = buf[9];
                s = (a*256 + b) + "";
                downTh.setText(s);
                a = buf[10];
                if(buf[11] < 0)
                    b = buf[11] + 256;
                else
                    b = buf[11];
                s = (a*256 + b) + "";
                motionSpace.setText(s);
                a = buf[12];
                if(buf[13] < 0)
                    b = buf[13] + 256;
                else
                    b = buf[13];
                s = (a*256 + b) + "";
                tyDelay.setText(s);
                a = buf[14];
                if(buf[15] < 0)
                    b = buf[15] + 256;
                else
                    b = buf[15];
                s = (a*256 + b) + "";
                ctRatio.setText(s);
                a = buf[16];
                if(buf[17] < 0)
                    b = buf[17] + 256;
                else
                    b = buf[17];
                s = (a*256 + b) + "";
                overCurrent.setText(s);
                a = buf[18];
                if(buf[19] < 0)
                    b = buf[19] + 256;
                else
                    b = buf[19];
                s = (a*256 + b) + "";
                lockUpTh.setText(s);
                a = buf[20];
                if(buf[21] < 0)
                    b = buf[21] + 256;
                else
                    b = buf[21];
                s = (a*256 + b) + "";              
                lockDownTh.setText(s);
                a = buf[22];
                if(buf[23] < 0)
                    b = buf[23] + 256;
                else
                    b = buf[23];
                s = (a*256 + b) + "";
                lockHigh.setText(s);
                a = buf[24];
                if(buf[25] < 0)
                    b = buf[25] + 256;
                else
                    b = buf[25];
                s = (a*256 + b) + "";
                lockLow.setText(s);
                a = buf[26];
                if(buf[27] < 0)
                    b = buf[27] + 256;
                else
                    b = buf[27];
                s = (a*256 + b) + "";
                trTh.setText(s);
                a = buf[28];
                if(buf[29] < 0)
                    b = buf[29] + 256;
                else
                    b = buf[29];
                s = (a*256 + b) + "";
                trDelay.setText(s);
                a = buf[30];
                if(buf[31] < 0)
                    b = buf[31] + 256;
                else
                    b = buf[31];
                s = (a*256 + b) + "";
                alarmTempTh.setText(s);

                a = buf[32];
                if(buf[33] < 0)
                    b = buf[33] + 256;
                else
                    b = buf[33];
                s = (a*256 + b) + "";
                tranCapacity.setText(s);

                a = buf[34];
                if(buf[35] < 0)
                    b = buf[35] + 256;
                else
                    b = buf[35];
                s = (a*256 + b) + "";
                deviceCode.setText(s);
                break;
 
            case 0x22:
                int a1,a2,a3,a4,a5,a6;
                if(buf[7] < 0)
                    a1 = buf[7] + 256;
                else
                    a1 = buf[7];

                if(buf[9] < 0)
                    a2 = buf[9] + 256;
                else
                    a2 = buf[9];

                if(buf[11] < 0)
                    a3 = buf[11] + 256;
                else
                    a3 = buf[11];

                if(buf[13] < 0)
                    a4= buf[13] + 256;
                else
                    a4 = buf[13];

                if(buf[15] < 0)
                    a5 = buf[15] + 256;
                else
                    a5 = buf[15];

                if(buf[17] < 0)
                    a6 = buf[17] + 256;
                else
                    a6 = buf[17];
                Calendar calendar = Calendar.getInstance();
                calendar.set(a1,a2,a3,a4,a5,a6);
                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.ENGLISH);
                s=sdf.format(calendar.getTime());
                timeEdit.setText(s);
                break;
        }
    }
    private  boolean numberStat(String str)
    {
        int i;
        boolean res = false;
        for(i=0; i<str.length();i++)
        {
            int chr=str.charAt(i);
            if(chr<48 || chr>57)
            {
                res = true;
                break;
            }
        }
        return res;
    }
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        this.mActivity = (Activity)context;
    }
}
