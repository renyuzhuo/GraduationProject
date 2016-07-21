package com.renyuzhuo.chat.detail.team.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.team.TeamInformationActivity;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddTeamAdapter extends ArrayAdapter<Team> {

    Context context;
    List<Team> teams;

    public AddTeamAdapter(Context context, List<Team> userBriefs) {
        super(context, R.layout.friend_item);
        this.context = context;
        this.teams = userBriefs;
    }

    @Override
    public int getCount() {
        if (teams == null) {
            return 0;
        } else {
            return teams.size();
        }
    }

    @Override
    public Team getItem(int position) {
        if (havePosition(position)) {
            return teams.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if (havePosition(position)) {
            return teams.get(position).hashCode();
        } else {
            return 0;
        }
    }

    private boolean havePosition(int position) {
        if (teams == null || teams.size() < position) {
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
        final Team t = teams.get(position);
        Picasso.with(context).load(teams.get(position).getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(viewHolder.heap);
        viewHolder.remark.setText(teams.get(position).getTeamname());
        viewHolder.add.setVisibility(View.VISIBLE);

        viewHolder.add.setOnClickListener(new View.OnClickListener() {
            Team team = t;

            @Override
            public void onClick(View v) {
                TeamInformationActivity.startActivity(context, team);
            }
        });

        return view;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setData(List<Team> teams) {
        this.teams = teams;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView heap;
        TextView remark;
        Button add;
    }

}
