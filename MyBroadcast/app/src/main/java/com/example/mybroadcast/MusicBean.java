package com.example.mybroadcast;

public class MusicBean {
    //歌曲名称
    private String musicName;
    //演唱者
    private String singer;
    //歌曲长度,单位毫秒
    private int musicDuration;
    //专辑编号，用以获取专辑封面
    private int album_id;
    //mp3文件路径
    private String musicUrl;
    //歌词文件路径
    private String lrcUrl;

    public MusicBean(String musicName, String singer, int musicDuration, int album_id, String musicUrl, String lrcUrl) {
        this.musicName = musicName;
        this.singer = singer;
        this.musicDuration = musicDuration;
        this.album_id = album_id;
        this.musicUrl = musicUrl;
        this.lrcUrl = lrcUrl;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public int getMusicDuration() {
        return musicDuration;
    }

    public void setMusicDuration(int musicDuration) {
        this.musicDuration = musicDuration;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public String getMusicUrl() {
        return musicUrl;
    }

    public void setMusicUrl(String musicUrl) {
        this.musicUrl = musicUrl;
    }

    public String getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(String lrcUrl) {
        this.lrcUrl = lrcUrl;
    }
}
