package com.example.mybroadcast;

//因为要排序所以用Comparable接口，Comparable接口就是用来自定义的类来比较大小。
//这里就是用来比较歌词的开始时间的
public class LrcBean implements Comparable<LrcBean> {
    //歌词开始时间
    private int begintime;
    //歌词信息
    private String lrcMsg;

    public LrcBean(int begintime, String lrcMsg) {
        this.begintime = begintime;
        this.lrcMsg = lrcMsg;
    }

    public int getBegintime() {
        return begintime;
    }

    public void setBegintime(int begintime) {
        this.begintime = begintime;
    }

    public String getLrcMsg() {
        return lrcMsg;
    }

    public void setLrcMsg(String lrcMsg) {
        this.lrcMsg = lrcMsg;
    }

    @Override
    public int compareTo(LrcBean o) {
        return this.begintime - o.begintime;
    }
    //这样的话，如果该对象小于、等于或大于指定对象，则分别返回负整数、零或正整数
}
