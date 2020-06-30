package com.example.mybroadcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PuBuAdapter extends RecyclerView.Adapter<PuBuAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<PubuBean> list;

    public PuBuAdapter(Context context, List<PubuBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PuBuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.pubuadapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PuBuAdapter.ViewHolder holder, int position) {
        PubuBean pubuBean = list.get(position);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        BitmapFactory.decodeResource(context.getResources(), pubuBean.getImage(), options);
//
//        //获取图片的宽高
//        int height = options.outHeight;
//        int width = options.outWidth;
//
//        if(height>2109){
//            holder.imageView.hei
//        }
//        holder.imageView.setMaxHeight(365);
        holder.imageView.setImageResource(pubuBean.getImage());

        holder.tv1.setText(pubuBean.getTitle());
        holder.tv2.setText(pubuBean.getAutor());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tv1, tv2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tv1 = itemView.findViewById(R.id.infromation);
            tv2 = itemView.findViewById(R.id.autor);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
