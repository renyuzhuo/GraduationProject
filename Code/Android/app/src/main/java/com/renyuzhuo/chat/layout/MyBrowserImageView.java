package com.renyuzhuo.chat.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.renyuzhuo.chat.util.LogUtil;

/**
 * Created by RENYUZHUO on 2016/4/25.
 */
public class MyBrowserImageView extends ImageView {
    public MyBrowserImageView(Context context) {
        super(context);
    }

    public MyBrowserImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyBrowserImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final void openMySelfBrowser(View view) {
        LogUtil.log("点击了");
    }

}
