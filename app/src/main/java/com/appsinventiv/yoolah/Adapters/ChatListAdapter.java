package com.appsinventiv.yoolah.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.appsinventiv.yoolah.Activites.ChattingScreen;
import com.appsinventiv.yoolah.Models.UserMessages;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    Context context;

    List<UserMessages> itemList;
    List<UserMessages> arrayList;

    public ChatListAdapter(Context context, List<UserMessages> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.arrayList = new ArrayList<>(itemList);

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, viewGroup, false);
        ChatListAdapter.ViewHolder viewHolder = new ChatListAdapter.ViewHolder(view);
        return viewHolder;
    }

    public void updateList(List<UserMessages> itemList) {
        this.itemList = itemList;
        arrayList.clear();
        arrayList.addAll(itemList);
        notifyDataSetChanged();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (UserMessages item : arrayList) {
//                if (item.get().toLowerCase().contains(charText.toLowerCase())) {
//
//                    itemList.add(item);
//                }

            }


        }

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final UserMessages userModel = itemList.get(i);
        viewHolder.name.setText(userModel.getTitle());
        viewHolder.time.setText(CommonUtils.getFormattedDate(userModel.getTime()));
        if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_TEXT)) {
            viewHolder.message.setText(userModel.getMessageByName() + ": " + userModel.getMessageText());
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_REPLY)) {
            viewHolder.message.setText(userModel.getMessageByName() + ": " + userModel.getMessageText());
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_BUBBLE)) {
            viewHolder.message.setText(userModel.getMessageText());
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_IMAGE)) {
            viewHolder.message.setText(userModel.getMessageByName() + ": " + "\uD83D\uDCF7  Image");
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_DOCUMENT)) {
            viewHolder.message.setText(userModel.getMessageByName() + ": " + "\uD83D\uDCC4  Document");
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_AUDIO)) {
            viewHolder.message.setText(userModel.getMessageByName() + ": " + "\uD83C\uDFB5 Audio");
        } else if (userModel.getMessageType().equals(Constants.MESSAGE_TYPE_DELETED)) {
            viewHolder.message.setText(userModel.getMessageByName() + ": " + "deleted message");
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ChattingScreen.class);
                i.putExtra("roomId", userModel.getRoomId());
                i.putExtra("name", userModel.getTitle());
                i.putExtra("picture", userModel.getCoverUrl());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i);
            }
        });
        Glide.with(context).load(AppConfig.BASE_URL_Image + userModel.getCoverUrl()).placeholder(R.drawable.team).into(viewHolder.picture);

        HashMap<Integer, Boolean> map = SharedPrefs.getLastSeenMessage(userModel.getId());
        if (map != null && map.size() > 0) {
            Boolean abc = map.get(userModel.getId());
            if (abc != null) {
                if (abc) {
                    viewHolder.name.setTypeface(Typeface.DEFAULT);
                    viewHolder.message.setTypeface(Typeface.DEFAULT);
                    viewHolder.undread.setVisibility(View.GONE);

                } else {
                    viewHolder.name.setTypeface(Typeface.DEFAULT_BOLD);
                    viewHolder.message.setTypeface(Typeface.DEFAULT_BOLD);
                    viewHolder.undread.setVisibility(View.VISIBLE);

                }
            } else {
                viewHolder.name.setTypeface(Typeface.DEFAULT_BOLD);
                viewHolder.message.setTypeface(Typeface.DEFAULT_BOLD);
                viewHolder.undread.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.name.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.message.setTypeface(Typeface.DEFAULT_BOLD);
            viewHolder.undread.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, message, time;
        CircleImageView picture;
        View undread;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            undread = itemView.findViewById(R.id.undread);
            picture = itemView.findViewById(R.id.picture);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }


}
