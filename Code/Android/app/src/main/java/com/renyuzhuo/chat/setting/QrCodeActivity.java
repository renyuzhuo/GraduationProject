package com.renyuzhuo.chat.setting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.Me;
import com.renyuzhuo.chat.util.JsonUtils;
import com.renyuzhuo.chat.util.ZXingUtil;

public class QrCodeActivity extends AppCompatActivity {

    private Bitmap img;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.img);
        Me me = new Me(ChatApplication.getUserId(), ChatApplication.getUsername(), ChatApplication.getNickname(), ChatApplication.getHeap());
        img = ZXingUtil.create2DCoderBitmap(JsonUtils.toJson(me), 800, 800);
        imageView.setImageBitmap(img);
    }

}