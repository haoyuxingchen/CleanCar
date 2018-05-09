package com.example.justdoit.wificar;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class Control extends MainActivity{


    private SeekBar seekBar = null;
    private SeekBar seekBarClean = null;
    private DataOutputStream out;
    boolean ReadDataFlage=true;//接收数据标志

    TextView textView35;//显示轮子速度值
    TextView textViewClean;//显示清洁刷速度值
    TextView textViewZhuangtai;//显示状态

    ImageButton imageButton31;//前进
    ImageButton imageButton32;//后退
    ImageButton imageButton33;//右转
    ImageButton imageButton34;//左转

    boolean forward = false;
    boolean back = false;
    boolean right = false;
    boolean left = false;

    boolean switchOnOff =false;
    boolean switchClean=false;
    boolean switchT=false;
    boolean MoShi=false;
    boolean switchTP=false;
    int SeekBarprogrss=0;
    int SeekBarCleanprogrss=0;

    String Zhuangtai ="当前状态：";
    String SMoshi="当前模式：";
    String SDianYa="电压值：";
    String Direction="前进方向：";
    String SQingjieshua="清洁刷状态：";
    String SSpeed="车轮速度：";
    String SCleanSpeed="风扇速度：";
    String STanjiao="探测器状态：";
    String STanjiaoDianyuan="探测器电源：";
    String sock="";

    int a=0;//计数测试

    private Handler mHandler;

    private ListView lv;

   private List<SwitchAdapter> List=new ArrayList<>();

    Vibrator vibrator;//按钮按下震动

    boolean SendDataFlag = true;//发送数据任务控制
    SendMsgThread sendMsgThread;//发送数据任务
    Intent intentmain = new Intent(); //界面跳转--主界面

    long exitTime=0;//返回按键计时
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        starthandle=false;//关闭welcome继承的线程
        iswelcom=false;//继承的其它的不必要操作关闭
        fromwelcom=false;

        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme);//设置主题风格
        setContentView(R.layout.control2);
        Bundle bunde = this.getIntent().getExtras();//这个意思就是掏出来数据，类型还是Bundle
        if(bunde != null && bunde.containsKey("fromwelcom")){
            fromwelcom= bunde.getBoolean("fromwelcom");//这个是从Bundle里面取出键为fromwelcom的字段
        }

        textView35 = (TextView) findViewById(R.id.textView35);//轮子速度显示
        textViewClean = (TextView) findViewById(R.id.textViewClean);//清洁刷速度显示
        textViewZhuangtai= (TextView) findViewById(R.id.textViewZhuangtai);//状态

        imageButton31 = (ImageButton) findViewById(R.id.imageButton31);//前进
        imageButton32 = (ImageButton) findViewById(R.id.imageButton32);//后退
        imageButton33 = (ImageButton) findViewById(R.id.imageButton33);//右转
        imageButton34 = (ImageButton) findViewById(R.id.imageButton34);//左转
        initSwitchList();     //初始化数据
        SwitchListView adapter=new SwitchListView(Control.this,R.layout.listview,List);

        ListView listview = (ListView) findViewById(R.id.ListView);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             Switch switch1 =view.findViewById(R.id.aSwitch);
             TextView text =view.findViewById(R.id.tv);
             if(switch1.isChecked()){
                 switch1.setChecked(false);
                 if(text.getText()=="状态"){
                     switchOnOff=false;
                 }
                 if(text.getText()=="模式"){
                     MoShi=true;
                 }
                 if(text.getText()=="探测器状态"){
                     switchT=false;
                 }
                 if(text.getText()=="清洁刷"){
                     switchClean=false;
                 }
                 //if(text.getText()=="探测器电源"){
                     //switchTP=false;
                // }

             }
             else{
                 switch1.setChecked(true);
                 if(text.getText()=="状态"){
                     switchOnOff=true;
                 }
                 if(text.getText()=="模式"){
                     MoShi=false;
                 }
                 if(text.getText()=="探测器状态"){
                     switchT=true;
                 }
                 if(text.getText()=="清洁刷"){
                     switchClean=true;
                 }
                 //if(text.getText()=="探测器电源"){
                   //  switchTP=true;
                // }

             }
            }
        });

        imageButton31.setOnTouchListener(imageButton31Touch);//前进
        imageButton32.setOnTouchListener(imageButton32Touch);//后退
        imageButton33.setOnTouchListener(imageButton33Touch);//右转
        imageButton34.setOnTouchListener(imageButton34Touch);//左转

        vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);//震动

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//速度滑动条监听器
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SeekBarprogrss=progress;
                textView35.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "停止滑动！");
            }
        });
        seekBarClean = (SeekBar) findViewById(R.id.seekBarClean);
        seekBarClean.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {//风扇速度滑动条监听器
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SeekBarCleanprogrss=progress;
                textViewClean.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "开始滑动！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("------------", "停止滑动！");
            }
        });
        SeekBarprogrss=seekBar.getProgress();
        SeekBarCleanprogrss=seekBarClean.getProgress();

        mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                String a=null;
                //textViewZhuangtai.setText(SDianYa+String.valueOf(a)+"\n"+SSpeed+String.valueOf(SeekBarprogrss)+"\n"+SCleanSpeed+String.valueOf(SeekBarCleanprogrss)+"\n");
                if(switchOnOff){//开启状态
                    if(MoShi){//遥控模式
                        //a=null;
                        String d=null;String q=null;String t=null;//a是字符串d是方向q是清洁刷t是探角
                        if (forward) {//前进
                            d="前进";
                        }
                        if (back) {//后退
                            d="后退";
                        }
                        if (left) {//左转
                            d="左转";
                        }
                        if (right) {//右转
                            d="右转";
                        }
                        if (!(forward || back || right || left)) //没有按下的按钮
                        {
                            d="无";
                        }
                        if(switchClean){
                          q="开启";
                        }
                        else{
                            q="关闭";
                        }
                        if(switchT){
                            t="开启";
                        }else{
                            t="关闭";
                        }
                        a=Zhuangtai+"开启"+"\n"+SMoshi+"遥控"+"\n"+Direction+d+"\n"+SSpeed+String.valueOf(SeekBarprogrss)+"\n"+SCleanSpeed+String.valueOf(SeekBarCleanprogrss)+"\n"+SQingjieshua+q+"\n"+STanjiao+t;
                        //textViewZhuangtai.setText(a+"\n"+sock);//写入textview
                        textViewZhuangtai.setText(a);
                    }else{//自动模式
                        //a=null;
                        a=Zhuangtai+"开启"+"\n"+SMoshi+"自动"+"\n"+SSpeed+String.valueOf(SeekBarprogrss)+"\n"
                                +SCleanSpeed+String.valueOf(SeekBarCleanprogrss);
                        //+"\n"+Direction+""+"\n"+SSpeed+""+"\n"+SCleanSpeed+""+"\n"+SQingjieshua+""+"\n"+STanjiao+"";
                        //textViewZhuangtai.setText(a+"\n"+sock);
                        textViewZhuangtai.setText(a);
                    }
                }
                else{//关闭状态
                    //a=null;
                    a=Zhuangtai+"关闭";
                    //textViewZhuangtai.setText(a+"\n"+sock);
                    textViewZhuangtai.setText(a);
                }
                mHandler.postDelayed(this, 100);
            }
        });

        sendMsgThread = new SendMsgThread();
        sendMsgThread.start();//启动发送数据任务

        ReadDataThread readDataThread = new ReadDataThread();//接收数据
        readDataThread.start();//启动接收数据任务


    }


    private void initSwitchList(){
            SwitchAdapter list1=new SwitchAdapter("状态",false,"关","开");
            List.add(list1);
            SwitchAdapter list2=new SwitchAdapter("模式",false,"手动","自动");
            List.add(list2);
            SwitchAdapter list3=new SwitchAdapter("清洁刷",false,"关","开");
            List.add(list3);
            SwitchAdapter list4=new SwitchAdapter("探测器状态",false,"关","开");
            List.add(list4);
            SwitchAdapter list5=new SwitchAdapter("探测器电源",false,"关","开");
            List.add(list5);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 判断间隔时间 小于2秒就返回应用
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次返回连接界面",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            else
            {
                if(fromwelcom){
                    intentmain.setClass(getApplication(), MainActivity.class);//有错误跳转到主界面重新连接
                    Log.e("Control", "welcom转入mainactivity");
                    SendDataFlag=false;//发送消息关闭
                    fromwelcom=false;
                    ReadDataFlage=false;//接收消息关闭
                    socket = null;
                    startActivity(intentmain);
                    Control.this.finish();  // 结束第3屏Control
                }else{
                    titleEditText.setText("连接");
                    SendDataFlag=false;//发送消息关闭
                    fromwelcom=false;
                    ReadDataFlage=false;//接收消息关闭
                    socket=null;
                    super.onBackPressed();
                    Log.e("Control", "Control返回mainactivity");
                    finish();
                }
            }
            return false;
        }
        return false;
    }

    /***
     * 前进按钮
     */
    private OnTouchListener imageButton31Touch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                forward = true;
                back=false;
                imageButton31.setImageResource(R.drawable.qianjindown);//改变背景

                vibrator.vibrate(new long[]{0,20}, -1);//震动
            }
            if (event.getAction()==MotionEvent.ACTION_UP) {
                forward = false;
                imageButton31.setImageResource(R.drawable.qianjin);//改变背景
            }
            return false;
        }
    };

    /***
     * 后退按钮
     */
    private OnTouchListener imageButton32Touch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                back=true;
                forward=false;
                imageButton32.setImageResource(R.drawable.houtuidown);//改变背景
                vibrator.vibrate(new long[]{0,20}, -1); //震动
            }
            if (event.getAction()==MotionEvent.ACTION_UP) {
                back=false;
                imageButton32.setImageResource(R.drawable.houtui);//改变背景
            }
            return false;
        }
    };

    /***
     * 右转按钮
     */
    private OnTouchListener imageButton33Touch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                right=true;
                left=false;
                imageButton33.setImageResource(R.drawable.youzhuandown);//改变背景
                vibrator.vibrate(new long[]{0,20}, -1);  //震动
            }
            if (event.getAction()==MotionEvent.ACTION_UP) {
                right=false;
                imageButton33.setImageResource(R.drawable.youzhuan);//改变背景
            }
            return false;
        }
    };

    /***
     * 左转按钮
     */
    private OnTouchListener imageButton34Touch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                left=true;
                right=false;
                imageButton34.setImageResource(R.drawable.zuozhuandown);//改变背景
                vibrator.vibrate(new long[]{0,20},-1); //震动
            }
            if (event.getAction()==MotionEvent.ACTION_UP) {
                left=false;
                imageButton34.setImageResource(R.drawable.zuozhuan);//改变背景
            }
            return false;
        }
    };

    /**
     *左转大于右转大于后退大于前进
     *(单个按钮)谁按下执行谁
     *
     */
    class SendMsgThread extends Thread {
        public void run() {
            while (SendDataFlag) {
                byte[] sendbyte = new byte[9];//发送的数据缓存

                sendbyte[0] = (byte) 0x7B;
                sendbyte[1] = (byte) 0x7B;
                if (switchOnOff) {   //小车启动
                    if (MoShi) {        //手动模式
                        sendbyte[2] = (byte) 0x30;
                        if (forward) {//前进
                            sendbyte[3] = (byte) 0x01;
                        }
                        if (back) {//后退
                            sendbyte[3] = (byte) 0x02;
                        }
                        if (left) {//左转
                            sendbyte[3] = (byte) 0x03;
                        }
                        if (right) {//右转
                            sendbyte[3] = (byte) 0x04;
                        }

                        sendbyte[4] = (byte) (SeekBarprogrss+1);
                        sendbyte[5] = (byte) (SeekBarCleanprogrss+1);

                        if (switchClean) {
                            sendbyte[6] = (byte) 0x31;//清洁刷开启
                        } else {
                            sendbyte[6] = (byte) 0x30;//清洁刷关闭
                        }
                        if (switchT) {
                            sendbyte[7] = (byte) 0x31;//探角开启
                        } else {
                            sendbyte[7] = (byte) 0x30;//探角关闭
                        }
                        if (!(forward || back || right || left)) //没有按下的按钮
                        {
                            sendbyte[3] = (byte) 0x30;//其他值
                            sendbyte[4] = (byte) 0x01;//速度为零
                        }
                        sendbyte[8] = (byte) 0x7D;


                    } else {            //自动模式
                        sendbyte[2] = (byte) 0x31;
                        sendbyte[3] = (byte) 0x30;//无意义的值
                        sendbyte[4] = (byte) (SeekBarprogrss+1);
                        sendbyte[5] = (byte) (SeekBarCleanprogrss+1);
                        if (switchClean) {
                            sendbyte[6] = (byte) 0x31;//清洁刷开启
                        } else {
                            sendbyte[6] = (byte) 0x30;//清洁刷关闭
                        }
                        sendbyte[7] = (byte) 0x01;//探角
                        sendbyte[8] = (byte) 0x7D;  //结束

                    }

                } else {               //小车关闭
                    sendbyte[2] = (byte) 0x30;//遥控模式
                    sendbyte[3] = (byte) 0x30;//方向无效值
                    sendbyte[4] = (byte) 0x01;//速度0
                    sendbyte[5] = (byte) 0x01;//风扇转速0
                    sendbyte[6] = (byte) 0x30;//清洁刷关闭
                    sendbyte[7] = (byte) 0x30;//探角关闭
                    sendbyte[8] = (byte) 0x7D;

                 }
                netSend(sendbyte);

                    String byte1="";
                    for(int i=0;i<sendbyte.length;i++){
                        byte1=byte1+" "+sendbyte[i];
                    }
                    sock="\n"+"sock:"+byte1+" ";//测试socket发送值

                try {
                    Thread.sleep(200);//延时200ms
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送数据
     * @param byt
     */
    private void netSend(byte[] byt)
    {

        try
        {
            outputStream = socket.getOutputStream();
            Log.e("Control", "获取输出流"+socket);
        }
        catch (Exception e)
        {
            Log.e("Control", "获取输出流错误"+socket);
        }
        try
        {
            outputStream.write(byt);
            Log.e("Control", "发送"+socket);
        }
        catch (IOException e)
        {
            Log.e("Control", "发送错误,跳转到主界面!!");
            SendDataFlag = false;
            socket = null;
            intentmain.setClass(Control.this, MainActivity.class);//有错误跳转到主界面重新连接
            Control.this.startActivity(intentmain);
            finish();
        }

    }

//    /**
//     * CRC检验值
//     * @param modbusdata
//     * @param length
//     * @return CRC检验值
//     */
//    protected int crc16_modbus(byte[] modbusdata, int length)
//    {
//        int i=0, j=0;
//
//        int crc = 0;
//
//        try
//        {
//            for (i = 0; i < length; i++)
//            {
//                crc ^= (modbusdata[i]&(0xff));//注意这里要&0xff
//                for (j = 0; j < 8; j++)
//                {
//                    if ((crc & 0x01) == 1)
//                    {
//                        crc = (crc >> 1) ;
//                        crc = crc ^ 0xa001;
//                    }
//                    else
//                    {
//                        crc >>= 1;
//                    }
//                }
//            }
//        }
//        catch (Exception e)
//        {
//
//        }
//
//        return crc;
//    }
//
//    /**
//     * CRC校验正确标志
//     * @param modbusdata
//     * @param length
//     * @return 0-failed 1-success
//     */
//    protected int crc16_flage(byte[] modbusdata, int length)
//    {
//        int Receive_CRC = 0, calculation = 0;//接收到的CRC,计算的CRC
//
//        Receive_CRC = crc16_modbus(modbusdata, length);
//        calculation = modbusdata[length + 1];
//        calculation <<= 8;
//        calculation += modbusdata[length];
//        if (calculation != Receive_CRC)
//        {
//            return 0;
//        }
//        return 1;
//    }

    /***
     *
     * @author 接收消息任务
     *
     */
    class ReadDataThread extends Thread
    {
        public void run()
        {
            while(ReadDataFlage)
            {

                try
                {
                    inputStream = socket.getInputStream();
                    Log.e("Control", "获取输入流"+socket);
                }
                catch (Exception e)
                {
                    Log.e("Control", "获取输入流错误"+socket);
                }
                try
                {
                    ReadbyteLen = inputStream.read(Readbyte);
                    if (ReadbyteLen == -1)
                    {
                        Log.e("Control", "接收消息错误");
                        //socket = null;
                        //ReadDataFlage = false;
                    }
                } catch (IOException e) {
                    Log.e("Control", "接收消息错误");
                    //ReadDataFlage = false;
                    //socket = null;
                }

                try {
                    Thread.sleep(2000);//延时200ms
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

    public void ntget(){

    }

}

