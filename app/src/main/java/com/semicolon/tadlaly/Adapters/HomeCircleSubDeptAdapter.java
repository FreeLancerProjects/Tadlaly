package com.semicolon.tadlaly.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class HomeCircleSubDeptAdapter extends RecyclerView.Adapter<HomeCircleSubDeptAdapter.myHolder> {

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class myHolder extends RecyclerView.ViewHolder {
        public myHolder(View itemView) {
            super(itemView);
        }
    }
}
