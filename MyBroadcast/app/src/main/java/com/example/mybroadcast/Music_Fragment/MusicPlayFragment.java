package com.example.mybroadcast.Music_Fragment;


import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mybroadcast.MainActivity;
import com.example.mybroadcast.MusicBean;
import com.example.mybroadcast.R;
import com.example.mybroadcast.SMPConstants;
import com.example.mybroadcast.Service.PlayMusicService;
import com.example.mybroadcast.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicPlayFragment extends Fragment implements View.OnClickListener {
    static TextView tv_title, lyrics, music_start, music_end;
    static ImageView iv_cover, sys, play, xys;
    static SeekBar seekBar;
    // 保存MediaPlayer对象，用以播放音乐

    // 用以记录播放器的状态
    static int MpStatus;
    private LrcReceiver lrcReceiver;
    private SeekReceiver seekReceiver;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_play, container, false);
        tv_title = view.findViewById(R.id.tv_title);
        lyrics = view.findViewById(R.id.lyrics);
        music_start = view.findViewById(R.id.music_start);
        music_end = view.findViewById(R.id.music_end);
        iv_cover = view.findViewById(R.id.iv_cover);
        sys = view.findViewById(R.id.sys);
        play = view.findViewById(R.id.play);
        xys = view.findViewById(R.id.xys);
        seekBar = view.findViewById(R.id.sb);


        initView(MainActivity.currentIndex);

        sys.setOnClickListener(this);
        play.setOnClickListener(this);
        xys.setOnClickListener(this);


        //当前播放器状态设置为Stop状态
        MpStatus = Util.SMPConstants.STATUS_STOP;
        //实例化MediaPlayer对象
        //实现一首歌曲播放完后，自动播放下一首的功能OnCompletionListener播完后监听器
        PlayMusicService.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer arg0) {
                nextMusic();
            }
        });

        //注册
        lrcReceiver = new LrcReceiver();
        getActivity().registerReceiver(lrcReceiver, new IntentFilter(SMPConstants.ACT_LRC_RETURN_BROADCAST));

        seekReceiver = new SeekReceiver();
        getActivity().registerReceiver(seekReceiver,new IntentFilter(SMPConstants.ACT_PROGRESS_RETURN_BROADCAST));


        //实现拖动进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseMusic();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Intent intent = new Intent(getActivity(), PlayMusicService.class);
                intent.putExtra("CMD",SMPConstants.CMD_CHANDEPROGESS);
                intent.putExtra("CP",seekBar.getProgress());
                getActivity().startService(intent);
                continueMusic();
            }
        });


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(lrcReceiver);
        getActivity().unregisterReceiver(seekReceiver);
    }

    //暂停
    private void pauseMusic() {
        Intent intent = new Intent(getActivity(), PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMD_PAUSE);
        getActivity().startService(intent);

        MpStatus = Util.SMPConstants.STATUS_PAUSE;
        //修改按钮的图片把暂停的图片改成播放
        play.setImageResource(R.drawable.play_selector);
    }

    //继续播放
    private void continueMusic() {
        Intent intent = new Intent(getActivity(), PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMD_CONTINUE);
        getActivity().startService(intent);

        MpStatus = Util.SMPConstants.STATUS_PLAY;
        //修改按钮的图片
        play.setImageResource(R.drawable.bofang);
    }

    //播放
    private void playMusic() {
        Intent intent = new Intent(getActivity(), PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMD_PLAY);
        getActivity().startService(intent);

        MpStatus = Util.SMPConstants.STATUS_PLAY;
        //修改按钮的图片
        play.setImageResource(R.drawable.bofang);
        lyrics.setText("");
    }

    //播放上一首，如果已经是第一首则播放最后一首
    private void prevMusic() {
        Intent intent = new Intent(getActivity(), PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMD_PREV);
        getActivity().startService(intent);

        if (MainActivity.currentIndex <= 0) {
            MainActivity.currentIndex = PlayMusicService.musicBeanArrayList.size() - 1;
        } else {
            MainActivity.currentIndex--;
        }


        playMusic();
        MpStatus = Util.SMPConstants.STATUS_PLAY;
        //修改按钮的图片
        lyrics.setText("");
        initView(MainActivity.currentIndex);
    }

    //播放下一首，如果已经是最后一首则播放第一首
    private void nextMusic() {

        Intent intent = new Intent(getActivity(), PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMD_NEXT);
        getActivity().startService(intent);

        if (MainActivity.currentIndex >= PlayMusicService.musicBeanArrayList.size() - 1) {
            MainActivity.currentIndex = 0;
        } else {
            MainActivity.currentIndex++;
        }
        playMusic();
        MpStatus = Util.SMPConstants.STATUS_PLAY;
        initView(MainActivity.currentIndex);
    }

    //找封面
    private Bitmap getAlbumArt(int album_id) {
        Bitmap bmp = null;
        Cursor cur = getActivity().getContentResolver().query(ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, album_id), null, null, null, null);
        boolean b = cur.moveToNext();
        Log.i("cur.moveToNext()------", b + "");
        if (b) {
            String path = cur.getString(cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));//获取封面
            Log.i("path-----", path + "");
            bmp = BitmapFactory.decodeFile(path);
        }
        return bmp;
    }

    //设置基本控件的信息。
    private void initView(int music_index) {
        if (music_index > -1) {
            MusicBean bean = PlayMusicService.musicBeanArrayList.get(music_index);
            tv_title.setText(bean.getMusicName());
            music_start.setText("00:00");
            music_end.setText(Util.toTime(bean.getMusicDuration()));
            lyrics.setText("");
            //设置进度条的最大长度
            seekBar.setIndeterminate(false);
            seekBar.setMax(bean.getMusicDuration());
            //获取专辑id
            int album_id = bean.getAlbum_id();
            Log.i("album_id---------------", album_id + "");
            Bitmap bm = getAlbumArt(album_id);
            //如果能够找到专辑封面则显示，否则显示默认图片
            if (bm != null) {
                BitmapDrawable bmpDraw = new BitmapDrawable(getResources(), bm);
                iv_cover.setImageDrawable(bmpDraw);
            } else {
                iv_cover.setImageResource(R.drawable.nopic);
            }
        }
    }

    //设置点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play:
                switch (MpStatus) {
                    case Util.SMPConstants.STATUS_PAUSE:
                        continueMusic();
                        break;
                    case Util.SMPConstants.STATUS_PLAY:
                        pauseMusic();
                        break;
                    case Util.SMPConstants.STATUS_STOP://STATUS_STOP为初始值0，表示刚刚初始化。
                        playMusic();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.sys:
                prevMusic();
                break;
            case R.id.xys:
                nextMusic();
                break;
            default:
                break;
        }
        MainActivity.musicListAdapter.notifyDataSetChanged();
    }

    //用来刷新页面的
    public void setMpStatus(int mpStatus) {
        initView(MainActivity.currentIndex);
        MpStatus = mpStatus;
        if (mpStatus == Util.SMPConstants.STATUS_PLAY) {
            play.setImageResource(R.drawable.bofang);
        } else {
            play.setImageResource(R.drawable.play_selector);
        }
    }

    class LrcReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取广播中带的歌词信息
            String msg = intent.getStringExtra("LRC");
            //显示歌词信息
            lyrics.setText(msg);
        }
    }

    class SeekReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取广播中带的进度条信息
            int time = intent.getIntExtra("PROGRESS",0);
            seekBar.setProgress(time);
            music_start.setText(Util.toTime(time));
        }
    }

}
