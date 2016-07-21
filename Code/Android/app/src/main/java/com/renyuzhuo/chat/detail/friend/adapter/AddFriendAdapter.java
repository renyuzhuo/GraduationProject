package com.renyuzhuo.chat.detail.friend.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.friend.FriendDetailActivity;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddFriendAdapter extends ArrayAdapter<UserBrief> {

    Context context;
    List<UserBrief> userBriefs;

    public AddFriendAdapter(Context context, List<UserBrief> userBriefs) {
        super(context, R.layout.friend_item);
        this.context = context;
        this.userBriefs = userBriefs;
    }

    @Override
    public int getCount() {
        if (userBriefs == null) {
            return 0;
        } else {
            return userBriefs.size();
        }
    }

    @Override
    public UserBrief getItem(int position) {
        if (havePosition(position)) {
            return userBriefs.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (havePosition(position)) {
            return userBriefs.get(position).hashCode();
        } else {
            return 0;
        }
    }

    private boolean havePosition(int position) {
        if (userBriefs == null || userBriefs.size() < position) {
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
        final UserBrief u = userBriefs.get(position);
        Picasso.with(context).load(userBriefs.get(position).getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(viewHolder.heap);
        viewHolder.remark.setText(userBriefs.get(position).getNickname());
        viewHolder.add.setVisibility(View.VISIBLE);

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            UserBrief userBrief = u;

            @Override
            public void onClick(View v) {
                FriendDetailActivity.startActivity(context, null, userBrief);
            }
        });

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(List<UserBrief> userBriefs) {
        this.userBriefs = userBriefs;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView heap;
        TextView remark;
        Button add;
    }

}
