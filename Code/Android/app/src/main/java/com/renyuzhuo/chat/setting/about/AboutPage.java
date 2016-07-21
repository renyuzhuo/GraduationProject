package com.renyuzhuo.chat.setting.about;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.util.Global;

/**
 * Created by medyo on 3/25/16.
 */
public class AboutPage {
    Context mContext;
    LayoutInflater mInflater;
    String mDescription;
    int mImage = 0;
    boolean mIsRTL = false;
    Typeface mCustomFont;
    private View mView;

    public AboutPage(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mView = mInflater.inflate(R.layout.about_page, null);
    }

    public AboutPage addEmail(String email) {
        Element emailElement = new Element();
        emailElement.setTitle(mContext.getString(R.string.about_contact_me));
        emailElement.setIcon(R.drawable.about_icon_email);
        emailElement.setColor(ContextCompat.getColor(mContext, R.color.about_item_icon_color));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailElement.setIntent(intent);

        addItem(emailElement);
        return this;
    }

    public AboutPage addFacebook(String id) {
        Element facebookElement = new Element();
        facebookElement.setTitle(mContext.getString(R.string.about_facebook));
        facebookElement.setIcon(R.drawable.about_icon_facebook);
        facebookElement.setColor(ContextCompat.getColor(mContext, R.color.facebook_color));
        facebookElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        if (AboutPageUtils.isAppInstalled(mContext, "com.facebook.katana")) {
            intent.setPackage("com.facebook.katana");
            int versionCode = 0;
            try {
                versionCode = mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + "http://facebook.com/" + id);
                intent.setData(uri);
            } else {
                Uri uri = Uri.parse("fb://page/" + id);
                intent.setData(uri);
            }
        } else {
            intent.setData(Uri.parse("http://facebook.com/" + id));
        }

        facebookElement.setIntent(intent);

        addItem(facebookElement);
        return this;
    }

    public AboutPage addBlog() {
        Element facebookElement = new Element();
        facebookElement.setTitle(mContext.getString(R.string.about_blog));

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        intent.setData(Uri.parse(Global.BLOG_CHAT_URL));

        facebookElement.setIntent(intent);

        addItem(facebookElement);
        return this;
    }


    public AboutPage addGitHub(String id) {
        Element gitHubElement = new Element();
        gitHubElement.setTitle(mContext.getString(R.string.about_github));
        gitHubElement.setIcon(R.drawable.about_icon_github);
        gitHubElement.setColor(ContextCompat.getColor(mContext, R.color.github_color));
        gitHubElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(String.format("https://github.com/%s", id)));

        gitHubElement.setIntent(intent);
        addItem(gitHubElement);

        return this;
    }

    public AboutPage addItem(Element element) {
        LinearLayout wrapper = (LinearLayout) mView.findViewById(R.id.about_providers);
        wrapper.addView(createItem(element));
        wrapper.addView(getSeparator(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.about_separator_height)));
        return this;
    }

    public AboutPage setImage(int resource) {
        this.mImage = resource;
        return this;
    }

    public AboutPage addGroup(String name) {

        TextView textView = new TextView(mContext);
        textView.setText(name);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.about_item_text_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.about_group_item_text_size));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (mCustomFont != null) {
            textView.setTypeface(mCustomFont);
        }

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.about_group_text_padding);
        textView.setPadding(padding, padding, padding, padding);

        if (mIsRTL) {
            textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        } else {
            textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        }
        textView.setLayoutParams(textParams);

        ((LinearLayout) mView.findViewById(R.id.about_providers)).addView(textView);
        return this;
    }

    public AboutPage isRTL(boolean value) {
        this.mIsRTL = value;
        return this;
    }

    public View create() {
        TextView description = (TextView) mView.findViewById(R.id.description);
        ImageView image = (ImageView) mView.findViewById(R.id.image);
        if (mImage > 0) {
            image.setImageResource(mImage);
        }

        if (!TextUtils.isEmpty(mDescription)) {
            description.setText(mDescription);
        }

        if (mIsRTL) {
            description.setGravity(Gravity.RIGHT);
        } else {
            description.setGravity(Gravity.LEFT);
        }

        if (mCustomFont != null) {
            description.setTypeface(mCustomFont);
        }

        return mView;
    }

    private View createItem(final Element element) {
        LinearLayout wrapper = new LinearLayout(mContext);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setClickable(true);

        if (element.getIntent() != null) {
            wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mContext.startActivity(element.getIntent());
                    } catch (Exception e) {
                    }
                }
            });

        }

        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
        wrapper.setBackgroundResource(outValue.resourceId);

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.about_text_padding);
        wrapper.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapper.setLayoutParams(wrapperParams);


        TextView textView = new TextView(mContext);
        textView.setTextColor(ContextCompat.getColor(mContext, R.color.about_item_text_color));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.about_item_text_size));
        textView.setTextAppearance(mContext, android.R.style.TextAppearance);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textParams);
        if (mCustomFont != null) {
            textView.setTypeface(mCustomFont);
        }

        ImageView iconView = null;

        if (element.getIcon() != null) {
            iconView = new ImageView(mContext);
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_size);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
            iconView.setLayoutParams(iconParams);
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_padding);
            iconView.setPadding(iconPadding, 0, iconPadding, 0);
            iconView.setImageResource(element.getIcon());

            Drawable wrappedDrawable = DrawableCompat.wrap(iconView.getDrawable());
            wrappedDrawable = wrappedDrawable.mutate();

            if (element.getColor() != null) {
                DrawableCompat.setTint(wrappedDrawable, element.getColor());
            } else {
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, R.color.about_item_icon_color));
            }
        } else {
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_padding);
            textView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        }


        textView.setText(element.getTitle());


        if (mIsRTL) {
            wrapper.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            wrapper.addView(textView);
            if (element.getIcon() != null) {
                wrapper.addView(iconView);
            }

        } else {
            wrapper.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            if (element.getIcon() != null) {
                wrapper.addView(iconView);
            }
            wrapper.addView(textView);
        }

        return wrapper;
    }

    private View getSeparator() {
        return mInflater.inflate(R.layout.about_page_separator, null);
    }
}
