package com.appsinventiv.yoolah.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.ChattingScreen;
import com.appsinventiv.yoolah.Activites.FillPoll;
import com.appsinventiv.yoolah.Models.PollModel;
import com.appsinventiv.yoolah.Models.UserMessages;
import com.appsinventiv.yoolah.R;
import com.appsinventiv.yoolah.Utils.AppConfig;
import com.appsinventiv.yoolah.Utils.CommonUtils;
import com.appsinventiv.yoolah.Utils.Constants;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PollToFillAdapter extends RecyclerView.Adapter<PollToFillAdapter.ViewHolder> {
    Context context;

    List<PollModel> itemList;

    public PollToFillAdapter(Context context, List<PollModel> itemList) {
        this.context = context;
        this.itemList = itemList;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.poll_to_item_layout, viewGroup, false);
        PollToFillAdapter.ViewHolder viewHolder = new PollToFillAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        PollModel model = itemList.get(i);
        holder.title.setText("Title: " + model.getTitle());
        holder.question.setText("Question: " + model.getQuestion());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, FillPoll.class);
                i.putExtra("pollId", model.getId());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<PollModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, question;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            question = itemView.findViewById(R.id.question);

        }
    }


}
