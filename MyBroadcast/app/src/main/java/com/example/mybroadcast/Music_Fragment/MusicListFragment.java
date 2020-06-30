package com.example.mybroadcast.Music_Fragment;


import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.mybroadcast.MainActivity;
import com.example.mybroadcast.MusicBean;
import com.example.mybroadcast.R;
import com.example.mybroadcast.SMPConstants;
import com.example.mybroadcast.Service.PlayMusicService;
import com.example.mybroadcast.Util;

import static com.example.mybroadcast.Music_Fragment.MusicPlayFragment.iv_cover;
import static com.example.mybroadcast.Music_Fragment.MusicPlayFragment.lyrics;
import static com.example.mybroadcast.Music_Fragment.MusicPlayFragment.music_end;
import static com.example.mybroadcast.Music_Fragment.MusicPlayFragment.music_start;
import static com.example.mybroadcast.Music_Fragment.MusicPlayFragment.seekBar;
import static com.example.mybroadcast.Music_Fragment.MusicPlayFragment.tv_title;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends Fragment {

    private ListView listView;

    public MusicListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_music_list, container, false);
        listView = view.findViewById(R.id.lv);
        listView.setAdapter(MainActivity.musicListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.currentIndex = position;
                Intent intent = new Intent(getActivity(), PlayMusicService.class);
                intent.putExtra("CMD", SMPConstants.CMD_PLAYAPOSITION);
                intent.putExtra("postion", position);
                getActivity().startService(intent);
                MainActivity.musicListAdapter.notifyDataSetChanged();
                playMusic();
            }
        });
        return view;
    }


    //播放
    private void playMusic() {
        Intent intent = new Intent(getActivity(), PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMD_PLAY);
        getActivity().startService(intent);
        MusicPlayFragment.MpStatus = Util.SMPConstants.STATUS_PLAY;
////        //修改按钮的图片
        MusicPlayFragment.play.setImageResource(R.drawable.bofang);
////        MusicPlayFragment.MpStatus = Util.SMPConstants.STATUS_PLAY;
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

}
