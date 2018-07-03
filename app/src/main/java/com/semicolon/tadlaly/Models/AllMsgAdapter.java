package com.semicolon.tadlaly.Models;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semicolon.tadlaly.Activities.AllMessagesActivity;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllMsgAdapter extends RecyclerView.Adapter<AllMsgAdapter.myHolder> {
    private Context context;
    private List<PeopleMsgModel> peopleMsgModelList;
    private AllMessagesActivity activity;

    public AllMsgAdapter(Context context, List<PeopleMsgModel> peopleMsgModelList) {
        this.context = context;
        this.peopleMsgModelList = peopleMsgModelList;
        this.activity = (AllMessagesActivity) context;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_usermsg_row,parent,false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        PeopleMsgModel peopleMsgModel = peopleMsgModelList.get(position);
        holder.BindData(peopleMsgModel);
        holder.itemView.setOnClickListener(view -> activity.setPos(holder.getAdapterPosition()));

    }

    @Override
    public int getItemCount() {
        return peopleMsgModelList.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        private CircleImageView image;
        private TextView name,msg,date,time;
        public myHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            msg = itemView.findViewById(R.id.last_msg);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);

        }
        public void BindData(PeopleMsgModel peopleMsgModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.Image_Url+peopleMsgModel.getFrom_image())).into(image);
            name.setText(peopleMsgModel.getFrom_name());
            msg.setText(peopleMsgModel.getContent_message());
            String[] split = peopleMsgModel.getTime_message().split(" ");
            date.setText(split[0]);
            time.setText(split[1]+" "+split[2]);
            Log.e("time",split[1]+" "+split[2]);
            try {

            }catch (NullPointerException e)
            {

            }catch (ArrayIndexOutOfBoundsException e){}

        }
    }
}
