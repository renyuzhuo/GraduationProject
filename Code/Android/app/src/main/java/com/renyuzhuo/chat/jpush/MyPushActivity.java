package com.renyuzhuo.chat.jpush;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.renyuzhuo.chat.util.Global;

import cn.jpush.android.ui.PushActivity;

public class MyPushActivity extends PushActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void openMySelfBrowser(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        intent.setData(Uri.parse(Global.BLOG_CHAT_URL));
        startActivity(intent);
    }
}
