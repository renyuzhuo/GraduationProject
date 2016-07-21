package com.renyuzhuo.chat.detail.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.renyuzhuo.chat.ChatApplication;
import com.renyuzhuo.chat.R;
import com.renyuzhuo.chat.detail.friend.FriendDetailActivity;
import com.renyuzhuo.chat.model.Friend;
import com.renyuzhuo.chat.model.TeamMessage;
import com.renyuzhuo.chat.model.UserBrief;
import com.renyuzhuo.chat.setting.MeActivity;
import com.renyuzhuo.chat.sql.FriendSQL;
import com.renyuzhuo.chat.sql.UserBriefSQL;
import com.renyuzhuo.chat.util.DateUtils;
import com.renyuzhuo.chat.util.Dialog;
import com.renyuzhuo.chat.util.Global;
import com.renyuzhuo.chat.util.LogUtil;
import com.renyuzhuo.chat.util.OkHttpClientManager;
import com.renyuzhuo.chat.util.PlayMedia;
import com.renyuzhuo.chat.util.SoftkeyboardUtil;
import com.renyuzhuo.chat.util.ToastUtil;
import com.renyuzhuo.chat.util.ZipUtil;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class TeamDetailAdapter extends ArrayAdapter<TeamMessage> {

    Context context;
    List<TeamMessage> teamMessages;
    int myId;
    ViewHold hode;

    public TeamDetailAdapter(Context context, List<TeamMessage> teamMessages) {
        super(context, R.layout.message_detail_item);
        this.context = context;
        this.teamMessages = teamMessages;
        this.myId = ChatApplication.getUserId();
    }

    public interface IMsgViewType {
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }

    @Override
    public int getCount() {
        if (teamMessages != null) {
            return teamMessages.size();
        }
        return 0;
    }

    @Override
    public TeamMessage getItem(int position) {
        if (teamMessages != null) {
            return teamMessages.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (teamMessages != null) {
            return teamMessages.get(position).hashCode();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        TeamMessage message = teamMessages.get(position);

        if (message.getFromuser() == myId) {
            return IMsgViewType.IMVT_TO_MSG;
        } else {
            return IMsgViewType.IMVT_COM_MSG;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TeamMessage teamMessage = getItem(position);
        boolean isToMsg = (teamMessage.getFromuser() == myId);
        View v = convertView;
        if (v == null) {
            hode = new ViewHold();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (isToMsg) {
                v = inflater.inflate(R.layout.charting_item_msg_right, null);
            } else {
                v = inflater.inflate(R.layout.charting_item_msg_left, null);
            }

            hode.heap = (ImageView) v.findViewById(R.id.heap);
            hode.msg = (EmojiconTextView) v.findViewById(R.id.chatmessage);
            hode.time = (TextView) v.findViewById(R.id.sendtime);
            hode.chatImgMessage = (ImageView) v.findViewById(R.id.chat_img_message);
            hode.redIcon = (ImageView) v.findViewById(R.id.red_icon);
            hode.sendingIcon = (GifImageView) v.findViewById(R.id.sending_icon);
            hode.playIcon = (ImageView) v.findViewById(R.id.play_icon);

            v.setTag(hode);

        } else {
            hode = (ViewHold) v.getTag();
        }

        LogUtil.log(getItem(position).getType() + "");
        switch (getItem(position).getType()) {
            case 1: {
                hode.msg.setText(getItem(position).getMessage());
                hode.msg.setVisibility(View.VISIBLE);
                hode.chatImgMessage.setVisibility(View.GONE);
                hode.playIcon.setVisibility(View.GONE);
                break;
            }
            case 2: {
                LogUtil.log("图片:" + getItem(position).getPath());
                Picasso.with(context).load(getItem(position).getPath()).into(hode.chatImgMessage);
                hode.msg.setVisibility(View.GONE);
                hode.chatImgMessage.setVisibility(View.VISIBLE);
                hode.playIcon.setVisibility(View.GONE);
                break;
            }
            case 3: {
                hode.msg.setVisibility(View.GONE);
                hode.chatImgMessage.setVisibility(View.GONE);
                hode.playIcon.setVisibility(View.VISIBLE);
                String url = getItem(position).getPath();
                String[] urlsplit = url.split("/");
                final String filename = urlsplit[urlsplit.length - 1];
                final String saveUrl = Global.VIDEO_PATH + "/";
                final String saveUrl1 = saveUrl + filename;
                LogUtil.log("文件名称：" + filename);
                LogUtil.log("服务器路径：" + url);
                LogUtil.log("语音保存路径：" + saveUrl);
                final File file = new File(saveUrl1);
                //saveUrl1;
                if (!file.exists()) {
                    OkHttpClientManager.downloadAsyn(url, saveUrl,
                            new OkHttpClientManager.ResultCallback<String>() {

                                @Override
                                public void onError(Request request, Exception e) {
                                    LogUtil.log("下载语音失败");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onResponse(final String response) {
                                    LogUtil.log("下载语音:" + response);
                                    File file1 = new File(saveUrl + getItem(position).getMessage());
                                    if (!file1.exists()) {
                                        try {
                                            File[] files = ZipUtil.unzip(saveUrl + filename, Global.voicePassword);
                                            hode.playIcon.setOnClickListener(new PlayMedia(saveUrl + getItem(position).getMessage()));
                                        } catch (Exception e) {
                                            LogUtil.elog("解压缩失败");
                                        }
                                    }
                                }
                            });
                } else {
                    File file1 = new File(saveUrl + getItem(position).getMessage());
                    if (!file1.exists()) {
                        try {
                            File[] files = ZipUtil.unzip(saveUrl + filename, Global.voicePassword);
                        } catch (Exception e) {
                            LogUtil.elog("解压缩失败");
                        }
                    }
                    hode.playIcon.setOnClickListener(new PlayMedia(saveUrl + getItem(position).getMessage()));
                }
                break;
            }
        }

        if (isToMsg) {
            Picasso.with(context).load(ChatApplication.getHeap()).error(R.drawable.ic_taiji_normal).placeholder(R.drawable.ic_taiji_normal).into(hode.heap);
            hode.heap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MeActivity.startMeActivity(context);
                }
            });
        } else {
            final int pos = position;

            final Friend friend = FriendSQL.getFriendByYourId(getItem(pos).getFromuser());
            if (friend != null) {
                Picasso.with(context).load(friend.getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(hode.heap);
                hode.heap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendDetailActivity.startActivity(context, friend, null);
                    }
                });
            } else {
                final UserBrief userBrief = UserBriefSQL.getUserBriefById(getItem(pos).getFromuser());
                if (userBrief != null) {
                    Picasso.with(context).load(userBrief.getHeap()).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(hode.heap);
                    hode.heap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FriendDetailActivity.startActivity(context, friend, userBrief);
                        }
                    });
                } else {
                    Picasso.with(context).load(R.drawable.ic_taiji).error(R.drawable.ic_taiji).placeholder(R.drawable.ic_taiji).into(hode.heap);
                    hode.heap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SoftkeyboardUtil.hideSoftKeyboard(context, hode.msg);
                        }
                    });
                }
            }
        }

        if (getItem(position).isShowOrNotTime()) {
            hode.time.setText(DateUtils.dateToRead(getItem(position).getTime()));
            hode.time.setVisibility(View.VISIBLE);
        } else {
            hode.time.setVisibility(View.GONE);
        }

        final TeamMessage tempMessage = getItem(position);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            TeamMessage tMessage = tempMessage;

            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", tMessage.getMessage());
                cmb.setPrimaryClip(clip);
                ToastUtil.showToast(context, context.getResources().getString(R.string.hava_copy));
                return true;
            }
        });

        LogUtil.log("FromState: " + getItem(position).getFromstate());
        if (getItem(position).getFromstate() != null && getItem(position).getFromstate().equals("sending")) {
            if (hode.sendingIcon != null) {
                hode.sendingIcon.setVisibility(View.VISIBLE);
            }
            if (hode.redIcon != null) {
                hode.redIcon.setVisibility(View.GONE);
            }
        } else if (getItem(position).getFromstate() != null && getItem(position).getFromstate().equals("unread")) {
            if (hode.sendingIcon != null) {
                hode.sendingIcon.setVisibility(View.GONE);
            }
            if (hode.redIcon != null) {
                hode.redIcon.setVisibility(View.VISIBLE);
                v.setOnLongClickListener(new View.OnLongClickListener() {
                    TeamMessage tMessage = tempMessage;

                    @Override
                    public boolean onLongClick(View v) {
                        Dialog.setReSendDialog(context, tMessage);
                        return false;
                    }
                });
            }
        } else if (getItem(position).getFromstate() != null && getItem(position).getFromstate().equals("read")) {
            if (hode.sendingIcon != null) {
                hode.sendingIcon.setVisibility(View.GONE);
            }
            if (hode.redIcon != null) {
                hode.redIcon.setVisibility(View.GONE);
            }
        } else {
            LogUtil.log("发送消息未知状态");
        }

        return v;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setDate(List<TeamMessage> teamMessages) {
        this.teamMessages = teamMessages;
        notifyDataSetChanged();
    }

    public void setData(TeamMessage teamMessage) {
        teamMessages.add(teamMessage);
        notifyDataSetChanged();
    }

    private class ViewHold {
        ImageView heap;
        EmojiconTextView msg;
        TextView time;
        ImageView chatImgMessage;
        ImageView redIcon;
        ImageView playIcon;
        GifImageView sendingIcon;
    }
}
