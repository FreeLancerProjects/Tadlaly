package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.semicolon.tadlaly.Models.BankModel;
import com.semicolon.tadlaly.R;

import java.util.List;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.MyHolder> {

    private Context context;
    private List<BankModel> bankModelList;

    public BankAccountAdapter(Context context, List<BankModel> bankModelList) {
        this.context = context;
        this.bankModelList = bankModelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bank_account_row,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        BankModel bankModel = bankModelList.get(position);
        holder.BindData(bankModel);

    }

    @Override
    public int getItemCount() {
        return bankModelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_bank_name,tv_account_name,tv_account_number,tv_account_iban;
        public MyHolder(View itemView) {
            super(itemView);
            tv_bank_name = itemView.findViewById(R.id.tv_bank_name);
            tv_account_name = itemView.findViewById(R.id.tv_account_name);
            tv_account_number = itemView.findViewById(R.id.tv_account_number);
            tv_account_iban = itemView.findViewById(R.id.tv_account_iban);

        }

        public void BindData(BankModel bankModel)
        {
            tv_bank_name.setText(bankModel.getAccount_bank_name());
            tv_account_name.setText(bankModel.getAccount_name());
            tv_account_number.setText(bankModel.getAccount_number());
            tv_account_iban.setText(bankModel.getAccount_IBAN());
        }
    }
}
