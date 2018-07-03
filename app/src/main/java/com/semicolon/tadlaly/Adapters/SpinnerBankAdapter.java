package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.semicolon.tadlaly.Models.BankModel;
import com.semicolon.tadlaly.R;

import java.util.List;

public class SpinnerBankAdapter extends ArrayAdapter {
    private Context context;
    private List<BankModel> bankModelList;
    public SpinnerBankAdapter(@NonNull Context context, int resource, @NonNull List<BankModel> bankModelList) {
        super(context, resource, bankModelList);
        this.context = context;
        this.bankModelList = bankModelList;
    }

    @Override
    public int getCount() {
        return bankModelList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BankModel bankModel = bankModelList.get(position);
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item,parent,false);
        TextView title = view.findViewById(R.id.title);
        title.setText(bankModel.getAccount_bank_name());
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);

    }
}
