package com.renyuzhuo.chat.share;

import android.app.Activity;
import android.content.Context;

import com.renyuzhuo.chat.R;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 分享，new Share(context)
 */
public class Share {

    Context context;

    public Share(Activity context) {
        this.context = context;
        String url = "http://blog.renyuzhuo.cn/2016/04/25/GraduationProject.html";
        showShare("Chat分享", "RenYuZhuo毕业设计项目", "欢迎给出意见或建议", url, url, url);
    }

    private void showShare(String title, String text, String comment,
                           String url, String titleurl, String siteUrl) {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        oks.setTitle(title);
        oks.setTitleUrl(titleurl);
        oks.setText(text);
        oks.setUrl(url);
        oks.setComment(comment);
        oks.setSite(context.getString(R.string.app_name));
        oks.setSiteUrl(siteUrl);

        oks.show(context);
    }
}
