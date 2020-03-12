package com.ms.yzty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {


    private MyApplication myApp;
    private int tranType;
    private String ip;
    private int port;
    private MonitorFragment monitorFrag;
    private TelemeterFragment telemeterFrag;
    private ControlFragment controlFrag;
    private ConfigFragment configFrag;
    private Fragment[] fragments;

    private ExecutorService mThreadPool;
    private Handler mHandler;
    private int lastFragment;
    private TextView capacityLabel;
    private TextView capacity;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] readData;
    private int readCount;
    private int deviceId;
    private int revLength;
    private long heartTimeout;
    private int sendDataType;
    private int periodCount;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_main);

        myApp  = (MyApplication)this.getApplication();
        tranType = myApp.getIsTyTr();
        ip = myApp.getIp();
        port = myApp.getPort();

        capacity = findViewById(R.id.textView_capacity);
        capacityLabel = findViewById(R.id.textView_capacity_label);

        readData = new byte[256];
        heartTimeout = System.currentTimeMillis();
        readCount = 0;
        deviceId = 15;
        revLength = 0;
        sendDataType = 0x10;
        myApp.setSendDataType(sendDataType);
        int lastFragment;
        BottomNavigationView bnv;
        lastFragment = 0;
        periodCount = 0;
        myApp.setLastFragment(lastFragment);
        monitorFrag = new MonitorFragment();
        telemeterFrag = new TelemeterFragment();
        controlFrag = new ControlFragment();
        configFrag = new ConfigFragment();
        fragments = new Fragment[]{monitorFrag,telemeterFrag,controlFrag,configFrag};
        bnv = findViewById(R.id.bnv);
        bnv.setLabelVisibilityMode(1);
        getSupportFragmentManager().beginTransaction().replace(R.id.mainView,monitorFrag).show(monitorFrag).commit();
        bnv.setOnNavigationItemSelectedListener(changeFragment);
        initTop(tranType);
        initHandler();
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        receiveData();
        sendData();
    }
    private void initTop(int type)
    {
        if(type == 0)
        {
            capacity.setVisibility(View.INVISIBLE);
            capacityLabel.setVisibility(View.INVISIBLE);
        }
        else
        {
            capacity.setVisibility(View.VISIBLE);
            capacityLabel.setVisibility(View.VISIBLE);
        }
    }
    @SuppressWarnings("CanBeFinal")
    private BottomNavigationView.OnNavigationItemSelectedListener changeFragment=
            new BottomNavigationView.OnNavigationItemSelectedListener()
            {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_monitor:
                            if(myApp.getLastFragment()!=0)
                            {
                                switchFragment(myApp.getLastFragment(),0);
                                lastFragment=0;
                                sendDataType = 0x10;
                                myApp.setSendDataType(sendDataType);
                                myApp.setLastFragment(lastFragment);
                            }
                            return true;
                        case R.id.item_telemeter:
                            if(myApp.getLastFragment()!=1)
                            {
                                switchFragment(myApp.getLastFragment(),1);
                                lastFragment=1;
                                sendDataType = 0x16;
                                myApp.setSendDataType(sendDataType);
                                myApp.setLastFragment(lastFragment);
                            }
                            return true;
                        case R.id.item_control:
                            if(myApp.getLastFragment()!=2)
                            {
                                switchFragment(myApp.getLastFragment(),2);
                                lastFragment=2;
                                sendDataType = 0x10;   //转到此页面默认只发送心跳
                                myApp.setSendDataType(sendDataType);
                                myApp.setLastFragment(lastFragment);
                            }
                            return true;
                        case R.id.item_config:
                            if(myApp.getLastFragment()!=3)
                            {
                                switchFragment(myApp.getLastFragment(),3);
                                lastFragment=3;
                                sendDataType = 0x10; //转到此页面默认只发送心跳
                                myApp.setSendDataType(sendDataType);
                                myApp.setLastFragment(lastFragment);
                            }
                            return true;
                    }
                    return false;
                }
            };
    //切换Fragment
    private void switchFragment(int last,int index)
    {
        FragmentTransaction transaction =getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[last]);//隐藏上个Fragment
        if(!fragments[index].isAdded())
        {
            transaction.add(R.id.mainView,fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }
    @SuppressLint("HandlerLeak")
    private void initHandler() {
        // 实例化主线程,用于更新接收过来的消息
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                switch (msg.what)
                {
                    case 0:
                        Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case 1:  //心跳
                        revDataHandleHeart();
                        break;
                    case 2:  //遥测
                        revDataHandleTelemeter();
                        break;
                    case 3:
                    case 4:
                        if(lastFragment == 3)
                            configFrag.setData(readData);
                        break;
                    case 5:
                        if(lastFragment == 2)
                            controlFrag.setData(readData);
                        break;
                    default:

                }
            }
        };
    }
    private void sendTelemeter() {
        System.out.println("send telemeter  start");
        byte[] writeData = new byte[1024];
        int crc;
        int sendLen = 8;
        writeData[0] = 0x55;
        writeData[1] = (byte) 0xAA;
        writeData[2] = (byte) deviceId;
        writeData[3] = (byte) 0x16;
        writeData[4] = (byte) ((sendLen >> 8) & 0xFF);
        writeData[5] = (byte) (sendLen & 0xFF);

        crc = getCRC16(writeData, sendLen - 2);
        writeData[6] = (byte) ((crc) & 0xFF);
        writeData[7] = (byte) ((crc >> 8) & 0xFF);
        try {
            outputStream = myApp.getSocket().getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData, 0, sendLen);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void revDataHandleTelemeter()
    {
        if(lastFragment == 1)
        {
            telemeterFrag.setData(readData);
        }
    }
    private void createSocket()
    {
        try {
            socket = new Socket();
            SocketAddress socAddress = new InetSocketAddress(ip, port);
            socket.connect(socAddress, 3000);
            myApp.setSocket(socket);
        }
        catch (IOException e) {
            System.out.println("new err");
            e.printStackTrace();
        }
    }
    private void receiveData() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("rev start");
                while (myApp.getLoginStat())
                {
                    if(System.currentTimeMillis() - heartTimeout > 10000)
                    {
                        Message msg = Message.obtain();
                        msg.what = 0;
                        msg.obj = "无法连接设备，请检查网络或者设备！";
                        mHandler.sendMessage(msg);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try
                    {
                        if((socket != null) && (socket.isConnected()) && (!socket.isClosed()))
                        {
                            inputStream = socket.getInputStream();
                            myApp.setInputStream(inputStream);
                            byte[] buffer = new byte[1024];
                            int length = inputStream.read(buffer);
                            if(length > 0)
                            {
                                heartTimeout = System.currentTimeMillis();
                                System.out.println("rev work");
                                dataParse(buffer, length);
                            }
                        }
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                System.out.println("rev end");
            }
        });
    }
    private void dataParse(byte[] buf, int len )
    {
        int i;
        for(i = 0; i < len; i++)
        {
            readData[readCount] = buf[i];
            switch(readCount)
            {
                case 0:
                    if(readData[readCount] == 0x55)
                        readCount++;
                    break;
                case 1:
                    if(readData[readCount] == (0xAA - 256))
                        readCount++;
                    else
                    {
                        readCount = 0;
                    }
                    break;
                case 2:
                    if(readData[readCount] == deviceId)
                        readCount++;
                    else
                        readCount = 0;
                    break;
                case 3:
                    readCount++;
                    break;
                case 4:
                    revLength = readData[readCount] << 8;
                    readCount++;
                    break;
                case 5:
                    revLength = revLength +  readData[readCount];
                    readCount++;
                    break;
                default:
                    if(readCount < revLength - 1)
                        readCount++;
                    else
                        revDataHandle();
                    break;
            }
        }
    }
    private  void revDataHandle()
    {
        int j;
        int calCrc16,revCrc16,temp1,temp2;
        for(j = 0; j < revLength; j++)
        {
            System.out.println(readData[j]);
        }
        if(readData[revLength - 2] < 0)
            temp1 = readData[revLength - 2] + 256;
        else
            temp1= readData[revLength - 2];
        if(readData[revLength - 1] < 0)
            temp2 = readData[revLength - 1] + 256;
        else
            temp2= readData[revLength - 1];
        revCrc16 = temp2*256 + temp1;
        calCrc16 = getCRC16(readData, revLength -2);
        if(revCrc16 == calCrc16)
        {
            int codeType;
            if(readData[3] < 0)
                codeType = readData[3] + 256;
            else
                codeType = readData[3];
            Message msg = Message.obtain();
            switch (codeType)
            {
                case 0x10:
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                    break;
                case 0x16:
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                    break;
                case 0x22:
                    msg.what = 3;
                    mHandler.sendMessage(msg);
                    break;  
                case 0x23:
                    msg.what = 0;
                    msg.obj = "时间设置成功！";
                    mHandler.sendMessage(msg);
                    break; 
                case 0x11:
                    msg.what = 4;
                    mHandler.sendMessage(msg);
                    break;
                case 0x21:
                    msg.what = 0;
                    msg.obj = "参数设置成功！";
                    mHandler.sendMessage(msg);
                    break;  
                case 0x20:
                    msg.what = 0;
                    msg.obj = "模式切换成功！";
                    mHandler.sendMessage(msg);
                    break; 
                case 0x30:
                    msg.what = 5;
                    mHandler.sendMessage(msg);
                    break;   
                case 0x40:
                    int errCode, err0, err1;
                    if(readData[6] < 0)
                        err0 = readData[6] + 256;
                    else
                        err0 = readData[6];
                    if(readData[7] < 0)
                        err1 = readData[7] + 256;
                    else
                        err1 = readData[7];
                    errCode = err0*256 + err1;
                    revDataHandleErr(errCode);
                    break;
            }
        }
        readCount = 0;
        revLength = 0;
    }
    private void revDataHandleHeart()
    {
        TextView curGear = findViewById(R.id.textView_gear);
        String s = readData[6] + "";
        curGear.setText(s);
        TextView textViewMode = findViewById(R.id.textView_mode);
        if(readData[7] == 0)
            textViewMode.setText("自动模式");
        else if(readData[7] == 1)
            textViewMode.setText("遥控模式");
        if(tranType == 1)
        {
            TextView curCap = findViewById(R.id.textView_capacity);
            if(readData[19] == 0)
                curCap.setText("大");
            else if(readData[19] == 1)
                curCap.setText("小");
        }
        if(lastFragment == 0)
        {
            monitorFrag.setData(readData);
        }
    }
    private void revDataHandleErr(int errCode)
    {
        Message msg = Message.obtain();
        msg.what = 0;
        switch (errCode)
        {
            case 401:
                msg.obj = "校验和错误！";
                break;
            case 402:
                msg.obj = "未发送遥控命令！";
                break;
            case 403:
                msg.obj = "参数错误！";
                break;
            case 404:
                msg.obj = "存储故障！";
                break;
            case 405:
                msg.obj = "开关闭锁，拒绝动作！";
                break;
            case 406:
                msg.obj = "档位已最小禁止降档！";
                break;
            case 407:
                msg.obj = "档位已最大禁止升档！";
                break;
            case 408:
                msg.obj = "模式错误！";
                break;
            case 409:
                msg.obj = "校表失败！";
                break;
        }
        mHandler.sendMessage(msg);
    }
    private void sendData() {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                while (myApp.getLoginStat())
                {
                    if(socket == null) {
                        System.out.println("new");
                        createSocket();
                    }
                    else if(!socket.isConnected()) {
                        System.out.println("renew");
                        createSocket();
                    }
                    else
                    {
                        if(periodCount < 60)
                            periodCount++;
                        else
                            periodCount = 0;
                        if(myApp.getSendDataType() == 0x10)
                        {
                            if (periodCount == 20 || periodCount == 50)
                                sendHeart();
                        }
                        else if(myApp.getSendDataType() == 0x16)
                        {
                            if(periodCount == 20)
                                sendHeart();
                            else if(periodCount == 50)
                                sendTelemeter();
                        }
                        else
                        {
                            if(myApp.getSendDataType() == 0x22)
                            {
                                sendGetTime();
                                myApp.setSendDataType(0x00);
                            }
                            else if(myApp.getSendDataType() == 0x23)
                            {
                                sendSetTime();
                                myApp.setSendDataType(0x00);
                            }
                            else if(myApp.getSendDataType() == 0x11)
                            {
                                sendGetConfig();
                                myApp.setSendDataType(0x00);
                            }
                            else if(myApp.getSendDataType() == 0x21)
                            {
                                sendSetConfig();
                                myApp.setSendDataType(0x00);
                            }
                            else if(myApp.getSendDataType() == 0x20)
                            {
                                sendModeControl();
                                myApp.setSendDataType(0x10);
                            }
                            else if(myApp.getSendDataType() == 0x30)
                            {
                                sendMotionControl();
                                myApp.setSendDataType(0x00);
                            }
                            else if(myApp.getSendDataType() == 0x31)
                            {
                                sendMotionConfirm();
                                myApp.setSendDataType(0x10);
                            }
                        }

                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("send  end");
            }
        });
    }
    private void sendMotionConfirm()
    {
        System.out.println("send motion confirm");
        byte[] writeData = new byte[1024];
        byte[] getData;
        getData = myApp.getSendByte();

        int crc;
        int sendLen = 9;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x31;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);

        writeData[6] = getData[0];
        crc = getCRC16(writeData, sendLen-2);
        writeData[sendLen -2] = (byte)((crc) & 0xFF);
        writeData[sendLen -1] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendMotionControl()
    {
        System.out.println("send motion control");
        byte[] writeData = new byte[1024];
        byte[] getData ;
        getData = myApp.getSendByte();

        int crc;
        int sendLen = 9;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x30;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);

        writeData[6] = getData[0];
        crc = getCRC16(writeData, sendLen-2);
        writeData[sendLen -2] = (byte)((crc) & 0xFF);
        writeData[sendLen -1] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendModeControl()
    {
        System.out.println("send mode control");
        byte[] writeData = new byte[1024];
        byte[] getData ;
        getData = myApp.getSendByte();

        int crc;
        int sendLen = 9;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x20;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);

        writeData[6] = getData[0];
        crc = getCRC16(writeData, sendLen-2);
        writeData[sendLen -2] = (byte)((crc) & 0xFF);
        writeData[sendLen -1] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendSetConfig()
    {
        System.out.println("send set config");
        byte[] writeData = new byte[1024];
        byte[] getData;
        getData = myApp.getSendByte();
        int crc,i;
        int sendLen = 38;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x21;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);
        for( i=0; i<30; i++)
            writeData[i+6] = getData[i];

        crc = getCRC16(writeData, sendLen-2);
        writeData[sendLen -2] = (byte)((crc) & 0xFF);
        writeData[sendLen -1] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendGetConfig()
    {
        System.out.println("send get config");
        byte[] writeData = new byte[1024];

        int crc;
        int sendLen = 8;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x11;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);

        crc = getCRC16(writeData, sendLen-2);
        writeData[6] = (byte)((crc) & 0xFF);
        writeData[7] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendSetTime()
    {
        System.out.println("send get time");
        byte[] writeData = new byte[64];
        byte[] getData;
        getData = myApp.getSendByte();
        int crc;
        int sendLen = 20;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x23;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);
        writeData[7] = getData[0];
        writeData[9] = getData[1];
        writeData[11] = getData[2];
        writeData[13] = getData[3];
        writeData[15] = getData[4];
        writeData[17] = getData[5];
        crc = getCRC16(writeData, sendLen-2);
        writeData[sendLen - 2] = (byte)((crc) & 0xFF);
        writeData[sendLen - 1] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendGetTime()
    {
        System.out.println("send get time");
        byte[] writeData = new byte[1024];
        int crc;
        int sendLen = 8;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x22;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);

        crc = getCRC16(writeData, sendLen-2);
        writeData[6] = (byte)((crc) & 0xFF);
        writeData[7] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void sendHeart()
    {
        System.out.println("send heart  start");
        byte[] writeData = new byte[1024];
        int crc;
        int sendLen = 8;
        writeData[0] = 0x55;
        writeData[1] = (byte)0xAA;
        writeData[2] = (byte)deviceId;
        writeData[3] = (byte)0x10;
        writeData[4] = (byte)((sendLen >> 8) & 0xFF);
        writeData[5] = (byte)(sendLen & 0xFF);

        crc = getCRC16(writeData, sendLen-2);
        writeData[6] = (byte)((crc) & 0xFF);
        writeData[7] = (byte)((crc >> 8) & 0xFF);
        try
        {
            outputStream = socket.getOutputStream();
            myApp.setOutputStream(outputStream);
            outputStream.write(writeData,0, sendLen);
            outputStream.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private int getCRC16(byte[] data,int length)
    {
        int regCrc=0xffff;
        int temp;
        int i,j;
        for( i = 0; i<length; i ++)
        {
            temp = data[i];
            if(temp < 0)
                temp += 256;
            temp &= 0xff;
            regCrc^= temp;
            for (j = 0; j<8; j++)
            {
                if ((regCrc & 0x0001) == 0x0001)
                    regCrc=(regCrc>>1)^0xA001;
                else
                    regCrc >>=1;
            }
        }
        return (regCrc&0xffff);
    }
}
