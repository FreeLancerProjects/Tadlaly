package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.semicolon.tadlaly.Fragments.Home_Fragment;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Department_Adapter extends RecyclerView.Adapter<Department_Adapter.myHolder> {

    private List<DepartmentsModel> departmentsModelList;
    private Context context;
    Home_Fragment home_fragment;
    public Department_Adapter(List<DepartmentsModel> departmentsModelList, Context context, Home_Fragment home_fragment) {
        this.departmentsModelList = departmentsModelList;
        this.context = context;
        this.home_fragment = home_fragment;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_home_adapter_item,parent,false);

        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        DepartmentsModel departmentsModel = departmentsModelList.get(position);
        holder.BindData(departmentsModel);
        holder.itemView.setOnClickListener(view -> home_fragment.setPos(holder.getAdapterPosition()));
    }


    @Override
    public int getItemCount() {
        return departmentsModelList.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private TextView title;
        public myHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            title = itemView.findViewById(R.id.title);
            //Typeface typeface = Typeface.createFromAsset(context.getAssets(),"OYA-Regular.ttf");

            //title.setTypeface(typeface);


        }

        public void BindData(DepartmentsModel departmentsModel)
        {
            Picasso.with(context).load(Uri.parse(Tags.Image_Url+departmentsModel.getMain_department_image())).into(img);
            title.setText(departmentsModel.getMain_department_name());
        }
    }
}
