package com.example.mybroadcast;

public class SMPConstants {
    //MediaPlayer状态信息
    public static final int STATUS_STOP = 0;
    public static final int STATUS_PALY = 1;
    public static final int STATUS_PAUSE = 2;
    public static final int STATUS_CALLIN_PAUSE = 3;//来电提醒


    //Activity向Service传送的命令
    public static final int CMD_PLAY = 1;//播放
    public static final int CMD_PAUSE = 2;//暂停
    public static final int CMD_CONTINUE = 3;//继续播放
    public static final int CMD_PREV = 4;//上一首
    public static final int CMD_NEXT = 5;//下一首
    public static final int CMD_GETINFORM = 6;//获取后台状态信息
    public static final int CMD_CHANDEPROGESS = 7;//改变播放进度
    public static final int CMD_PLAYAPOSITION = 8;//播放指定位置歌曲
    //后台向前台发送后台当前状态信息广播
    public static final String ACT_SERVICE_REQUEST_BROADCAST =
            "cn.edu.szpt.MySimpleMP3Player.ResponseInform";
    //后台向前台发送歌词广播
    public static final String ACT_LRC_RETURN_BROADCAST=
            "cn.edu.szpt.MySimpleMP3Player.ACT_LRC_RETURN_BROADCAST";
    //后台向前台发送进度条信息广播
    public static final String ACT_PROGRESS_RETURN_BROADCAST=
            "cn.edu.szpt.MySimpleMP3Player.ACT_PROGRESS_RETURN_BROADCAST";

}
