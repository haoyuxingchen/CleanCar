package com.example.justdoit.wificar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import static android.text.InputType.TYPE_CLASS_PHONE;

public class MainActivity extends welcome {

    Button connectbutton21;//连接服务器
    ProgressBar progressBar21;//进度条
    EditText iPEditText;//IP地址
    EditText portEditText;//端口号
    TextView titleEditText;//"连接"


    boolean ConnectFlage=true;
    int ShowPointSum=0;//显示点的数量,连接中.....(后面的点)

    OutputStream outputStream=null;//定义输出流
    InputStream inputStream=null;//定义输入流

    private SharedPreferences.Editor editor;//存储数据


    Intent intentcontrol = new Intent(); //界面跳转;//控制界面

    long exitTime=0;

    //boolean ReadDataFlage=false;//接收数据标志
    byte[] Readbyte = new byte[1024];
    int ReadbyteLen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        starthandle=false;//关闭线程
        iswelcom=false;//继承的其它不必要操作关闭

        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme);//设置主题风格
        setContentView(R.layout.activity_main);

        exitTime = System.currentTimeMillis();//返回退出记录值

        connectbutton21 = (Button)findViewById(R.id.button21);//连接服务器按钮
        //buttonExit = (Button) findViewById(R.id.buttonExit);//获取按钮
        progressBar21 = (ProgressBar)findViewById(R.id.progressBar21);//进度条
        progressBar21.setVisibility(View.INVISIBLE);//进度条不显示
        iPEditText = (EditText)findViewById(R.id.editText21);//IP地址
        iPEditText.setInputType(TYPE_CLASS_PHONE);//只显示数字和点
        portEditText = (EditText)findViewById(R.id.editText22);//端口号
        portEditText.setInputType(TYPE_CLASS_PHONE);//只显示数字和点
        titleEditText = (TextView)findViewById(R.id.textView21);//"连接"

        connectbutton21.setOnClickListener(connectbutton21Click);//对话框连接按钮点击事件
        connectbutton21.setOnTouchListener(connectbutton21Touch);//按钮的触摸事件
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean issave = sharedPreferences.getBoolean("save", false);//得到save文件存的值，得不到会返回false
        if (issave)
        {
            String Ipstring = sharedPreferences.getString("Ipstring", "192.168.4.1");//取出ip,不存在返回192.168.4.1
            String portint = sharedPreferences.getString("portint", "8888");//取出端口号,不存在返回8888
            iPEditText.setText(Ipstring);
            portEditText.setText(portint);
        }

              Log.e("MainActivity", "socket="+socket);
              titleEditText.setText("连接");

    }




    /***
     * 对话框连接按钮点击事件
     */
    private OnClickListener connectbutton21Click = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Ipstring = iPEditText.getText().toString().replace(" ", "");
            portint = Integer.valueOf(portEditText.getText().toString().replace(" ", ""));
            progressBar21.setVisibility(View.VISIBLE);//显示进度条
            tcpClientCountDownTimer.cancel();
            tcpClientCountDownTimer.start();
            ConnectFlage = true;
            ShowPointSum = 0;

            //ReadDataFlage = false;
            ConnectSeverThread connectSeverThread = new ConnectSeverThread();
            connectSeverThread.start();

            editor = sharedPreferences.edit();
            editor.putString("Ipstring", Ipstring);//记录ip
            editor.putString("portint", portEditText.getText().toString());//记录端口号
            editor.putBoolean("save", true);//写入记录标志
            editor.commit();
        }
    };


    /***
     *
     * @author 连接服务器任务
     *
     */
    class ConnectSeverThread extends Thread
    {
        public void run()
        {
            while(ConnectFlage)
            {
                try
                {
                    ipAddress = InetAddress.getByName(Ipstring);
                    socket = new Socket(ipAddress, portint);
                    Log.e("Main", "点击连接"+socket);
                    ConnectFlage = false;
                    tcpClientCountDownTimer.cancel();
                    runOnUiThread(new Runnable()
                    {
                        public void run()
                        {
                            progressBar21.setVisibility(View.INVISIBLE);//关闭滚动条
                            //AlertDialog21.cancel();//关闭提示框
                        }
                    });
                    inputStream = socket.getInputStream();//获取输入流

                    //ReadDataFlage = true;

                    //ReadDataThread readDataThread = new ReadDataThread();
                   // readDataThread.start();

//                    intentcontrol.setClass(MainActivity.this, Control.class);
//                    MainActivity.this.startActivity(intentcontrol);

                    fromwelcom=false;
                    Intent intent = new Intent(getApplication(),  Control.class);
                   //它把数据压进了一个叫做Bundle的东西里面，里面是键值对，取的时候特别好取
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("fromwelcom",fromwelcom);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }
            }
        }
    }


    /***
     * 延时3s的定时器
     */
    private CountDownTimer tcpClientCountDownTimer = new CountDownTimer(3000,200) {
        @Override
        public void onTick(long millisUntilFinished) {//每隔200ms进入
            if (ConnectFlage)
            {	ShowPointSum ++;
                switch (ShowPointSum%9)
                {   case 0:titleEditText.setText("连接中");break;
                    case 1:titleEditText.setText("连接中.");break;
                    case 2:titleEditText.setText("连接中..");break;
                    case 3:titleEditText.setText("连接中...");break;
                    case 4:titleEditText.setText("连接中....");break;
                    case 5:titleEditText.setText("连接中.....");break;
                    case 6:titleEditText.setText("连接中......");break;
                    case 7:titleEditText.setText("连接中.......");break;
                    case 8:titleEditText.setText("连接中........");break;
                    default:
                        break;
                }
            }
        }
        @Override
        public void onFinish() {//3s后进入(没有取消定时器的情况下)
            if (ConnectFlage)
            {	ConnectFlage = false;
                progressBar21.setVisibility(View.INVISIBLE);//关闭滚动条
                titleEditText.setText("连接服务器失败!!");
            }
            tcpClientCountDownTimer.cancel();
        }
    };


    /***
     * 手机返回按钮
     */
    public boolean onKeyDown(int keyCode,KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000)
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                try
                {
                    if (socket!=null)
                    {
                        socket.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (outputStream!=null)
                    {
                        outputStream.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (inputStream!=null)
                    {
                        inputStream.close();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                finish();
            }
            return false;
        }
        return false;
    }
    /***
     * 主界面连接服务器按钮背景改变
     */
    private View.OnTouchListener connectbutton21Touch = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                connectbutton21.setBackgroundResource(R.drawable.buttondown);
            }
            if (event.getAction()==MotionEvent.ACTION_UP) {
                connectbutton21.setBackgroundResource(R.drawable.butonup);
            }
            return false;
        }
    };

}

