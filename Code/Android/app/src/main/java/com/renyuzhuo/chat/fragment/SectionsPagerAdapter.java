package com.renyuzhuo.chat.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    Context context;
    private List<Fragment> fragments;

    public SectionsPagerAdapter(Context context, FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        ChatApplication.page = position;
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.chat);
            case 1:
                return context.getResources().getString(R.string.contract);
            case 2:
                return context.getResources().getString(R.string.group);
            case 3:
                return context.getResources().getString(R.string.feedback);
        }
        return null;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        LogUtil.log("点击Page:" + position);
        ChatApplication.page = position;
    }
}
