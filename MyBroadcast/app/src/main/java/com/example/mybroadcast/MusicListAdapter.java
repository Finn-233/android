package com.example.mybroadcast;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MusicListAdapter extends BaseAdapter {
    private Context context;
    private List<MusicBean> beanList;

    public MusicListAdapter(Context context, List<MusicBean> beanList) {
        this.context = context;
        this.beanList = beanList;
    }

    @Override
    public int getCount() {
        return beanList.size();
    }

    @Override
    public Object getItem(int position) {
        return beanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView name, singer, time;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.music_list_adapter, parent, false);
            holder.imageView = convertView.findViewById(R.id.music_cover);
            holder.name = convertView.findViewById(R.id.music_name);
            holder.singer = convertView.findViewById(R.id.singer);
            holder.time = convertView.findViewById(R.id.music_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MusicBean musicBean = beanList.get(position);
        holder.imageView.setImageResource(R.drawable.yinyue);
        holder.name.setText(musicBean.getMusicName());
        holder.singer.setText(musicBean.getSinger());
        holder.time.setText(Util.toTime(musicBean.getMusicDuration()));
        if (position == MainActivity.currentIndex) {
            holder.imageView.setImageResource(R.drawable.isplaying);
//            convertView.setBackgroundColor(Color.rgb(169,169,169));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}
