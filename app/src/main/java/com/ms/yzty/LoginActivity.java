package com.ms.yzty;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;


public class LoginActivity extends AppCompatActivity {

    private Button loginBt;
    private EditText ip;
    private EditText pwd;
    private EditText port;
    private Intent intent;
    private MyApplication myApp;
    private RadioGroup tranType;
    private long mExitTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_login);
        loginBt = findViewById(R.id.button_login_in);
        myApp = (MyApplication)this.getApplication();
        intent = new Intent(LoginActivity.this, MainActivity.class);
        pwd = findViewById(R.id.editText_pwd);
        ip = findViewById(R.id.editText_ip);
        port = findViewById(R.id.editText_port);
        tranType = findViewById(R.id.RadioGroup_select);
        loginBt.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                if(!myApp.getLoginStat())
                {
                    final String pwdOk = "123456";
                    final String pwdIn = pwd.getText().toString();
                    String strPort = port.getText().toString();
                    String strIp = ip.getText().toString();

                    if(pwdIn.equals(pwdOk))
                    {
                        if(TextUtils.isEmpty(strPort) ||TextUtils.isEmpty(strIp))
                        {
                            Toast.makeText(LoginActivity.this,"IP地址和端口不能为空！",Toast.LENGTH_LONG ).show();
                        }
                        else
                        {
                            myApp.setLoginStat(true);
                            myApp.setIp(ip.getText().toString());
                            myApp.setPort(Integer.parseInt(port.getText().toString()));
                            if(tranType.getCheckedRadioButtonId() == R.id.radioButton_ty)
                                myApp.setIsTyTr(0);
                            else
                                myApp.setIsTyTr(1);
                            loginBt.setText(R.string.button_login_out);
                            startActivity(intent);
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"密码错误！", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    myApp.setLoginStat(false);
                    closeTCP();
                    loginBt.setText(R.string.button_login_in);
                }

            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 1500) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }
            else
            {
                closeTCP();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void closeTCP()
    {
        try
        {
            if (myApp.getInputStream() != null) {
                myApp.getInputStream().close();
            }
            if (myApp.getOutputStream() != null) {
                myApp.getOutputStream().close();
            }
            if (myApp.getSocket() != null) {
                myApp.getSocket().close();
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
