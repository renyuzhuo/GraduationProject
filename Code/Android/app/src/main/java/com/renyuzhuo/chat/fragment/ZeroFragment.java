package com.renyuzhuo.chat.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.github.nkzawa.emitter.Emitter;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatService;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.ChatBaseDetailActivity;
import com.renyuzhuo.chat.detail.ChatDetailActivity;
import com.renyuzhuo.chat.fragment.Adapter.ZeroAdapter;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.sql.MessageSQL;
import com.renyuzhuo.chat.sql.MessageToReadSQL;
import com.renyuzhuo.chat.sql.UserBriefSQL;
import com.renyuzhuo.chat.util.Anim;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.List;

public class ZeroFragment extends Fragment {

    SwipeMenuListView listView;
    PullRefreshLayout pullRefreshLayout;
    private static ZeroAdapter adapter;
    static List<UserBrief> userBriefs;
    UserBrief userBrief;

    public ZeroFragment() {
        ChatService.mSocket.on(ChatApplication.getUserId() + "", fresh);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        initViewIds(rootView);

        userBriefs = UserBriefSQL.getAllUserBrief();
        if (adapter == null) {
            adapter = new ZeroAdapter(getActivity(), userBriefs);
        }
        listView.setAdapter(adapter);

        initOnClickListener();

        return rootView;
    }

    private void initViewIds(View rootView) {
        pullRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        listView = (SwipeMenuListView) rootView.findViewById(R.id.xlistview);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index) {
                switch (index) {
                    case 0: {
                        int userid = userBriefs.get(position).getId();
                        deleteMessageOfSb(userid);
                        break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * 删除和某人聊天记录
     *
     * @param userid 用户ID
     */
    private void deleteMessageOfSb(int userid) {
        UserBriefSQL.delUserBriefMessageByUserBriefId(userid);
        MessageSQL.delMessageByOtherId(userid);
        MessageToReadSQL.updateZeroToDb(userid);
        userBriefs = UserBriefSQL.getAllUserBrief();
        if (adapter != null) {
            adapter.setData(userBriefs);
        }
    }

    private void initOnClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                userBrief = userBriefs.get(position);
                if (userBrief == null) {
                    return;
                }
                LogUtil.log(userBrief.toString());
                Friend friend;
                if ((friend = FriendSQL.getFriendByYourId(userBrief.getId())) != null) {
                    ChatBaseDetailActivity.chatFriend = friend;
                    ChatBaseDetailActivity.chatUserBrief = null;
                } else {
                    ChatBaseDetailActivity.chatUserBrief = userBrief;
                    ChatBaseDetailActivity.chatFriend = null;
                }

                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                startActivity(intent);
                Anim.in(getActivity());
            }
        });

        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        pullRefreshLayout.setRefreshStyle(PullRefreshLayout.STYLE_CIRCLES);
    }

    Emitter.Listener fresh = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userBriefs = UserBriefSQL.getAllUserBrief();
                        adapter.setData(userBriefs);
                    }
                });
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.log("ZeroFragment onResume");
        refresh();
    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            SwipeMenuItem deleteItem = new SwipeMenuItem(getContext());
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
            deleteItem.setWidth(150);
            deleteItem.setIcon(R.drawable.ic_delete);
            menu.addMenuItem(deleteItem);
        }
    };

    public void refresh() {
        LogUtil.log("onRefresh");
        userBriefs = UserBriefSQL.getAllUserBrief();
        adapter.setData(userBriefs);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    public static void publicRefresh() {
        if (userBriefs != null && adapter != null) {
            userBriefs = UserBriefSQL.getAllUserBrief();
            adapter.setData(userBriefs);
        }
    }

}