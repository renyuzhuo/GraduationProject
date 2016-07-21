package com.renyuzhuo.chat.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.ChatMainActivity;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.ChatBaseDetailActivity;
import com.renyuzhuo.chat.detail.friend.FriendDetailActivity;
import com.renyuzhuo.chat.fragment.Adapter.OneAdapter;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneFragment extends Fragment {

    PullRefreshLayout pullRefreshLayout;
    SwipeMenuListView listView;
    List<Friend> friends;
    private static OneAdapter oneAdapter;

    public OneFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        initViewIds(rootView);

        friends = FriendSQL.getFriends("friend");
        LogUtil.log("friends", friends.toString());
        if (oneAdapter == null) {
            oneAdapter = new OneAdapter(getActivity(), friends);
        }
        listView.setAdapter(oneAdapter);

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
                        //删除好友
                        int chatFriendListId = friends.get(position).getMyFriendListId();
                        Map<String, String> map = new HashMap<>();
                        map.put("myId", String.valueOf(ChatApplication.getUserId()));
                        map.put("token", ChatApplication.getToken());
                        map.put("toId", String.valueOf(friends.get(position).getYourId()));
                        map.put("chatFriendListId", String.valueOf(chatFriendListId));
                        NetOption.postDataToUrl(Global.DELETE_FRIEND_URL, map, new NetOptionResponse() {
                            @Override
                            public Response.Listener<JSONObject> success() {
                                return new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        refresh();
                                    }
                                };
                            }
                        });
                        break;
                    }
                }
                return false;
            }
        });
    }

    private void initOnClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatBaseDetailActivity.chatFriend = friends.get(position);
                if (ChatBaseDetailActivity.chatFriend == null) {
                    return;
                }
                FriendDetailActivity.startActivity(getActivity(), ChatBaseDetailActivity.chatFriend, null);
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

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.log("OneFragment onResume");
        refreshFriendList();
    }

    private void refreshFriendList() {
        friends = FriendSQL.getFriends("friend");
        oneAdapter.setFriends(friends);
    }

    public void refresh() {
        LogUtil.log("onRefresh");

        ((ChatMainActivity) getActivity()).getFriendList();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pullRefreshLayout.setRefreshing(false);
                friends = FriendSQL.getFriends("friend");
                oneAdapter.setFriends(friends);
            }
        }, 1000);
    }
}