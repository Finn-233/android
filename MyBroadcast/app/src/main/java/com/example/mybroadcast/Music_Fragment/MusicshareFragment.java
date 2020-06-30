package com.example.mybroadcast.Music_Fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.mybroadcast.PuBuAdapter;
import com.example.mybroadcast.PubuBean;
import com.example.mybroadcast.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicshareFragment extends Fragment {
    private RecyclerView rv_staggered;

    public MusicshareFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_musicshare, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rv_staggered = getView().findViewById(R.id.pubu);
        rv_staggered.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        //一个参数表示有多少列，一个表示怎么分布，这个为垂直。
        rv_staggered.setAdapter(new PuBuAdapter(getContext(), add()));
    }

    public List<PubuBean> add() {
        List<PubuBean> list = new ArrayList<>();
        int tu[] = new int[]{R.drawable.tu1, R.drawable.tu6, R.drawable.tu2, R.drawable.tu12, R.drawable.tu3, R.drawable.tu8, R.drawable.tu4,
                R.drawable.tu5, R.drawable.tu11, R.drawable.tu7, R.drawable.tu9,
                R.drawable.tu10};
        for (int i : tu) {
            PubuBean pubuBean = new PubuBean(i, "测试测试测试", "作者test");
            Log.i("pubu", i + "");
            list.add(pubuBean);
        }

        return list;
    }
}
