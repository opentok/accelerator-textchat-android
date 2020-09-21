package com.tokbox.android.accpack.textchat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    private AsyncListDiffer<ChatMessage> messagesList = new AsyncListDiffer<ChatMessage>(this,DIFF_CALLBACK);
    private View messageView;

    public MessagesAdapter(List<ChatMessage> messagesList) throws Exception{
        if (messagesList == null) {
            throw new Exception("MessageList cannot be null");
        }
        this.messagesList.submitList(messagesList);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new MessageViewHolder(view);

    }

    public void submitList(List<ChatMessage> list) {
        messagesList.submitList(list);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        ChatMessage message = messagesList.getCurrentList().get(position);
        SimpleDateFormat ft =
                new SimpleDateFormat("hh:mm a");
        if ( message.getSenderAlias() != null && !message.getSenderAlias().isEmpty()) {
            holder.msgInfo.setText(message.getSenderAlias() + ", " + ft.format(new Date(message.getTimestamp())).toString());
            holder.initial.setText(String.valueOf(Character.toUpperCase((message.getSenderAlias().charAt(0)))));
        }
        else {
            holder.msgInfo.setText(ft.format(new Date(message.getTimestamp())).toString());
        }
        holder.messageText.setText(message.getText());

    }

    @Override
    public int getItemCount() {
        return messagesList.getCurrentList().size();
    }

    @Override
    public int getItemViewType(int position) {

        if ( messagesList != null ) {
            ChatMessage item = messagesList.getCurrentList().get(position);

            if(item.getMessageStatus() == ChatMessage.MessageStatus.SENT_MESSAGE) {
                return R.layout.sent_row;
            }else {
                return R.layout.received_row;
            }
        }
        else {
            try {
                throw new Exception ("MessageList cannot be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView msgInfo, messageText, initial;

        public MessageViewHolder(View view) {
            super(view);
            this.msgInfo = (TextView) view.findViewById(R.id.msg_info);
            this.messageText = (TextView) view.findViewById(R.id.msg_text);
            this.initial = (TextView) view.findViewById(R.id.initial);
        }
    }
    public static final DiffUtil.ItemCallback<ChatMessage> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<ChatMessage>() {
        @Override
        public boolean areItemsTheSame(
                @NonNull ChatMessage oldMsg, @NonNull ChatMessage newMsg) {
            // Message properties may have changed if reloaded from the DB, but ID is fixed
            return oldMsg.getMessageId() == newMsg.getMessageId();
        }
        @Override
        public boolean areContentsTheSame(
                @NonNull ChatMessage oldMsg, @NonNull ChatMessage newMsg) {
            // NOTE: if you use equals, your object must properly override Object#equals()
            // Incorrectly returning false here will result in too many animations.
            return oldMsg.equals(newMsg);
        }
    };
}