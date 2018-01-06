package com.project.jsh.androidprj;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면 구성 (탭 + 뷰페이저)
        final PagerAdapter pagerAdapter = new PagerAdapter(
                getSupportFragmentManager()
        );
        final ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }
}
