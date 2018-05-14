package com.example.wen.xiamitablayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.wen.xiamitablayout.fragment.LazyFragment;
import com.example.wen.xiamitablayout.fragment.OneFragment;
import com.example.wen.xiamitablayout.fragment.TwoFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private XiaMiTabLayout viewpager_tab;
    private ViewPager viewpager;
    private ArrayList<String> list = new ArrayList<>();
    private MyViewPager myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
    }

    private void initData() {
        list.clear();
        list.add("推荐");
        list.add("影视");
        list.add("综艺");
        list.add("幼儿");
        list.add("恐怖");
    }

    private void initView() {

        viewpager_tab = findViewById(R.id.viewpager_tab);
        viewpager = findViewById(R.id.viewpager);

        myViewPagerAdapter = new MyViewPager(getSupportFragmentManager(), list);
        viewpager_tab.setDataList(list);
        viewpager.setAdapter(myViewPagerAdapter);
        viewpager_tab.setupWithViewPager(viewpager);
        //初始化
        viewpager.setCurrentItem(0);
    }


    public class MyViewPager extends FragmentStatePagerAdapter {
        private List<String> mDataList;
        private boolean[] mInit;
        private Map<Integer, LazyFragment> baseFragmentMap = new HashMap<>();

        public MyViewPager(FragmentManager fm, List<String> list) {
            super(fm);
            mDataList = list;
            mInit = new boolean[mDataList.size()];
        }

        @Override
        public Fragment getItem(int position) {
            LazyFragment fragment = baseFragmentMap.get(position);
            if (fragment == null) {
                if (position % 2 == 0) {
                    fragment = OneFragment.newInstance();
                } else {
                    fragment = TwoFragment.newInstance(mDataList.get(position));
                }
                baseFragmentMap.put(position, fragment);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return mDataList.size();
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (!mInit[position]) {
                LazyFragment lazyFragment = (LazyFragment) object;
                if (lazyFragment.getTargetView() != null) {
                    mInit[position] = true;
                    lazyFragment.initNet();
                }
            }
        }
    }
}
