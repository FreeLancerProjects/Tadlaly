package com.semicolon.tadlaly.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.semicolon.tadlaly.Fragments.Home_Fragment;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.R;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.myHolder> {
    private Context context;
    private List<DepartmentsModel.SubdepartObject> subdepartObjectList;
    private Home_Fragment home_fragment;
    public BranchAdapter(Context context,Home_Fragment home_fragment ,List<DepartmentsModel.SubdepartObject> subdepartObjectList) {
        this.context = context;
        this.subdepartObjectList = subdepartObjectList;
        this.home_fragment = home_fragment;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dept_row,parent,false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {
        DepartmentsModel.SubdepartObject subdepartObject = subdepartObjectList.get(position);
        holder.BindData(subdepartObject);
        setFadeAnimation(holder.itemView);
        holder.itemView.setOnClickListener(view -> home_fragment.setSubDeptPos(subdepartObject));
    }

    private void setFadeAnimation(View view) {
        ScaleAnimation animation = new ScaleAnimation(0f,1f,0f,1f);
        animation.setDuration(800);
        view.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return subdepartObjectList.size();
    }

    public class myHolder extends RecyclerView.ViewHolder {
       // private ImageView subDept_icon;
        private TextView subDept_name;
        public myHolder(View itemView) {
            super(itemView);
           // subDept_icon = itemView.findViewById(R.id.subDept_icon);
            subDept_name = itemView.findViewById(R.id.subDept_name);

        }
        public void BindData(DepartmentsModel.SubdepartObject subdepartObject)
        {
           // Typeface typeface = Typeface.createFromAsset(context.getAssets(),"OYA-Regular.ttf");

            //Picasso.with(context).load(Uri.parse(Tags.Image_Url+subdepartObject.getSub_department_image())).into(subDept_icon);
           // subDept_name.setTypeface(typeface);
            subDept_name.setText(subdepartObject.getSub_department_name());

        }
    }
}
