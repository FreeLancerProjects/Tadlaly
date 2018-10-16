package com.semicolon.tadlaly.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ProfileActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener{
    private FrameLayout container;
    private ImageView back;
    private CircleImageView user_image;
    private CardView update_name,update_email,update_phone,update_password,update_country,update_username;
    private final int IMG_REQ=23;
    private Bitmap bitmap;
    private String encodedImage;
    private ProgressDialog dialog;
    private TextView name,email,phone,password,country,username;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
       /* Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);*/
        initView();
        CreateProgressDialog();
        preferences = new Preferences(this);
        userSingleTone = UserSingleTone.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        userSingleTone.getUser(this);
    }

    private void initView() {
        container = findViewById(R.id.update_image);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        country = findViewById(R.id.country);
        user_image= findViewById(R.id.image);
        username = findViewById(R.id.username);
        update_name = findViewById(R.id.update_name);
        update_email = findViewById(R.id.update_email);
        update_phone = findViewById(R.id.update_phone);
        update_username = findViewById(R.id.update_username);
        update_password = findViewById(R.id.update_password);
        update_country = findViewById(R.id.update_country);
        back = findViewById(R.id.back);
        back.setOnClickListener(view -> finish());
        update_name.setOnClickListener(view ->
                {
                    Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                    intent.putExtra("type", Tags.update_name);
                    startActivity(intent);

                }
        );
        update_email.setOnClickListener(view ->
                {
                    Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                    intent.putExtra("type", Tags.update_email);
                    startActivity(intent);

                }
        );
        update_phone.setOnClickListener(view ->
                {
                    Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                    intent.putExtra("type", Tags.update_phone);
                    startActivity(intent);

                }
        );
        update_password.setOnClickListener(view ->
                {
                    Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                    intent.putExtra("type", Tags.update_pass);
                    startActivity(intent);

                }
        );
        update_country.setOnClickListener(view ->
                {
                    Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                    intent.putExtra("type", Tags.update_country);
                    startActivity(intent);

                }
        );
        update_username.setOnClickListener(view ->
                {
                    Intent intent = new Intent(ProfileActivity.this,UpdateProfileActivity.class);
                    intent.putExtra("type", Tags.update_username);
                    startActivity(intent);

                }
        );
        container.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent.createChooser(intent,"select image"),IMG_REQ);

        });
    }

    private void CreateProgressDialog()
    {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.updating_prof));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
    }
    private void UpdateUi(UserModel userModel) {
        try {
            Picasso.with(this).load(Uri.parse(Tags.Image_Url+userModel.getUser_photo())).into(user_image);
            name.setText(userModel.getUser_full_name());
            email.setText(userModel.getUser_email());
            phone.setText(userModel.getUser_phone());
            password.setText("123456789");
            country.setText(userModel.getUser_city());
            username.setText(userModel.getUser_name());
        }catch (NullPointerException e)
        {
        }
        catch (Exception e)
        {
        }



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMG_REQ && resultCode == RESULT_OK && data !=null)
        {
            try {
                Uri uri = data.getData();
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                user_image.setImageBitmap(bitmap);
                encodedImage = EncodeImage(bitmap);
                UploadImage(encodedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void UploadImage(String encodedImage) {
        dialog.show();
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<UserModel> call = retrofit.create(Services.class).updatePhoto(userModel.getUser_id(), encodedImage);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                if (response.isSuccessful())
                {
                    dialog.dismiss();
                    ProfileActivity.this.userModel = response.body();
                    userSingleTone.setUserModel(response.body());
                    preferences.UpdatePref(response.body());
                    UpdateUi(response.body());

                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("Error",t.getMessage());
                Toast.makeText(ProfileActivity.this, R.string.error, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private String EncodeImage(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        byte [] bytes = outputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        UpdateUi(userModel);
    }
}
