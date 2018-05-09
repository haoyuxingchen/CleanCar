package com.example.justdoit.wificar;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class welcome extends Activity{

    String Ipstring="";//记录IP地址
    int portint=0;//记录端口号
    InetAddress ipAddress;
    static Socket socket = null;//定义socket
    public SharedPreferences sharedPreferences;//存储数据

     boolean Canconnect=false;
     boolean starthandle=true;//welcome线程延迟两秒 进入其它界面 连接不上进入连接界面否则进入操控界面
     boolean fromwelcom=false;//是否从welcome进入
    boolean iswelcom=true;//是否从welcome进入
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(android.R.style.Theme);//设置主题风格

        setContentView(R.layout.activity_welcome);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean issave = sharedPreferences.getBoolean("save", false);//得到save文件存的值，得不到会返回false
        if (issave)
        {
            String Ipstring1 = sharedPreferences.getString("Ipstring", "192.168.4.1");//取出ip,不存在返回192.168.4.1
            String portint1 = sharedPreferences.getString("portint", "8888");//取出端口号,不存在返回8888
            Ipstring=Ipstring1.toString().replace(" ", "");
            portint=Integer.valueOf(portint1.replace(" ", ""));
        }
        else{
            Canconnect=false;
        }
        startHandle();
//        Toast.makeText(getApplicationContext(), "ip  "+Ipstring,
//        Toast.LENGTH_LONG).show();

    }
    private void startHandle(){
        if(starthandle){
            ConnectSever connectSever = new ConnectSever();
            connectSever.start();
            handler = new Handler();
            handler.postDelayed(new splashhandler(), 2000); // 延迟2秒，再运行splashhandler的run()
            setContentView(R.layout.activity_welcome);
        }

    }
    class splashhandler implements Runnable
    {
        public void run()
        {
            if(Canconnect==true){
                fromwelcom=true;
                Intent intent = new Intent(getApplication(),  Control.class);
//主角，它把数据压进了一个叫做Bundle的东西里面，里面是键值对，取的时候特别好取
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromwelcom",fromwelcom);
                intent.putExtras(bundle);
                startActivity(intent);
                welcome.this.finish();
            }
            else{
                fromwelcom=false;
                Intent intent = new Intent(getApplication(),  MainActivity.class);
//主角，它把数据压进了一个叫做Bundle的东西里面，里面是键值对，取的时候特别好取
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromwelcom",fromwelcom);
                intent.putExtras(bundle);
                startActivity(intent);
                welcome.this.finish();
//                startActivity(new Intent(getApplication(),  MainActivity.class)); // 显示第2屏
//                fromwelcom=false;
//                welcome.this.finish();   // 结束第1屏
            }
        }
    }

    /***
     *
     * @author 连接服务器任务
     *
     */
    class ConnectSever extends Thread
    {
        public void run()
        {
                try
                {
                    ipAddress = InetAddress.getByName(Ipstring);
                    socket = new Socket(ipAddress, portint);
                    Log.e("welcome", "welcometry"+socket);
                    Canconnect=true;
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("welcome", "welcome"+socket);
                }
        }

    }


}
