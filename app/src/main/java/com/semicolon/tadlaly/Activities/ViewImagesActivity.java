package com.semicolon.tadlaly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.semicolon.tadlaly.Adapters.ViewImageAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.R;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.List;

public class ViewImagesActivity extends AppCompatActivity {
    private ImageView back,img_prev,img_next;
    private DiscreteScrollView discrete_recyclerView;
    private TextView tv_title;
    private ViewImageAdapter adapter;
    private List<MyAdsModel.Images> imagePathList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_images);
        initView();
        getDataFromIntent();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            imagePathList.clear();
            List<MyAdsModel.Images> imgs = (List<MyAdsModel.Images>) intent.getSerializableExtra("images");
            tv_title.setText("<1/"+imgs.size()+">");
            imagePathList.addAll(imgs);
            Log.e("hmgsize",imagePathList.size()+"");
            if (imgs.size()==1)
            {
                img_next.setVisibility(View.INVISIBLE);
                img_prev.setVisibility(View.INVISIBLE);
            }else if (imgs.size()>1)
            {
                img_next.setVisibility(View.VISIBLE);
                img_prev.setVisibility(View.INVISIBLE);
            }

            adapter.notifyDataSetChanged();

        }
    }

    private void initView() {
        imagePathList = new ArrayList<>();
        back = findViewById(R.id.back);
        img_prev = findViewById(R.id.imgPrev);
        img_next = findViewById(R.id.imgNext);
        discrete_recyclerView = findViewById(R.id.discrete_recyclerView);
        tv_title = findViewById(R.id.tv_title);
        adapter = new ViewImageAdapter(imagePathList,this);
        discrete_recyclerView.setAdapter(adapter);
        discrete_recyclerView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.05f)
                .setMinScale(0.85f)
                .setPivotX(Pivot.X.CENTER)
                .setPivotY(Pivot.Y.BOTTOM)
                .build());
        discrete_recyclerView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                int page = adapterPosition+1;
                tv_title.setText("<"+page+"/"+imagePathList.size()+">");
                if (adapterPosition==0)
                {
                    if (imagePathList.size()>1)
                    {
                        img_prev.setVisibility(View.INVISIBLE);
                        img_next.setVisibility(View.VISIBLE);
                    }else if (imagePathList.size()==1)
                    {
                        img_prev.setVisibility(View.INVISIBLE);
                        img_next.setVisibility(View.INVISIBLE);
                    }


                }else if (adapterPosition==imagePathList.size()-1)
                {
                    img_next.setVisibility(View.INVISIBLE);
                    img_prev.setVisibility(View.VISIBLE);

                }else
                    {
                        img_next.setVisibility(View.VISIBLE);
                        img_prev.setVisibility(View.VISIBLE);
                    }
            }
        });
        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discrete_recyclerView.smoothScrollToPosition(discrete_recyclerView.getCurrentItem()+1);
            }
        });
        img_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discrete_recyclerView.smoothScrollToPosition(discrete_recyclerView.getCurrentItem()-1);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
