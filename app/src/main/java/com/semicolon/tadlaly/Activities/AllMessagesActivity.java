package com.semicolon.tadlaly.Activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.AllMsgAdapter;
import com.semicolon.tadlaly.Models.PeopleMsgModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AllMessagesActivity extends Activity implements UserSingleTone.OnCompleteListener{
    private ImageView back;
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager manager;
    private ProgressBar progressBar;
    private List<PeopleMsgModel> peopleMsgModelList;
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private TextView no_msg;
    private NotificationManager notificationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);
        initView();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        userSingleTone =UserSingleTone.getInstance();
        userSingleTone.getUser(this);
    }

    private void initView() {
        peopleMsgModelList = new ArrayList<>();
        back = findViewById(R.id.back);
        no_msg = findViewById(R.id.no_msg);
        progressBar = findViewById(R.id.progBar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        manager = new LinearLayoutManager(this);
        recView = findViewById(R.id.recView);
        adapter = new AllMsgAdapter(this,peopleMsgModelList);
        recView.setLayoutManager(manager);
        recView.setAdapter(adapter);
        back.setOnClickListener(view -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        ShortcutBadger.removeCount(getApplicationContext());
        getAllPeopleMsg();

    }

    private void getAllPeopleMsg() {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<PeopleMsgModel>> call = retrofit.create(Services.class).getAllPeople_chat(userModel.getUser_id());
        call.enqueue(new Callback<List<PeopleMsgModel>>() {
            @Override
            public void onResponse(Call<List<PeopleMsgModel>> call, Response<List<PeopleMsgModel>> response) {
                if (response.isSuccessful())
                {
                    peopleMsgModelList.clear();

                    if (response.body().size()>0)
                    {
                        progressBar.setVisibility(View.GONE);
                        no_msg.setVisibility(View.GONE);
                        peopleMsgModelList.addAll(response.body());
                        adapter.notifyDataSetChanged();
                    }else
                        {
                            progressBar.setVisibility(View.GONE);

                            no_msg.setVisibility(View.VISIBLE);

                        }
                }
            }

            @Override
            public void onFailure(Call<List<PeopleMsgModel>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

                Toast.makeText(AllMessagesActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                Log.e("Error",t.getMessage());
            }
        });
    }

    public void setPos(int pos)
    {
        PeopleMsgModel peopleMsgModel = peopleMsgModelList.get(pos);
        Intent intent = new Intent(this,SendMsgActivity.class);
        intent.putExtra("chat_user_id",peopleMsgModel.getFrom_id());
        intent.putExtra("chat_user_name",peopleMsgModel.getFrom_name());
        intent.putExtra("chat_user_image",peopleMsgModel.getFrom_image());
        intent.putExtra("chat_user_phone",peopleMsgModel.getFrom_phone());
        intent.putExtra("curr_user_name",userModel.getUser_full_name());
        intent.putExtra("curr_user_image",userModel.getUser_photo());
        startActivity(intent);
    }
    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel =userModel;
    }
}
