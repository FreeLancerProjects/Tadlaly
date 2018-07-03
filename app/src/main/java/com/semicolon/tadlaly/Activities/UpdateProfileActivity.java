package com.semicolon.tadlaly.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.semicolon.tadlaly.Fragments.UpdatePassword_Fragment;
import com.semicolon.tadlaly.Fragments.UpdateProfileItems_Fragment;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Tags;

import me.anwarshahriar.calligrapher.Calligrapher;

public class UpdateProfileActivity extends AppCompatActivity {
    private String type;
    private ImageView back;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);
        initView();
        getDataFromIntent();

    }

    private void initView() {
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        back.setOnClickListener(view ->
        finish()
        );
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            type = intent.getStringExtra("type");
            if (type!=null && !TextUtils.isEmpty(type))
            {
                if (type.equals(Tags.update_pass))
                {
                    title.setText(getString(R.string.update_pass));
                    getSupportFragmentManager().beginTransaction().replace(R.id.update_fragments_containers,new UpdatePassword_Fragment()).commit();
                }else if (type.equals(Tags.update_name))
                {
                    title.setText(R.string.upd_name);
                    Bundle bundle = new Bundle();
                    bundle.putString("type",Tags.update_name);
                    UpdateProfileItems_Fragment udi =new UpdateProfileItems_Fragment();
                    udi.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.update_fragments_containers,udi).commit();


                }
                else if (type.equals(Tags.update_email))
                {
                    title.setText(R.string.upd_email);
                    Bundle bundle = new Bundle();
                    bundle.putString("type",Tags.update_email);
                    UpdateProfileItems_Fragment udi =new UpdateProfileItems_Fragment();
                    udi.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.update_fragments_containers,udi).commit();


                }
                else if (type.equals(Tags.update_phone))
                {
                    title.setText(R.string.upd_phone);
                    Bundle bundle = new Bundle();
                    bundle.putString("type",Tags.update_phone);
                    UpdateProfileItems_Fragment udi =new UpdateProfileItems_Fragment();
                    udi.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.update_fragments_containers,udi).commit();


                }
                else if (type.equals(Tags.update_country))
                {
                    title.setText(R.string.upd_city);
                    Bundle bundle = new Bundle();
                    bundle.putString("type",Tags.update_country);
                    UpdateProfileItems_Fragment udi =new UpdateProfileItems_Fragment();
                    udi.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.update_fragments_containers,udi).commit();


                }
                else if (type.equals(Tags.update_username))
                {
                    title.setText(R.string.upd_username);
                    Bundle bundle = new Bundle();
                    bundle.putString("type",Tags.update_username);
                    UpdateProfileItems_Fragment udi =new UpdateProfileItems_Fragment();
                    udi.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.update_fragments_containers,udi).commit();


                }
            }
        }
    }
}
