package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Activities.TransferActivity;
import com.semicolon.tadlaly.Models.BankModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BankAdapter extends RecyclerView.Adapter<BankAdapter.MyHolder> {

    private Context context;
    private List<BankModel> bankModelList;
    private TransferActivity activity;

    public BankAdapter(Context context, List<BankModel> bankModelList) {
        this.context = context;
        this.activity = (TransferActivity) context;
        this.bankModelList = bankModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bank_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        BankModel bankModel = bankModelList.get(position);
        holder.BindData(bankModel);
        holder.itemView
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BankModel bankModel = bankModelList.get(holder.getAdapterPosition());
                        activity.setBankItem(bankModel);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return bankModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private RoundedImageView image;
        private TextView tv_bank_name,tv_account_name,tv_account_number,tv_account_iban;
        public MyHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_account_name = itemView.findViewById(R.id.tv_account_name);
            tv_account_number = itemView.findViewById(R.id.tv_account_number);
            tv_account_iban = itemView.findViewById(R.id.tv_account_iban);

        }

        public void BindData(BankModel bankModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.Image_Url+bankModel.getAccount_image())).into(image);
            tv_bank_name.setText(bankModel.getAccount_bank_name());
            tv_account_name.setText(bankModel.getAccount_name());
            tv_account_number.setText(bankModel.getAccount_number());
            tv_account_iban.setText(bankModel.getAccount_IBAN());
        }
    }
}
