package com.renyuzhuo.chat.fragment.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.model.Team;
import com.renyuzhuo.chat.util.AdapterUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TwoAdapter extends ArrayAdapter<Team> {

    private Context context;
    private List<Team> teams;

    public TwoAdapter(Context context, List<Team> teams) {
        super(context, R.layout.team_item);
        this.context = context;
        this.teams = teams;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = AdapterUtil.getView(context, R.layout.team_item);
            viewHolder.heap = (ImageView) view.findViewById(R.id.heap);
            viewHolder.teamname = (TextView) view.findViewById(R.id.teamname);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (getItem(position) != null) {
            viewHolder.teamname.setText(getItem(position).getTeamname());
            Picasso.with(context).load(getItem(position).getHeap()).placeholder(R.drawable.ic_taiji).error(R.drawable.ic_taiji).into(viewHolder.heap);
        }
        return view;
    }

    private boolean havePosition(int position) {
        if (teams == null || teams.size() < position) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView heap;
        TextView teamname;
    }

}
