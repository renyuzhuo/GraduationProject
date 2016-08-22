package com.renyuzhuo.chat.fragment.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.renyuzhuo.chat.util.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ZeroAdapter extends ArrayAdapter<UserBrief> {

    Context context;
    List<UserBrief> userBriefs;

    public ZeroAdapter(Context context, List<UserBrief> userBriefs) {
        super(context, R.layout.message_item);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            view = AdapterUtil.getView(context, R.layout.message_item);
            viewHolder = new ViewHolder();
            viewHolder.heap = (ImageView) view.findViewById(R.id.heap);
            viewHolder.remark = (TextView) view.findViewById(R.id.remark);
            viewHolder.message = (TextView) view.findViewById(R.id.message);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.messageUnread = (TextView) view.findViewById(R.id.message_unread);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (ChatApplication.messageToReads != null && ChatApplication.messageToReads.containsKey(userBriefs.get(position).getId())) {
            viewHolder.messageUnread.setVisibility(View.VISIBLE);
            viewHolder.messageUnread.setText(String.valueOf(ChatApplication.messageToReads.get(userBriefs.get(position).getId())));
        } else {
            viewHolder.messageUnread.setVisibility(View.GONE);
        }

        viewHolder.remark.setText(userBriefs.get(position).getNickname());
        viewHolder.message.setText(userBriefs.get(position).getMessage());
        viewHolder.time.setText(DateUtils.dateToRead(userBriefs.get(position).getTime()));
        Picasso.with(context).load(userBriefs.get(position).getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(viewHolder.heap);

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
        TextView messageUnread;
        ImageView heap;
        TextView remark;
        TextView message;
        TextView time;
    }

}
