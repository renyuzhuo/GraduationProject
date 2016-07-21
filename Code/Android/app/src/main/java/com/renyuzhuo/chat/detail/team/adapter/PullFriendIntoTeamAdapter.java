package com.renyuzhuo.chat.detail.team.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.renyuzhuo.chat.util.Global;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RENYUZHUO on 2016/5/3.
 */
public class PullFriendIntoTeamAdapter extends ArrayAdapter<Friend> {

    Context context;
    List<Friend> friends;
    Team team;

    public PullFriendIntoTeamAdapter(Context context, List<Friend> friends, Team team) {
        super(context, R.layout.friend_item);
        this.context = context;
        this.friends = friends;
        this.team = team;
    }

    @Override
    public int getCount() {
        if (friends == null) {
            return 0;
        } else {
            return friends.size();
        }
    }

    @Override
    public Friend getItem(int position) {
        if (havePosition(position)) {
            return friends.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (havePosition(position)) {
            return friends.get(position).hashCode();
        } else {
            return 0;
        }
    }

    private boolean havePosition(int position) {
        if (friends == null || friends.size() < position) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            view = AdapterUtil.getView(context, R.layout.friend_item);
            viewHolder = new ViewHolder();
            viewHolder.heap = (ImageView) view.findViewById(R.id.heap);
            viewHolder.remark = (TextView) view.findViewById(R.id.remark);
            viewHolder.add = (Button) view.findViewById(R.id.add);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Friend t = friends.get(position);
        Picasso.with(context).load(friends.get(position).getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(viewHolder.heap);
        viewHolder.remark.setText(friends.get(position).getYourNickname());
        viewHolder.add.setVisibility(View.VISIBLE);

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            Friend friend = t;

            @Override
            public void onClick(View v) {
                Map map = new HashMap();
                map.put("token", ChatApplication.getToken());
                map.put("myId", String.valueOf(ChatApplication.getUserId()));
                map.put("teamId", String.valueOf(team.getTeamid()));
                map.put("otherId", String.valueOf(friend.getYourId()));
                NetOption.postDataToUrl(Global.ADD_FRIEND_TO_TEAM, map, new NetOptionResponse() {
                    @Override
                    public Response.Listener<JSONObject> success() {
                        return new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                            }
                        };
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(List<Friend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView heap;
        TextView remark;
        Button add;
    }

}
