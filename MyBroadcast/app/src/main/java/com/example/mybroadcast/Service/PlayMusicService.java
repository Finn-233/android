package com.example.mybroadcast.Service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mybroadcast.LrcBean;
import com.example.mybroadcast.MusicBean;
import com.example.mybroadcast.SMPConstants;
import com.example.mybroadcast.Util;
import com.example.mybroadcast.lrc.LrcProcessor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

//）歌词的同步原理：音乐播放时，程序通过一个线程定时比较当前时间和歌词文件中指
//定的开始时间，如果到时，则通过广播将该段歌词发送出去，否则，等待下次比较时间。
public class PlayMusicService extends Service {
    public static ArrayList<MusicBean> musicBeanArrayList;
    private int currentIndex = -1;
    public static MediaPlayer mp;
    private int MpStatus;//记录状态
    //保存每条歌词的时间和内容
    private ArrayList<LrcBean> lrcs;
    //下一条歌词显示的时间
    private int nextTimeMil = 0;
    //歌词Arraylist中的序号
    private int LrcPos;
    //歌词内容
    private String message;
    //用以调度歌词线程
    private Handler lrcHandler = new Handler();
    //自定义实现Runnable接口的线程类
    private LrcCallBack r = null;
    //用以调度进度线程
    private Handler prgHandler = new Handler();
    private PrgCallBack pr = null;
    //在前端发送过来的进度条
    private int seekbartime = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        musicBeanArrayList = new ArrayList<>();
        setData();
        if (musicBeanArrayList.size() > 0) currentIndex = 0;
        MpStatus = SMPConstants.STATUS_STOP;
        mp = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int cmd = intent.getIntExtra("CMD", -1);//这里的-1表示如果找不到就用-1来赋值。
            switch (cmd) {
                case SMPConstants.CMD_GETINFORM:
                    sendPMSInfo();
                    break;
                case SMPConstants.CMD_PLAY:
                    playMusic();
                    break;
                case SMPConstants.CMD_NEXT:
                    nextMusic();
                    break;
                case SMPConstants.CMD_CONTINUE:
                    continueMusic();
                    break;
                case SMPConstants.CMD_PAUSE:
                    pauseMusic();
                    break;
                case SMPConstants.CMD_PREV:
                    prevMusic();
                    break;
                case SMPConstants.CMD_CHANDEPROGESS:
                    seekbartime = intent.getIntExtra("CP", 0);
                    mp.seekTo(seekbartime);
                    break;
                case SMPConstants.CMD_PLAYAPOSITION:
                    int postion = intent.getIntExtra("postion", 0);
                    currentIndex = postion;
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendPMSInfo() {
        Intent intent = new Intent(SMPConstants.ACT_SERVICE_REQUEST_BROADCAST);
        intent.putExtra("index", currentIndex);
        intent.putExtra("status", MpStatus);
        sendBroadcast(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            mp.stop();
            mp.release();
        }
    }


    //暂停
    private void pauseMusic() {
        mp.pause();
        MpStatus = Util.SMPConstants.STATUS_PAUSE;
    }

    //继续播放
    private void continueMusic() {
        mp.start();
        MpStatus = Util.SMPConstants.STATUS_PLAY;
    }

    //播放
    private void playMusic() {
        String musicPath = PlayMusicService.musicBeanArrayList.get(currentIndex).getMusicUrl();
        try {
            mp.reset();
            mp.setDataSource(musicPath);
            mp.prepare();
            //解析歌词
            initLrc(musicPath.substring(0, musicPath.length() - 3) + "lrc");
            String lrcpa = musicPath.substring(0, musicPath.length() - 3) + "lrc";
            //启动歌词线程
            lrcHandler.post(r);

            //启动进度条线程
            prgHandler.post(pr);

            mp.start();
            MpStatus = Util.SMPConstants.STATUS_PLAY;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //播放上一首，如果已经是第一首则播放最后一首
    private void prevMusic() {
        if (currentIndex <= 0) {
            currentIndex = PlayMusicService.musicBeanArrayList.size() - 1;
        } else {
            currentIndex--;
        }
        playMusic();
        MpStatus = Util.SMPConstants.STATUS_PLAY;
    }

    //播放下一首，如果已经是最后一首则播放第一首
    private void nextMusic() {
        if (currentIndex >= PlayMusicService.musicBeanArrayList.size() - 1) {
            currentIndex = 0;
        } else {
            currentIndex++;
        }
        playMusic();
        MpStatus = Util.SMPConstants.STATUS_PLAY;
    }


    private void setData() {
        musicBeanArrayList.clear();
        //跟之前一样ContentProvider来查询手机中的音乐文件
        Cursor c = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        while (c.moveToNext()) {
            String musicname = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
            String singer = c.getString(c.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            int duration = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
            int albumid = c.getInt(c.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            String musicurl = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
            String lrcurl = "";
            MusicBean bean = new MusicBean(musicname, singer, duration, albumid, musicurl, lrcurl);
            musicBeanArrayList.add(bean);
        }
        c.close();
    }

    //打开PlayMusicService，当开始播放音乐时，搜索歌词文件，如果存在，则解析歌词文件
    private void initLrc(String lrcPath) {
        InputStream in;
        try {
            //判断指定文件的编码格式
            String charset = LrcProcessor.getCharSet(new FileInputStream(lrcPath));
            //解析歌词文件
            LrcProcessor lrcProc = new LrcProcessor();
            in = new FileInputStream(lrcPath);
            lrcs = lrcProc.process(in, charset);

            if (r != null) {
                lrcHandler.removeCallbacks(r);
            }
            //实例化线程对象
            r = new LrcCallBack(lrcs);


            if (pr != null) {
                prgHandler.removeCallbacks(pr);
            }
            //创建进度条线程对象
            pr = new PrgCallBack();

            nextTimeMil = 0;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    //    在该方法中比较时间，判断是否到了要求的时间，到时则发送广播.
    class LrcCallBack implements Runnable {
        private ArrayList<LrcBean> lrcList;

        public LrcCallBack(ArrayList<LrcBean> list) {
            this.lrcList = list;
            LrcPos = 0;
        }

        @Override
        public void run() {
            try {
                //如果首次调用，则获取下一条歌词的显示时间和内容
                if (nextTimeMil == 0) {
                    nextTimeMil = lrcList.get(LrcPos).getBegintime();
                    message = lrcList.get(LrcPos).getLrcMsg();
                }
                //获取当前播放时间
                int time = mp.getCurrentPosition();//该方法返回持续时间（以毫秒为单位）是media的内部方法

                if (seekbartime != 0) {
                    LrcPos = 0;
                    nextTimeMil = lrcList.get(LrcPos).getBegintime();
                    while (nextTimeMil >= seekbartime) {
                        LrcPos++;
                        nextTimeMil = lrcList.get(LrcPos).getBegintime();
                        message = lrcList.get(LrcPos).getLrcMsg();
                    }
                    // 通过广播将歌词发送到前台
                    Intent i = new Intent(SMPConstants.ACT_LRC_RETURN_BROADCAST);
                    i.putExtra("LRC", message);
                    sendBroadcast(i);
                    seekbartime = 0;
                } else {
                    //如果到了歌词显示的时间，则将歌词通过广播发送出去
                    if (time >= nextTimeMil) {
                        // 通过广播将歌词发送到前台
                        Intent i = new Intent(SMPConstants.ACT_LRC_RETURN_BROADCAST);
                        i.putExtra("LRC", message);
                        sendBroadcast(i);
                        LrcPos++;
                        //获取下条歌词的显示时间
                        nextTimeMil = lrcList.get(LrcPos).getBegintime();
                        //获取下条歌词的内容
                        message = lrcList.get(LrcPos).getLrcMsg();
                    }
                }
                //如果时间没有超过歌曲长度，则10毫秒后再次运行该线程
                if (time < mp.getDuration()) {
                    lrcHandler.postDelayed(this, 10);
                }
            } catch (Exception ex) {
                Log.i("LrcErr", ex.getMessage());
            }

        }
    }


    class PrgCallBack implements Runnable {
        @Override
        public void run() {
            int time = mp.getCurrentPosition();
            Intent i = new Intent(SMPConstants.ACT_PROGRESS_RETURN_BROADCAST);
            i.putExtra("PROGRESS", time);
            sendBroadcast(i);
            //每隔300毫秒发送一次
            prgHandler.postDelayed(this, 300);
        }
    }

}
