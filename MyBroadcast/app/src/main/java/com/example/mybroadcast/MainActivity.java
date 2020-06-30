package com.example.mybroadcast;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.mybroadcast.Music_Fragment.MusicListFragment;
import com.example.mybroadcast.Music_Fragment.MusicPlayFragment;
import com.example.mybroadcast.Music_Fragment.MusicshareFragment;
import com.example.mybroadcast.Service.PlayMusicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private RadioGroup radioGroup;
    private RadioButton play, list, share;
    public static MusicListAdapter musicListAdapter;
    public static int currentIndex = -1;
    private StatusReceiver statusReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusReceiver = new StatusReceiver();

        viewPager = findViewById(R.id.vp);
        radioGroup = findViewById(R.id.but);
        play = findViewById(R.id.play);
        list = findViewById(R.id.list);
        share = findViewById(R.id.show);
        Intent intent = new Intent(this, PlayMusicService.class);
        intent.putExtra("CMD", SMPConstants.CMD_GETINFORM);//获取后台状态信息 = 6
        startService(intent);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        play.setChecked(true);
                        break;
                    case 1:
                        list.setChecked(true);
                        break;
                    case 2:
                        share.setChecked(true);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.play:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.list:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.show:
                        viewPager.setCurrentItem(2);
                        break;
                }
            }
        });

    }


    //下面两个是用来注册动态的Receiver
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(statusReceiver);//注销广播接收器
    }

    //注册
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(statusReceiver, new IntentFilter(SMPConstants.ACT_SERVICE_REQUEST_BROADCAST));
        //new IntentFilter(SMPConstants.ACT_SERVICE_REQUEST_BROADCAST)创建过滤器，使其用于特定的
    }
    //onResume方法是Activity第一次创建时 重新加载实例时调用 例如 我打开App搜索第一个界面OnCreate完
    // 就调用onResume 然后切换到下一个界面 第一个界面不finish 按Back键回来时 就调onResume 不调onCreate，
    // 还有就是 App用到一半 有事Home键切出去了 在回来时调onResume
    //在onCreate之后，以及每次横竖切屏，还有从其他界面返回的时候，都会调用onResume()

    //权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 1000) {
            Toast.makeText(this, "请授予外部储存权限", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    class StatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            currentIndex = intent.getIntExtra("index", -1);
            int mpstatus = intent.getIntExtra("status", -1);
            if (fragmentList == null) {
                musicListAdapter = new MusicListAdapter(getApplicationContext(),
                        PlayMusicService.musicBeanArrayList);
                //初始化数据
                fragmentList = new ArrayList<Fragment>();
                MusicPlayFragment f1 = new MusicPlayFragment();
                MusicListFragment f2 = new MusicListFragment();
                MusicshareFragment f3 = new MusicshareFragment();
                fragmentList.add(f1);
                fragmentList.add(f2);
                fragmentList.add(f3);
                viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), 0, fragmentList));
                viewPager.setCurrentItem(0);
            }
            //根据播放状态，显示界面的状态
            ((MusicPlayFragment) fragmentList.get(0)).setMpStatus(mpstatus);

        }
    }

}
