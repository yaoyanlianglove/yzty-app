package com.ms.yzty;

import android.app.Application;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MyApplication extends Application {
    private String ip;
    private int port;
    private int isTyTr;
    private boolean loginStat;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private int lastFragment;
    private int sendDataType;
    private byte[] sendByte;

    public void setSendByte(byte[] i)
    {
        sendByte = i;
    }
    public byte[] getSendByte()
    {
        return sendByte;
    }
    public void setSendDataType(int i)
    {
        sendDataType = i;
    }
    public int getSendDataType(){return sendDataType;}
    public void setLastFragment(int i)
    {
        lastFragment = i;
    }
    public int getLastFragment(){return lastFragment;}
    public void setIp(String s)
    {
        ip = s;
    }
    public String getIp(){return ip;}
    public void setPort(int i)
    {
        port = i;
    }
    public int getPort(){return port;}
    public void setIsTyTr(int i)
    {
        isTyTr = i;
    }
    public boolean getLoginStat(){return loginStat;}
    public void setLoginStat(boolean i)
    {
        loginStat = i;
    }
    public int getIsTyTr(){return isTyTr;}
    public Socket getSocket(){return socket;}
    public void setSocket(Socket s){socket = s;}

    public InputStream getInputStream(){return inputStream;}
    public void setInputStream(InputStream in){inputStream = in;}
    public OutputStream getOutputStream(){return outputStream;}
    public void setOutputStream(OutputStream out){outputStream = out;}

    public void onCreate() {
        super.onCreate();
        loginStat = false;
        socket    = null;
    }
}
