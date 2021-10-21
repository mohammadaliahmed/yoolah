package com.appsinventiv.yoolah.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.ChattingScreen;
import com.appsinventiv.yoolah.Models.UserMessages;
import com.appsinventiv.yoolah.Models.UserModel;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.appsinventiv.yoolah.Utils.SharedPrefs;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {
    Context context;

    List<UserModel> itemList;
    boolean isAdmin;
    ParticipantsAdapterCallbacks callbacks;


    public ParticipantsAdapter(Context context, List<UserModel> itemList, boolean isAdmin, ParticipantsAdapterCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.isAdmin = isAdmin;
        this.callbacks = callbacks;
    }

    public void setItemList(List<UserModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_layout, viewGroup, false);
        ParticipantsAdapter.ViewHolder viewHolder = new ParticipantsAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final UserModel userModel = itemList.get(i);

        if (isAdmin) {
            if (userModel.getCanMessage() == 1) {

                viewHolder.canMessage.setChecked(true);
            } else {
                viewHolder.canMessage.setChecked(false);
            }
            if (userModel.getId().equals(SharedPrefs.getUserModel().getId())) {
                viewHolder.canMessage.setVisibility(View.GONE);
            } else {
                viewHolder.canMessage.setVisibility(View.VISIBLE);
            }


        } else {
            viewHolder.canMessage.setVisibility(View.GONE);
        }
        viewHolder.name.setText(userModel.getName());

        Glide.with(context).load(AppConfig.BASE_URL_Image + userModel.getThumbnailUrl()).placeholder(R.drawable.team).into(viewHolder.picture);


        if (isAdmin) {
            if (userModel.getId().equals(SharedPrefs.getUserModel().getId())) {
                viewHolder.admin.setVisibility(View.VISIBLE);


            } else {
                viewHolder.admin.setVisibility(View.GONE);

            }
        } else {
            viewHolder.admin.setVisibility(View.GONE);


        }

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (isAdmin) {
                    if (!userModel.getId().equals(SharedPrefs.getUserModel().getId())) {

                        callbacks.onDelete(userModel);
                    }
                }
                return false;
            }
        });


        viewHolder.canMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        if (!userModel.getId().equals(SharedPrefs.getUserModel().getId())) {
                            callbacks.onAllowToMessage(userModel, true);
                        }
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, admin;
        CircleImageView picture;
        Switch canMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            canMessage = itemView.findViewById(R.id.canMessage);
            admin = itemView.findViewById(R.id.admin);
            picture = itemView.findViewById(R.id.picture);
        }
    }


    public interface ParticipantsAdapterCallbacks {
        public void onDelete(UserModel model);

        public void onAllowToMessage(UserModel model, boolean canMessage);
    }
}
