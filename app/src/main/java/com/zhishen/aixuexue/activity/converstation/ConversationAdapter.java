package com.zhishen.aixuexue.activity.converstation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htmessage.sdk.ChatType;
import com.htmessage.sdk.client.HTClient;
import com.htmessage.sdk.model.HTConversation;
import com.htmessage.sdk.model.HTGroup;
import com.htmessage.sdk.model.HTMessage;
import com.htmessage.sdk.model.HTMessageTextBody;
import com.zhishen.aixuexue.R;
import com.zhishen.aixuexue.activity.emojicon.SmileUtils;
import com.zhishen.aixuexue.bean.User;
import com.zhishen.aixuexue.http.Constant;
import com.zhishen.aixuexue.manager.ContactsManager;
import com.zhishen.aixuexue.manager.HTAtMessageHelper;
import com.zhishen.aixuexue.util.CommonUtils;
import com.zhishen.aixuexue.util.DateUtils;
import com.zhishen.aixuexue.util.GroupNoticeMessageUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private List<HTConversation> htConversations = new ArrayList<>();
    private Context context;
    private OnConversationClickListener clickListener;

    public ConversationAdapter(Context context, List<HTConversation> htConversations) {
        this.context = context;
        this.htConversations = htConversations;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_conversation_single, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final HTConversation htConversation = htConversations.get(position);
        ChatType chatType = htConversation.getChatType();
        HTMessage htMessage = null;
        List<HTMessage> messages = htConversation.getAllMessages();
        if (messages.size() > 0) {
            htMessage = htConversation.getLastMessage();
        }
        final String userId = htConversation.getUserId();
        Log.d("1212", messages.size() + "====   " + userId);
        String avatar = "";
        String nick = userId;
        if (htMessage != null) {
            if (htMessage.getDirect() == HTMessage.Direct.SEND) {
                if (!TextUtils.isEmpty(htMessage.getStringAttribute("otherNick"))) {
                    nick = htMessage.getStringAttribute("otherNick");
                }
                if (!TextUtils.isEmpty(htMessage.getStringAttribute("otherAvatar"))) {
                    avatar = htMessage.getStringAttribute("otherAvatar");
                }
            } else if (htMessage.getDirect() == HTMessage.Direct.RECEIVE) {
                if (!TextUtils.isEmpty(htMessage.getStringAttribute("nick"))) {
                    nick = htMessage.getStringAttribute("nick");
                }
                if (!TextUtils.isEmpty(htMessage.getStringAttribute("avatar"))) {
                    avatar = htMessage.getStringAttribute("avatar");
                }
            }
        }
        if (chatType == ChatType.groupChat) {
            HTGroup htGroup = HTClient.getInstance().groupManager().getGroup(userId);
            holder.tv_name.setMaxLines(1);
            holder.tv_name.setMaxEms(10);
            holder.tv_name.setEllipsize(TextUtils.TruncateAt.END);
            if (HTAtMessageHelper.get().hasAtMeMsg(userId)) {
                holder.mentioned.setVisibility(View.VISIBLE);
            } else {
                holder.mentioned.setVisibility(View.GONE);
            }
            if (htGroup != null) {
                holder.tv_name.setText(htGroup.getGroupName());
                String imgUrl = htGroup.getImgUrl();
                if (!TextUtils.isEmpty(imgUrl)) {
                    if (!imgUrl.startsWith("http") || !imgUrl.contains(Constant.baseImgUrl)) {
                        imgUrl = Constant.baseImgUrl + imgUrl;
                    }
                }
                CommonUtils.loadGroupAvatar(context, TextUtils.isEmpty(imgUrl) ? R.drawable.user_icon_chat_default : imgUrl, holder.ivAvatar);
            } else {
                //如果当前群还未获取到,
                if (htMessage != null) {
                    String groupName = htMessage.getStringAttribute("groupName");
                    String groupAvatar = htMessage.getStringAttribute("groupAvatar");
                    if (!TextUtils.isEmpty(groupName)) {
                        holder.tv_name.setText(groupName);
                    } else {
                    }
                    if (!TextUtils.isEmpty(groupAvatar)) {
                        if (!groupAvatar.startsWith("http") || !groupAvatar.contains(Constant.baseImgUrl)) {
                            groupAvatar = Constant.baseImgUrl + groupAvatar;
                        }
                    }
                    CommonUtils.loadGroupAvatar(context, TextUtils.isEmpty(groupAvatar) ? R.drawable.user_icon_chat_default : groupAvatar, holder.ivAvatar);
                }
            }
            holder.tv_group_tag.setVisibility(View.VISIBLE);
        } else {
            holder.mentioned.setVisibility(View.GONE);
            holder.tv_group_tag.setVisibility(View.INVISIBLE);
            User user = ContactsManager.getInstance().getContactList().get(userId);
            if (user != null) {
                nick = user.getNick();
                avatar = user.getAvatar();
                holder.tv_name.setText(TextUtils.isEmpty(nick) ? userId : nick);
                CommonUtils.loadUserAvatar(context, TextUtils.isEmpty(avatar) ? R.drawable.default_avatar : avatar, holder.ivAvatar);
            } else {
                holder.tv_name.setText(TextUtils.isEmpty(nick) ? userId : nick);
                CommonUtils.loadUserAvatar(context, TextUtils.isEmpty(avatar) ? R.drawable.default_avatar : avatar, holder.ivAvatar);
            }
        }
        if (htConversation.getUnReadCount() > 0) {
            // show unread message count
            holder.tv_unread.setText(String.valueOf(htConversation.getUnReadCount()));
            holder.tv_unread.setVisibility(View.VISIBLE);
        } else {
            holder.tv_unread.setVisibility(View.INVISIBLE);
        }

        if (htMessage != null) {
            holder.tv_content.setText(SmileUtils.getSmiledText(context, getContent(htMessage)),
                    TextView.BufferType.SPANNABLE);
            holder.tv_time.setText(DateUtils.getTimestampString(new Date(htMessage.getTime())));
        } else {
            holder.tv_content.setText("");
            holder.tv_time.setText(DateUtils.getTimestampString(new Date(htConversation.getTime())));
        }
        if (htConversation.getTopTimestamp() != 0) {
            holder.re_main.setBackgroundResource(R.drawable.list_item_bg_gray);
        } else {
            holder.re_main.setBackgroundResource(R.drawable.list_item_bg_white);
        }
        if (position == htConversations.size() - 1) {
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
        }
        final String finalNick = nick;
        final String finalAvatar = avatar;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onConversationClick(position, userId, finalNick, finalAvatar, htConversation);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (clickListener != null) {
                    clickListener.onConversationLongClick(position, htConversation);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return htConversations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 和谁的聊天记录
         */
        TextView tv_name;
        /**
         * 消息未读数
         */
        TextView tv_unread;
        /**
         * 最后一条消息的内容
         */
        TextView tv_content;
        /**
         * 最后一条消息的时间
         */
        TextView tv_time;

        ImageView ivAvatar;

        //群组的标识
        TextView tv_group_tag;
        RelativeLayout re_main;
        //有人at我
        TextView mentioned;
        View view;

        public ViewHolder(View convertView) {
            super(convertView);
            tv_name = (TextView) convertView.findViewById(R.id.name);
            tv_content = (TextView) convertView.findViewById(R.id.message);
            tv_time = (TextView) convertView.findViewById(R.id.time);
            tv_unread = (TextView) convertView.findViewById(R.id.unread_msg_number);
            ivAvatar = convertView.findViewById(R.id.avatar);
            tv_group_tag = (TextView) convertView.findViewById(R.id.tv_group_tag);
            re_main = (RelativeLayout) convertView.findViewById(R.id.re_main);
            mentioned = (TextView) convertView.findViewById(R.id.mentioned);
            view = convertView.findViewById(R.id.view);
        }
    }

    protected final static String[] msgs = {"发来一条消息", "[图片消息]", "[语音消息]", "[位置消息]", "[视频消息]", "[文件消息]",
            "%1个联系人发来%2条消息"
    };

    private String getContent(HTMessage message) {
        int action = message.getIntAttribute("action", 0);
        if (action > 1999 && action < 2005 || action == 3000) {
            return GroupNoticeMessageUtils.getGroupNoticeContent(message, context);
        }
        String notifyText = "";
        if (message.getType() == null) {
            return "";
        }
        switch (message.getType()) {
            case TEXT:

                HTMessageTextBody textBody = (HTMessageTextBody) message.getBody();

                String content = textBody.getContent();
                if (content != null) {
                    notifyText += content;

                } else {
                    notifyText += msgs[0];
                }

                break;
            case IMAGE:
                notifyText += msgs[1];
                break;
            case VOICE:

                notifyText += msgs[2];
                break;
            case LOCATION:
                notifyText += msgs[3];
                break;
            case VIDEO:
                notifyText += msgs[4];
                break;
            case FILE:
                notifyText += msgs[5];
                break;
        }
        return notifyText;
    }

    public void setOnConversationClickListener(OnConversationClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnConversationClickListener {
        void onConversationClick(int position, String userId, String userName, String avatar, HTConversation conversation);

        void onConversationLongClick(int position, HTConversation conversation);

    }
}
