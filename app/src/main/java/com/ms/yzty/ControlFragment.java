package com.ms.yzty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ControlFragment extends Fragment {
    private RadioGroup modeSelect;
    private RadioGroup controlSelect;

    private MyApplication myApp;
    private Activity mActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_control,container,false);
        modeSelect=view.findViewById(R.id.radioGroup_mode);
        controlSelect =view.findViewById(R.id.radioGroup_control);
        RadioButton trBtn;
        Button modeBtn;
        Button controlBtn;
        modeBtn = view.findViewById(R.id.button_switch_mode);
        controlBtn = view.findViewById(R.id.button_select);
        myApp = (MyApplication)mActivity.getApplication();
        trBtn = view.findViewById(R.id.radioButton_tr);
        if(myApp.getIsTyTr() == 0)
        {
            trBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            trBtn.setVisibility(View.VISIBLE);
        }
        modeBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                byte[] controlData = new byte[64];
                if(modeSelect.getCheckedRadioButtonId() == R.id.radioButton_mode_auto)
                {
                    controlData[0] = 0x00;
                    myApp.setSendByte(controlData);
                    myApp.setSendDataType(0x20);
                }
                else if(modeSelect.getCheckedRadioButtonId() == R.id.radioButton_mode_remote)
                {
                    controlData[0] = 0x01;
                    myApp.setSendByte(controlData);
                    myApp.setSendDataType(0x20);
                }
                else
                {
                    Toast.makeText(mActivity, "请选择一个模式", Toast.LENGTH_SHORT).show();
                }
            }
        });
        controlBtn.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                byte[] controlData = new byte[64];
                if(controlSelect.getCheckedRadioButtonId() == R.id.radioButton_ty_up)
                {
                    controlData[0] = 0x01;
                    myApp.setSendByte(controlData);
                    myApp.setSendDataType(0x30);
                }
                else if(controlSelect.getCheckedRadioButtonId() == R.id.radioButton_ty_down)
                {
                    controlData[0] = 0x02;
                    myApp.setSendByte(controlData);
                    myApp.setSendDataType(0x30);
                }
                else if(controlSelect.getCheckedRadioButtonId() == R.id.radioButton_tr)
                {
                    controlData[0] = 0x03;
                    myApp.setSendByte(controlData);
                    myApp.setSendDataType(0x30);
                }
                else
                {
                    Toast.makeText(mActivity, "请选择一个动作", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);
        this.mActivity = (Activity)context;
    }

    void setData(byte[] buf) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("提示");
        if(buf[6] == 0x01)
            builder.setMessage("确定升档吗？");
        else if(buf[6] == 0x02)
            builder.setMessage("确定降档吗？");
        else if(buf[6] == 0x03)
            builder.setMessage("确定调容吗？");

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                byte[] controlData = new byte[64];
                controlData[0] =(byte)0xFF;
                myApp.setSendByte(controlData);
                myApp.setSendDataType(0x31);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                byte[] controlData = new byte[64];
                controlData[0] = 0x00;
                myApp.setSendByte(controlData);
                myApp.setSendDataType(0x31);
            }
        });
        builder.show();
    }
}
