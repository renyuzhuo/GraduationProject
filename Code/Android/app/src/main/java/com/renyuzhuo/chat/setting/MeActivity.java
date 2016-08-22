package com.renyuzhuo.chat.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Dialog;
import com.renyuzhuo.chat.util.picture.MePictureUtil;
import com.squareup.picasso.Picasso;

public class MeActivity extends AppCompatActivity {

    ImageView myHeap;
    TextView nickname;
    Button qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);

        initFindViewByIds();
        initOnClickListener();
        Picasso.with(this).load(ChatApplication.getHeap()).placeholder(R.drawable.ic_taiji_normal).error(R.drawable.ic_taiji_normal).into(myHeap);
        nickname.setText(ChatApplication.getNickname());
    }

    private void initFindViewByIds() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myHeap = (ImageView) findViewById(R.id.my_heap);
        nickname = (TextView) findViewById(R.id.username);
        qrcode = (Button) findViewById(R.id.qrcode);
        qrcode.setVisibility(View.VISIBLE);
    }

    private void initOnClickListener() {
        myHeap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setPhotoDialog(MeActivity.this);
            }
        });
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNickname();
            }
        });
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MeActivity.this, QrCodeActivity.class);
                startActivity(intent);
                Anim.in(MeActivity.this);
            }
        });
    }

    private void updateNickname() {
        Intent intent = new Intent(this, UpdateNicknameActivity.class);
        startActivity(intent);
        Anim.in(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nickname.setText(ChatApplication.getNickname());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        new MePictureUtil(myHeap).onActivityResult(MeActivity.this, requestCode, resultCode, data);
    }

    public static void startMeActivity(Context context) {
        Intent intent = new Intent(context, MeActivity.class);
        context.startActivity(intent);
        Anim.in(context);
    }
}
