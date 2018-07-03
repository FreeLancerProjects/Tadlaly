package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semicolon.tadlaly.Activities.SendMsgActivity;
import com.semicolon.tadlaly.Models.PeopleMsgModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter <MessageAdapter.myHolder>{
    private Context context;
    private List<PeopleMsgModel> messageModelList;
    private SendMsgActivity activity;

    public MessageAdapter(Context context, List<PeopleMsgModel> messageModelList) {
        this.context = context;
        this.messageModelList = messageModelList;
        this.activity = (SendMsgActivity) context;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.msg_container,parent,false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        PeopleMsgModel messageModel = messageModelList.get(position);
        holder.BindData(messageModel);
        holder.itemView.setOnLongClickListener(view -> {
            activity.setMessageData(messageModelList.get(holder.getAdapterPosition()));
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        private CircleImageView chat_user_image;
        private TextView message,date;
        public myHolder(View itemView) {
            super(itemView);
            chat_user_image = itemView.findViewById(R.id.image);
            message = itemView.findViewById(R.id.msg);
            date = itemView.findViewById(R.id.date);
        }

        public void BindData(PeopleMsgModel messageModel)
        {
            Picasso.with(context).load(Tags.Image_Url+messageModel.getFrom_image()).into(chat_user_image);
            message.setText(messageModel.getContent_message());
            date.setText(messageModel.getTime_message());
            try {

            }catch (NullPointerException e)
            {

            }catch (Exception e){}

        }
    }
}
