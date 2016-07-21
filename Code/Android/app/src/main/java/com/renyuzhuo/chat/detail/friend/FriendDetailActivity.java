package com.renyuzhuo.chat.detail.friend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.ChatBaseDetailActivity;
import com.renyuzhuo.chat.detail.ChatDetailActivity;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.sql.UserBriefSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.ToastUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FriendDetailActivity extends BaseFriendActivity {

    ImageView myHeap;
    TextView nickname;
    Context context;
    public static Friend chatFriend = null;
    public static UserBrief chatUserBrief = null;
    public static int UPDATE_REMARK_CODE = 0;
    public static int UPDATE_REMARK_SUCCESS_CODE = 1;
    public static Button sendMessage;
    Button addFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        context = this;
        keepOneFriendActivity(this);

        if (FriendSQL.getFriendByYourId(getId()) != null) {
            chatFriend = FriendSQL.getFriendByYourId(getId());
        }
        if (UserBriefSQL.getUserBriefById(getId()) != null) {
            chatUserBrief = UserBriefSQL.getUserBriefById(getId());
        }

        initFindViewByIds();
        initOnClickListener();
        Picasso.with(this).load(getHeap()).placeholder(R.drawable.ic_taiji).error(R.drawable.ic_taiji).into(myHeap);
        nickname.setText(getName());
    }

    private void initFindViewByIds() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myHeap = (ImageView) findViewById(R.id.my_heap);
        nickname = (TextView) findViewById(R.id.username);
        sendMessage = (Button) findViewById(R.id.send_message);
        addFriend = (Button) findViewById(R.id.add_friend);
        if (chatFriend != null && chatFriend.getState() != null && chatFriend.getState().equals("friend")) {
            sendMessage.setVisibility(View.VISIBLE);
        } else {
            addFriend.setVisibility(View.VISIBLE);
        }
        if (getId() == ChatApplication.getUserId()) {
            addFriend.setVisibility(View.GONE);
        }
    }

    private void initOnClickListener() {
        myHeap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRemark();
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatBaseDetailActivity.chatFriend = chatFriend;
                ChatBaseDetailActivity.chatUserBrief = chatUserBrief;
                Intent intent = new Intent(context, ChatDetailActivity.class);
                context.startActivity(intent);
                Anim.in(context);
            }
        });

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
                map.put("token", ChatApplication.getToken());
                map.put("sid", String.valueOf(getId()));
                map.put("myId", String.valueOf(ChatApplication.getUserId()));
                map.put("remark", getName());
                map.put("state", "invite");

                NetOption.postDataToUrl(Global.ADD_FRIEND_URL, map, new NetOptionResponse() {
                    @Override
                    public Response.Listener<JSONObject> success() {
                        return new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                LogUtil.log("添加成功");
                                ToastUtil.showToast(context, getResources().getString(R.string.add_friend_success));
                            }
                        };
                    }

                    @Override
                    public Response.ErrorListener error() {
                        return new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 401) {
                                    ToastUtil.showToast(context, getResources().getString(R.string.add_friend_err));
                                }
                            }
                        };
                    }
                });
            }
        });
    }

    private int getId() {
        if (chatFriend == null) {
            if (chatUserBrief == null) {
                return -1;
            } else {
                return chatUserBrief.getId();
            }
        } else {
            return chatFriend.getYourId();
        }
    }

    public String getHeap() {
        if (chatFriend == null) {
            if (chatUserBrief == null) {
                return "heaperr";
            } else {
                return chatUserBrief.getHeap();
            }
        } else {
            return chatFriend.getHeap();
        }
    }

    public String getName() {
        if (chatFriend == null) {
            if (chatUserBrief == null) {
                return "Chat用户";
            } else {
                return chatUserBrief.getNickname();
            }
        } else {
            return chatFriend.getRemark();
        }
    }

    private void updateRemark() {
        if (chatFriend != null) {
            Intent intent = new Intent(this, UpdateRemarkActivity.class);
            intent.putExtra("remark", getName());
            intent.putExtra("chatFriendId", chatFriend.getMyFriendListId());
            startActivityForResult(intent, UPDATE_REMARK_CODE);
            Anim.in(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static void startActivity(Context context, Friend friend, UserBrief userBrief) {
        FriendDetailActivity.chatFriend = friend;
        FriendDetailActivity.chatUserBrief = userBrief;
        Intent intent = new Intent(context, FriendDetailActivity.class);
        context.startActivity(intent);
        Anim.in(context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UPDATE_REMARK_SUCCESS_CODE) {
            nickname.setText(data.getStringExtra("remark") != null ? data.getStringExtra("remark") : getResources().getString(R.string.chat_user));
        }
    }
}
