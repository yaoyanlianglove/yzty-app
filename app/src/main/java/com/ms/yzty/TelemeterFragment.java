package com.ms.yzty;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;




class TelemeterFragment extends Fragment {
    private TextView textViewPhaseVa;
    private TextView textViewPhaseVb;
    private TextView textViewPhaseVc;
    private TextView textViewPhaseIa;
    private TextView textViewPhaseIb;
    private TextView textViewPhaseIc;
    private TextView textViewPhasePa;
    private TextView textViewPhasePb;
    private TextView textViewPhasePc;
    private TextView textViewPhaseQa;
    private TextView textViewPhaseQb;
    private TextView textViewPhaseQc;
    private TextView textViewPf;
    private TextView textViewFreq;
    private TextView textViewDeviceTemp;
    private TextView textViewOilTemp;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view=inflater.inflate(R.layout.fragment_telemeter,container,false);
        textViewPhaseVa=view.findViewById(R.id.textView_phase_va);
        textViewPhaseVb=view.findViewById(R.id.textView_phase_vb);
        textViewPhaseVc=view.findViewById(R.id.textView_phase_vc);
        textViewPhaseIa=view.findViewById(R.id.textView_phase_ia);
        textViewPhaseIb=view.findViewById(R.id.textView_phase_ib);
        textViewPhaseIc=view.findViewById(R.id.textView_phase_ic);
        textViewPhasePa=view.findViewById(R.id.textView_phase_pa);
        textViewPhasePb=view.findViewById(R.id.textView_phase_pb);
        textViewPhasePc=view.findViewById(R.id.textView_phase_pc);
        textViewPhaseQa=view.findViewById(R.id.textView_phase_qa);
        textViewPhaseQb=view.findViewById(R.id.textView_phase_qb);
        textViewPhaseQc=view.findViewById(R.id.textView_phase_qc);
        textViewPf     =view.findViewById(R.id.textView_pf);
        textViewFreq   =view.findViewById(R.id.textView_freq);
        textViewDeviceTemp=view.findViewById(R.id.textView_device_tmp);
        textViewOilTemp   =view.findViewById(R.id.textView_oil_temp);
        return view;
    }
    void setData(byte[] buf) {
        int a,b;
        String s;
        a = buf[6];
        if(buf[7] < 0)
            b = buf[7] + 256;
        else
            b = buf[7];
        s = (float)(a*256 + b)/10 + "       ";
        textViewPhaseVa.setText(s);
        a = buf[8];
        if(buf[9] < 0)
            b = buf[9] + 256;
        else
            b = buf[9];
        s = (float)(a*256 + b)/10 + "       ";
        textViewPhaseVb.setText(s);
        a = buf[10];
        if(buf[11] < 0)
            b = buf[11] + 256;
        else
            b = buf[11];
        s = (float)(a*256 + b)/10 + "       ";
        textViewPhaseVc.setText(s);
        a = buf[12];
        if(buf[13] < 0)
            b = buf[13] + 256;
        else
            b = buf[13];
        s = (float)(a*256 + b)/1000 + "       ";
        textViewPhaseIa.setText(s);
        a = buf[14];
        if(buf[15] < 0)
            b = buf[15] + 256;
        else
            b = buf[15];
        s = (float)(a*256 + b)/1000 + "       ";
        textViewPhaseIb.setText(s);
        a = buf[16];
        if(buf[17] < 0)
            b = buf[17] + 256;
        else
            b = buf[17];
        s = (float)(a*256 + b)/1000 + "       ";
        textViewPhaseIc.setText(s);
        a = buf[18];
        if(buf[19] < 0)
            b = buf[19] + 256;
        else
            b = buf[19];
        s = (float)(a*256 + b) + "       ";
        textViewPhasePa.setText(s);
        a = buf[20];
        if(buf[21] < 0)
            b = buf[21] + 256;
        else
            b = buf[21];
        s = (float)(a*256 + b) + "       ";
        textViewPhasePb.setText(s);
        a = buf[22];
        if(buf[23] < 0)
            b = buf[23] + 256;
        else
            b = buf[23];
        s = (float)(a*256 + b) + "       ";
        textViewPhasePc.setText(s);
        a = buf[24];
        if(buf[25] < 0)
            b = buf[25] + 256;
        else
            b = buf[25];
        s = (float)(a*256 + b) + "       ";
        textViewPhaseQa.setText(s);
        a = buf[26];
        if(buf[27] < 0)
            b = buf[27] + 256;
        else
            b = buf[27];
        s = (float)(a*256 + b) + "       ";
        textViewPhaseQb.setText(s);
        a = buf[28];
        if(buf[29] < 0)
            b = buf[29] + 256;
        else
            b = buf[29];
        s = (float)(a*256 + b) + "       ";
        textViewPhaseQc.setText(s);
        a = buf[30];
        if(buf[31] < 0)
            b = buf[31] + 256;
        else
            b = buf[31];
        s = (float)(a*256 + b)/100 + "       ";
        textViewPf.setText(s);

        if(buf[32] < 0)
            a = buf[32] + 256;
        else
            a = buf[32];
        if(buf[33] < 0)
            b = buf[33] + 256;
        else
            b = buf[33];
        s = (float)(a*256 + b)/100 + "       ";
        textViewFreq.setText(s);

        a = buf[34];
        if(buf[35] < 0)
            b = buf[35] + 256;
        else
            b = buf[35];
        s = (float)(a*256 + b)/10 + "       ";
        textViewDeviceTemp.setText(s);

        a = buf[36];
        if(buf[37] < 0)
            b = buf[37] + 256;
        else
            b = buf[37];
        s = (float)(a*256 + b)/10 + "       ";
        textViewOilTemp.setText(s);
    }

}
