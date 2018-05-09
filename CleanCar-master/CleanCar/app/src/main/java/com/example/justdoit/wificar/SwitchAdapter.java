package com.example.justdoit.wificar;

/**
 * Created by Justdoit on 2018/5/3.
 */

public class SwitchAdapter {
    private String name;
    private String textoff;
    private String texton;
    private boolean Onoff;
    public SwitchAdapter(String name,boolean Onoff,String textoff,String texton){
        this.name=name;
        this.Onoff=Onoff;
        this.textoff=textoff;
        this.texton=texton;
    }
    public String getName(){
        return name;
    }
    public boolean getOnoff(){
        return Onoff;
    }
    public String getTextoff(){
        return textoff;
    }
    public String getTextOn(){
        return texton;
    }
}
