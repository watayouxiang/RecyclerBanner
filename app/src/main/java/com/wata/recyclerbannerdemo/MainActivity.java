package com.wata.recyclerbannerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wata.recyclerbanner.RecyclerBanner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerBanner banner = findViewById(R.id.rb_top);

        BannerAdapter bannerAdapter = new BannerAdapter();
        banner.setAdapter(bannerAdapter);
        List<String> data = getListData();
        bannerAdapter.setData(data);
    }

    private List<String> getListData() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("我是第 " + (i + 1) + " 页");
        }
        return list;
    }
}
