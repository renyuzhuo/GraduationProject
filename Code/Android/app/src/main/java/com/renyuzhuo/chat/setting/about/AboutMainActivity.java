package com.renyuzhuo.chat.setting.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.util.Global;

public class AboutMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Element versionElement = new Element();
        versionElement.setTitle(Global.VERSION);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_taiji_normal)
                .addItem(versionElement)
                .addBlog()
                .addGroup("与我联系")
                .addEmail("renyuzhuo@foxmail.com")
                .addFacebook("profile.php?id=100009966541551")
                .addGitHub("renyuzhuo")
                .create();

        setContentView(aboutPage);
    }
}
