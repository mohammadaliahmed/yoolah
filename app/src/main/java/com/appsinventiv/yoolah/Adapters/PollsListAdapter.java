package com.appsinventiv.yoolah.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.yoolah.Activites.ChattingScreen;
import com.appsinventiv.yoolah.Models.PollAnswer;
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

public class PollsListAdapter extends RecyclerView.Adapter<PollsListAdapter.ViewHolder> {
    Context context;

    List<PollModel> itemList;
    PollsAdapterCallback callback;

    public PollsListAdapter(Context context, List<PollModel> itemList,PollsAdapterCallback callback) {
        this.context = context;
        this.itemList = itemList;
        this.callback = callback;

    }

    public void setItemList(List<PollModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.poll_item_layout, viewGroup, false);
        PollsListAdapter.ViewHolder viewHolder = new PollsListAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        PollModel model = itemList.get(i);

        int optionOneCount = 0;
        int optionTwoCount = 0;
        int optionThreeCount = 0;
        int optionFourCount = 0;

        for (PollAnswer answer : model.getAnswers()) {
            if (answer.getOption() == 1) {
                optionOneCount++;
            } else if (answer.getOption() == 2) {
                optionTwoCount++;
            } else if (answer.getOption() == 3) {
                optionThreeCount++;
            } else if (answer.getOption() == 4) {
                optionFourCount++;
            }
        }


        holder.title.setText(model.getTitle());
        holder.voteCount.setText("Total votes " + model.getAnswers().size());
        holder.question.setText(model.getQuestion());
        holder.option1.setText(model.getOption1());
        holder.option2.setText(model.getOption2());

        holder.vote1.setText( optionOneCount+" votes");
        holder.vote2.setText( optionTwoCount+" votes");


        if (model.getOption3() != null) {
            holder.option3.setVisibility(View.VISIBLE);
            holder.option3.setText(model.getOption3());
            holder.vote3.setVisibility(View.VISIBLE);
            holder.vote3.setText( optionThreeCount+" votes");

        }
        if (model.getOption4() != null) {
            holder.option4.setVisibility(View.VISIBLE);
            holder.option4.setText(model.getOption4());
            holder.vote4.setVisibility(View.VISIBLE);
            holder.vote4.setText( optionFourCount+" votes");

        }

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onDeleteClicked(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView option1, option2, option3, option4, vote1, vote2, vote3, vote4, title, question, voteCount;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            option1 = itemView.findViewById(R.id.option1);
            option2 = itemView.findViewById(R.id.option2);
            option3 = itemView.findViewById(R.id.option3);
            option4 = itemView.findViewById(R.id.option4);

            vote1 = itemView.findViewById(R.id.vote1);
            vote2 = itemView.findViewById(R.id.vote2);
            vote3 = itemView.findViewById(R.id.vote3);
            vote4 = itemView.findViewById(R.id.vote4);

            title = itemView.findViewById(R.id.title);
            question = itemView.findViewById(R.id.question);
            voteCount = itemView.findViewById(R.id.voteCount);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public interface PollsAdapterCallback{
        public void onDeleteClicked(PollModel model);
    }

}
