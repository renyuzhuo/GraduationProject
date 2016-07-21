package com.renyuzhuo.chat.fragment.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OneAdapter extends ArrayAdapter<Friend> {

    Context context;
    List<Friend> friends;

    public OneAdapter(Context context, List<Friend> friends) {
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
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = AdapterUtil.getView(context, R.layout.friend_item);
            viewHolder.heap = (ImageView) view.findViewById(R.id.heap);
            viewHolder.remark = (TextView) view.findViewById(R.id.remark);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (getItem(position) != null) {
            viewHolder.remark.setText(getItem(position).getRemark());
            Picasso.with(context).load(getItem(position).getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(viewHolder.heap);
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
    }

}
