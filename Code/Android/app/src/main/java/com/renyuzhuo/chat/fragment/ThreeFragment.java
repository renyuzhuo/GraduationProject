package com.renyuzhuo.chat.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.widget.PullRefreshLayout;
import com.renyuzhuo.chat.ChatMainActivity;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.fragment.Adapter.ThreeAdapter;
import com.renyuzhuo.chat.model.Feedback;
import com.renyuzhuo.chat.sql.FeedBackSQL;
import com.renyuzhuo.chat.util.LogUtil;

import java.util.List;

public class ThreeFragment extends Fragment {

    SwipeMenuListView listView;
    PullRefreshLayout pullRefreshLayout;
    private static ThreeAdapter threeAdapter;
    List<Feedback> feedbacks;

    public ThreeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);

        initViewIds(rootView);
        getFeedbackMessages();
        initOnClickListener();
        return rootView;
    }

    private void getFeedbackMessages() {
        feedbacks = FeedBackSQL.getAllFeedBacks();
        threeAdapter = new ThreeAdapter(getActivity(), feedbacks);
        listView.setAdapter(threeAdapter);
    }


    private void initViewIds(View rootView) {
        pullRefreshLayout = (PullRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        listView = (SwipeMenuListView) rootView.findViewById(R.id.xlistview);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu swipeMenu, int index) {
                switch (index) {
                    case 0: {
                        break;
                    }
                    case 1: {
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
                LogUtil.log("positon:" + position);
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

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.log("ThreeFragment onResume");
    }

    public void refresh() {
        LogUtil.log("onRefresh");
        ChatMainActivity.getFeedbackMessages();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getFeedbackMessages();
                pullRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

}