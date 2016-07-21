package com.renyuzhuo.chat.detail.friend.adapter;

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
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteFriendAdapter extends ArrayAdapter<Friend> {

    Context context;
    static List<Friend> friends;

    public InviteFriendAdapter(Context context, List<Friend> friends) {
        super(context, R.layout.friend_item);
        this.context = context;
        this.friends = friends;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = AdapterUtil.getView(context, R.layout.friend_item);
            viewHolder.heap = (ImageView) view.findViewById(R.id.heap);
            viewHolder.remark = (TextView) view.findViewById(R.id.remark);
            viewHolder.submit = (Button) view.findViewById(R.id.submit);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (getItem(position) != null) {
            viewHolder.remark.setText(getItem(position).getRemark());
            Picasso.with(context).load(getItem(position).getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(viewHolder.heap);
            viewHolder.submit.setVisibility(View.VISIBLE);

            final int friendlistid = friends.get(position).getMyFriendListId();

            viewHolder.submit.setOnClickListener(new View.OnClickListener() {

                int flistid = friendlistid;

                @Override
                public void onClick(View v) {
                    Map<String, String> map = new HashMap<>();
                    map.put("token", ChatApplication.getToken());
                    map.put("myId", String.valueOf(ChatApplication.getUserId()));
                    map.put("flistid", String.valueOf(flistid));

                    NetOption.postDataToUrl(Global.SUBMIT_INVITE_FRIEND_URL, map, new NetOptionResponse() {
                        @Override
                        public Response.Listener<JSONObject> success() {
                            return new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    LogUtil.log("成功");
                                    FriendSQL.addFriend(flistid);
                                    friends = FriendSQL.getFriendsOfInviteMe();
                                    notifyDataSetChanged();
                                }
                            };
                        }
                    });
                }
            });
        }
        return view;
    }

    public void setFriends(List<Friend> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private boolean havePosition(int position) {
        if (friends == null || friends.size() < position) {
            return false;
        } else {
            return true;
        }
    }

    class ViewHolder {
        ImageView heap;
        TextView remark;
        Button submit;
    }

}
