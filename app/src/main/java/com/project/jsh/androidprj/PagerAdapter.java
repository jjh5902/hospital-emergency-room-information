package com.project.jsh.androidprj;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {

    final private ArrayList<Fragment> fragments = new ArrayList<>();
    final private ArrayList<String> titles = new ArrayList<>();
    private static int PAGE_NUMBER = 2;

    //===================================================================
    public PagerAdapter(FragmentManager fm) {
        super(fm);

        // 초기화
        PagerOne pagerOne = new PagerOne();
        PagerTwo pagerTwo = new PagerTwo();
        fragments.add(pagerOne);
        fragments.add(pagerTwo);

        titles.add("지도");
        titles.add("응급실 정보");

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
