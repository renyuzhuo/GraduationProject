package com.renyuzhuo.chat.fragment.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.friend.FriendDetailActivity;
import com.renyuzhuo.chat.model.Feedback;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.net.NetOption;
import com.renyuzhuo.chat.net.NetOptionResponse;
import com.renyuzhuo.chat.setting.MeActivity;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.renyuzhuo.chat.util.DateUtils;
import com.renyuzhuo.chat.util.Global;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreeAdapter extends ArrayAdapter<Feedback> {

    private Context context;
    private List<Feedback> feedbacks;

    public ThreeAdapter(Context context, List<Feedback> feedbacks) {
        super(context, R.layout.feedback_item);
        this.context = context;
        this.feedbacks = feedbacks;
    }

    @Override
    public int getCount() {
        if (feedbacks == null) {
            return 0;
        } else {
            return feedbacks.size();
        }
    }

    @Override
    public Feedback getItem(int position) {
        if (havePosition(position)) {
            return feedbacks.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (havePosition(position)) {
            return feedbacks.get(position).hashCode();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = AdapterUtil.getView(context, R.layout.feedback_item);
            viewHolder.nickname = (TextView) view.findViewById(R.id.nickname);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.message = (TextView) view.findViewById(R.id.message);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (getItem(position) != null) {
            if(getItem(position).getUserinfo_id() == ChatApplication.getUserId()){
                viewHolder.nickname.setText(context.getResources().getString(R.string.me));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MeActivity.startMeActivity(context);
                    }
                });
            } else {
                final Friend friend = FriendSQL.getFriendByYourId(getItem(position).getUserinfo_id());
                if (friend != null) {
                    viewHolder.nickname.setText(friend.getRemark());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FriendDetailActivity.startActivity(context, friend, null);
                        }
                    });
                } else {
                    viewHolder.nickname.setText(getItem(position).getNickname());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, String> map = new HashMap<>();
                            map.put("token", ChatApplication.getToken());
                            map.put("myId", String.valueOf(ChatApplication.getUserId()));
                            map.put("sid", String.valueOf(getItem(position).getUserinfo_id()));
                            NetOption.postDataToUrl(Global.FRIEND_URL, map, new NetOptionResponse() {
                                @Override
                                public Response.Listener<JSONObject> success() {
                                    return new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                JSONArray userinfos = response.getJSONArray("userinfos");
                                                UserBrief userBrief;
                                                for (int i = 0; i < userinfos.length(); i++) {
                                                    JSONObject userinfo = userinfos.getJSONObject(i);
                                                    userBrief = new UserBrief(userinfo.getInt("id"), userinfo.getString("nickname"), false, null, null, userinfo.getString("heap"));
                                                    FriendDetailActivity.startActivity(context, null, userBrief);
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                }
                            });
                        }
                    });
                }
            }

            viewHolder.time.setText(DateUtils.dateToRead(getItem(position).getTime()));
            viewHolder.message.setText(getItem(position).getMessage());
        }
        return view;
    }

    private boolean havePosition(int position) {
        if (feedbacks == null || feedbacks.size() < position) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    class ViewHolder {
        TextView nickname;
        TextView time;
        TextView message;
    }

}
