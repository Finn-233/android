package com.example.mybroadcast.lrc;

import android.util.Log;

import com.example.mybroadcast.LrcBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcProcessor {
    ArrayList<LrcBean> lrcmap = new ArrayList<>();

    //判断歌词文件的编码
    //这里通过对前三个字符的判断来判断用哪种编码
    public static String getCharSet(InputStream in) {
        byte[] b = new byte[3];
        String charset = "";
        try {
            in.read(b);
            in.close();
            if (b[0] == (byte) 0xEF && b[1] == (byte) 0xBB && b[2] == (byte) 0xBF) {
                charset = "UTF-8";
            } else if (b[0] == (byte) 0xFE && b[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
            } else if (b[0] == (byte) 0xFF && b[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
            } else {
                charset = "GBK";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return charset;
    }

    //解析b编码的歌词文件，解析后的结果以ArrayList的形式返回
    public ArrayList<LrcBean> process(InputStream in, String charset) {
        try {
            InputStreamReader inputStreamReader;
            if (!charset.equals("")) {
                inputStreamReader = new InputStreamReader(in, charset);
            } else {
                inputStreamReader = new InputStreamReader(in);
            }
            BufferedReader br = new BufferedReader(inputStreamReader);
            String temp = null;
            while ((temp = br.readLine()) != null) {
                paraseLine(temp);//把歌词用arraylist把时间和对应的歌词存起来了
            }
            Collections.sort(lrcmap);//这里的排序规则就是按照，我们在LrcBean写的接口？
            br.close();
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return lrcmap;
    }

    //按照歌词文件格式解析

    private void paraseLine(String str) {
        String msg;
        //获取歌曲信息
        //这里我们根据歌词文件的特性来把相关的信息拿出来
        if (str.startsWith("[ti:")) {
            String title = str.substring(4, str.length() - 1);
        } else if (str.startsWith("[ar:")) {
            String singer = str.substring(4, str.length() - 1);
        } else if (str.startsWith("[al:")) {
            String album = str.substring(4, str.length() - 1);
        }//通过正则来获取歌词
        else {
            Pattern p = Pattern.compile("\\[\\s*[0-9]{1,2}\\s*:\\s*[0-5][0-9]\\s*[\\.:]?\\s*[0-9]?[0-9]?\\s*\\]");
            Matcher m = p.matcher(str);
            msg = str.substring(str.lastIndexOf("]") + 1);
            while (m.find()) {
                String timestr = m.group();
                timestr = timestr.substring(1, timestr.length() - 1);
                int timeMil = time2long(timestr);
                LrcBean temp = new LrcBean(timeMil, msg);
                lrcmap.add(temp);
                Log.i("Test", timeMil + "---" + msg);
            }

        }
    }
    //[00:16.48]录音/缩混：百慕三石
    //[00:19.45]演唱：刘珂矣
    //将mm：ss.ddd格式的时间转换为毫秒值
    private int time2long(String timestr) {
        int min = 0, sec = 0, mil = 0;
        try {
            timestr = timestr.replace(".", ":");
            String[] s = timestr.split(":");
            switch (s.length) {
                case 2:
                    min = Integer.parseInt(s[0]);
                    sec = Integer.parseInt(s[1]);
                    break;
                case 3:
                    min = Integer.parseInt(s[0]);
                    sec = Integer.parseInt(s[1]);
                    mil = Integer.parseInt(s[2]);
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            Log.i("LrcErr", timestr + ex.getMessage());
        }
        return min * 60 * 1000 + sec * 1000 + mil * 10;
    }


}
