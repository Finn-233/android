package com.example.mybroadcast;

public class Util {

    public static String toTime(int time) {
        time /= 1000;
        int minute = time / 60;
        int hour = minute / 60;
        int second = time % 60;
        minute %= 60;
        if (hour > 0)
            return String.format("%02d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
    }
    //播放器状态
    public class SMPConstants {
        // MediaPlayer状态信息
        public static final int STATUS_STOP = 0;
        public static final int STATUS_PLAY = 1;
        public static final int STATUS_PAUSE = 2;
        public static final int STATUS_CALLIN_PAUSE = 3; //来电暂停
    }
}
