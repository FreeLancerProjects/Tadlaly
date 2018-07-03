package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Adapters.MessageAdapter;
import com.semicolon.tadlaly.Models.PeopleMsgModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;
import pl.tajchert.waitingdots.DotsTextView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SendMsgActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener{
    private ImageView back, sendBtn;
    private TextView curr_user_name, chat_user_name,no_conv;
    private CircleImageView curr_user_image, chat_user_image;
    private RelativeLayout call_btn;
    private EditText message;
    private LinearLayout deleteBtn;
    private RecyclerView recView;
    private RecyclerView.Adapter adapter;
    private LinearLayoutManager manager;
    private List<PeopleMsgModel> messageModelList;
    private ProgressDialog dialog,delDialog;
    //private ProgressBar progressBar;
    private DotsTextView dotsTextView;
    private UserModel userModel ;
    private UserSingleTone userSingleTone;
    private LinearLayout loading_container;
    private String chat_id="",chat_name="",chat_image="",chat_phone="",curr_name="",curr_image="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_msg);
        userSingleTone =UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        initView();
        getDataFromIntent();
        CreateProgressDialog();
        CreateDeleteProgressDialog();
        getMessages();
    }

    private void getDataFromIntent() {
        Intent intent =getIntent();
        if (intent!=null)
        {
            chat_id = intent.getStringExtra("chat_user_id");
            chat_name = intent.getStringExtra("chat_user_name");
            chat_image = intent.getStringExtra("chat_user_image");
            chat_phone = intent.getStringExtra("chat_user_phone");
            curr_name = intent.getStringExtra("curr_user_name");
            curr_image = intent.getStringExtra("curr_user_image");

            UpdateUi(curr_name,curr_image,chat_name,chat_image);
        }
    }

    private void UpdateUi(String curr_name, String curr_image, String name, String image) {
        curr_user_name.setText(curr_name);
        chat_user_name.setText(name);
        Picasso.with(this).load(Uri.parse(Tags.Image_Url+curr_image)).into(curr_user_image);
        Picasso.with(this).load(Uri.parse(Tags.Image_Url+image)).into(chat_user_image);
        ShortcutBadger.removeCount(getApplicationContext());
    }


    private void initView()
    {
        messageModelList = new ArrayList<>();
        back = findViewById(R.id.back);
        sendBtn = findViewById(R.id.send_btn);
        loading_container = findViewById(R.id.loading_container);
        curr_user_name = findViewById(R.id.curr_user_name);
        chat_user_name = findViewById(R.id.chat_user_name);
        curr_user_image = findViewById(R.id.curr_user_image);
        chat_user_image = findViewById(R.id.chat_user_image);
        message = findViewById(R.id.msg_txt);
        deleteBtn = findViewById(R.id.deleteBtn);
        call_btn = findViewById(R.id.call_btn);
        no_conv = findViewById(R.id.no_conv);
        dotsTextView = findViewById(R.id.dots);
        dotsTextView.showAndPlay();
      /*  progressBar = findViewById(R.id.progBar);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
    */    recView = findViewById(R.id.recView);


        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        manager.setStackFromEnd(true);
        recView.setLayoutManager(manager);
        adapter = new MessageAdapter(this,messageModelList);
        recView.setAdapter(adapter);
        back.setOnClickListener(view -> finish());
        sendBtn.setOnClickListener(view -> SendMessage());
        call_btn.setOnClickListener(view -> Call());
        deleteBtn.setOnClickListener(view -> Delete());

    }
    private void CreateProgressDialog()
    {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.sending_msg));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
    }
    private void CreateDeleteProgressDialog()
    {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        delDialog = new ProgressDialog(this);
        delDialog.setMessage(getString(R.string.deleting_msg));
        delDialog.setCanceledOnTouchOutside(false);
        delDialog.setCancelable(true);
        delDialog.setIndeterminateDrawable(drawable);
    }
    private void getMessages()
    {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<PeopleMsgModel>> call = retrofit.create(Services.class).getAllMsg(userModel.getUser_id(), chat_id);
        call.enqueue(new Callback<List<PeopleMsgModel>>() {
            @Override
            public void onResponse(Call<List<PeopleMsgModel>> call, Response<List<PeopleMsgModel>> response) {
                if (response.isSuccessful())
                {
                    messageModelList.clear();
                    if (response.body().size()>0)
                    {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                deleteBtn.setVisibility(View.VISIBLE);
                                no_conv.setVisibility(View.GONE);
                                dotsTextView.hideAndStop();
                                loading_container.setVisibility(View.GONE);
                                dotsTextView.setVisibility(View.GONE);
                                messageModelList.addAll(response.body());
                                adapter.notifyDataSetChanged();
                                recView.scrollToPosition(messageModelList.size()-1);
                            }
                        },1500);

                    }else
                        {
                            deleteBtn.setVisibility(View.GONE);
                            no_conv.setVisibility(View.VISIBLE);
                            dotsTextView.hideAndStop();
                            loading_container.setVisibility(View.GONE);
                            dotsTextView.setVisibility(View.GONE);

                        }
                }
            }

            @Override
            public void onFailure(Call<List<PeopleMsgModel>> call, Throwable t) {
                dotsTextView.hideAndStop();
                Log.e("Error",t.getMessage());
                Toast.makeText(SendMsgActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Delete()
    {
        delDialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<ResponseModel> call = retrofit.create(Services.class).DeleteAllMsg(userModel.getUser_id(), chat_id);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        delDialog.dismiss();
                        messageModelList.clear();
                        deleteBtn.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(SendMsgActivity.this, R.string.del_success, Toast.LENGTH_SHORT).show();
                    }else if (response.body().getSuccess()==0)
                    {
                        delDialog.dismiss();

                        Toast.makeText(SendMsgActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                delDialog.dismiss();
                Toast.makeText(SendMsgActivity.this,R.string.something, Toast.LENGTH_LONG).show();
                Log.e("Error",t.getMessage());
            }
        });
    }
    private void Call()
    {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + chat_phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }
    private void SendMessage()
    {
        String msg = message.getText().toString();
        if (!TextUtils.isEmpty(msg))
        {
            dialog.show();

            Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<ResponseModel> call = retrofit.create(Services.class).sendMsg(userModel.getUser_id(), chat_id, msg);
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    if (response.isSuccessful())
                    {
                        if (response.body().getSuccess()==1)
                        {

                            dialog.dismiss();
                            message.setText(null);
                            Toast.makeText(SendMsgActivity.this, R.string.msg_sent, Toast.LENGTH_SHORT).show();
                        }else
                            {
                                dialog.dismiss();
                            }
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(SendMsgActivity.this,R.string.something, Toast.LENGTH_LONG).show();
                    Log.e("Error",t.getMessage());
                }
            });
        }
    }

    public void setMessageData(PeopleMsgModel messageData)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.del_this_msg);
        alertDialog.setCancelable(true);
        AlertDialog alertDialog1 = alertDialog.create();

        alertDialog.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
            DeleteSingleMsg(messageData);
            alertDialog1.dismiss();
        });
        alertDialog.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> alertDialog1.dismiss());
        alertDialog1.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void DeleteSingleMsg(PeopleMsgModel messageData) {
        delDialog.show();
        List<String> msg_ids=new ArrayList<>();
        msg_ids.add(messageData.getId_message());
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<ResponseModel> call = retrofit.create(Services.class).deleteSingleMsg(msg_ids);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        delDialog.dismiss();
                        messageModelList.remove(messageData);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(SendMsgActivity.this,R.string.del_success, Toast.LENGTH_SHORT).show();

                        if (messageModelList.size()>0)
                        {
                            deleteBtn.setVisibility(View.VISIBLE);
                        }else
                            {
                                deleteBtn.setVisibility(View.GONE);

                            }

                    }
                    else if (response.body().getSuccess()==0)
                    {
                        delDialog.dismiss();
                        Toast.makeText(SendMsgActivity.this,R.string.error, Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                delDialog.dismiss();
                Toast.makeText(SendMsgActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                Log.e("Error",t.getMessage());

            }
        });
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }
}
