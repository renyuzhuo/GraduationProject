package com.renyuzhuo.chat.detail;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.adapter.TextWatcherAdapter;
import com.renyuzhuo.chat.layout.RecordButton;
import com.renyuzhuo.chat.util.Dialog;
import com.renyuzhuo.chat.util.LogUtil;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

public class BaseDetailActivity extends AppCompatActivity implements EmojiconsFragment.OnEmojiconBackspaceClickedListener, EmojiconGridFragment.OnEmojiconClickedListener {

    EditText editText;
    RelativeLayout relativeLayout;
    PullRefreshLayout pullRefreshLayout;
    ListView listView;
    RelativeLayout send, otherMedia;
    View emojicons;
    Context context;
    CheckBox checkBox;
    LinearLayout emojiLinerlayout;
    int page;
    RecordButton mRecordButton = null;
    LinearLayout speakLinerlayout;
    LinearLayout selectMediaLinerlayout;
    CheckBox otherMediaButton;
    RelativeLayout mediaSelecterSpeak;
    RelativeLayout mediaSelecterPicture;

    String name;
    String path;


    public void initViewIds() {
        editText = (EditText) findViewById(R.id.emoji_titile_input);
        relativeLayout = (RelativeLayout) findViewById(R.id.chat_detail);
        checkBox = (CheckBox) findViewById(R.id.emoji_check_menu);
        emojiLinerlayout = (LinearLayout) findViewById(R.id.emoji_linerlayout);
        listView = (ListView) findViewById(R.id.message);
        send = (RelativeLayout) findViewById(R.id.send);
        otherMedia = (RelativeLayout) findViewById(R.id.other_media);
        emojicons = findViewById(R.id.emojicons);
        pullRefreshLayout = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecordButton = (RecordButton) findViewById(R.id.voiceRecordButton);
        speakLinerlayout = (LinearLayout) findViewById(R.id.speak_linerlayout);
        selectMediaLinerlayout = (LinearLayout) findViewById(R.id.select_media_linerlayout);
        otherMediaButton = (CheckBox) findViewById(R.id.other_media_button);
        mediaSelecterSpeak = (RelativeLayout) findViewById(R.id.media_selecter_speak);
        mediaSelecterPicture = (RelativeLayout) findViewById(R.id.media_selecter_picture);
    }

    public void initOnClickListener() {
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_WATER_DROP);

        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollToBottom();
                hideEmojiKeyBoard();
                hideMediaSelector();
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcherAdapter() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(editText.getText())) {
                    send.setVisibility(View.GONE);
                    otherMedia.setVisibility(View.VISIBLE);
                } else {
                    send.setVisibility(View.VISIBLE);
                    otherMedia.setVisibility(View.GONE);
                }
            }
        });

        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.clearFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                return false;
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showEmojiKeyBoard();
                } else {
                    hideEmojiKeyBoard();
                }
            }
        });

        otherMediaButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showMediaSelector();
                } else {
                    hideMediaSelector();
                }
            }
        });

        mediaSelecterSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmojiKeyBoard();
                hideMediaSelector();
                showSpeakFragment();
            }
        });

        mediaSelecterPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog.setPhotoDialog(BaseDetailActivity.this);
            }
        });
    }

    private static Activity showActivity1;
    private static Activity showActivity2;

    public static void keepOneDetailActivity(Activity activity) {
        LogUtil.log("新的聊天窗口");
        if (showActivity1 != null) {
            showActivity1.finish();
            showActivity2 = activity;
            showActivity1 = null;
        } else {
            showActivity1 = activity;
            if (showActivity2 != null) {
                showActivity2.finish();
                showActivity2 = null;
            }
        }
    }

    public void scrollToBottom() {
    }

    public void refresh() {
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(editText);
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(editText, emojicon);
    }

    public void showEmojiKeyBoard() {
        LogUtil.log("showEmojiKeyBoard");
        hideSoftKeyboard();
        emojiLinerlayout.setVisibility(View.VISIBLE);
        selectMediaLinerlayout.setVisibility(View.GONE);
        hideSpeakFragment();
        checkBox.setChecked(true);
        otherMediaButton.setChecked(false);
    }

    public void hideEmojiKeyBoard() {
        LogUtil.log("hideEmojiKeyBoard");
        hideSoftKeyboard();
        emojiLinerlayout.setVisibility(View.GONE);
        hideSpeakFragment();
        checkBox.setChecked(false);
    }

    private void showMediaSelector() {
        LogUtil.log("showMediaSelector");
        hideSoftKeyboard();
        selectMediaLinerlayout.setVisibility(View.VISIBLE);
        emojiLinerlayout.setVisibility(View.GONE);
        hideSpeakFragment();
        otherMediaButton.setChecked(true);
        checkBox.setChecked(false);
    }

    private void hideMediaSelector() {
        LogUtil.log("hideMediaSelector");
        hideSoftKeyboard();
        selectMediaLinerlayout.setVisibility(View.GONE);
        hideSpeakFragment();
        otherMediaButton.setChecked(false);
    }

    private void showSpeakFragment() {
        hideSoftKeyboard();
        speakLinerlayout.setVisibility(View.VISIBLE);
    }

    private void hideSpeakFragment() {
        hideSoftKeyboard();
        speakLinerlayout.setVisibility(View.GONE);
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftKeyboard() {
        ((InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                editText.getWindowToken(), 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        }
        return super.onKeyDown(keyCode, event);
    }
}
