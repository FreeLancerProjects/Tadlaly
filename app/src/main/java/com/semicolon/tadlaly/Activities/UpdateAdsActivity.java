package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.siyamed.shapeimageview.RoundedImageView;
import com.semicolon.tadlaly.Adapters.SpinnerBranchAdapter;
import com.semicolon.tadlaly.Adapters.SpinnerDeptAdapter;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.Spinner_DeptModel;
import com.semicolon.tadlaly.Models.Spinner_branchModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.GetLocationDetails;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.DepartmentSingletone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;
import com.semicolon.tadlaly.share.Common;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateAdsActivity extends AppCompatActivity implements DepartmentSingletone.onCompleteListener,View.OnClickListener,UserSingleTone.OnCompleteListener{
    private ImageView back;
    private Button updateBtn;
    private TextView location;
    private Spinner depart_spinner,branch_spinner,ad_type;
    private EditText ad_title,edt_phone,ad_address,ad_cost,ad_content;
    private CheckBox checkbox_phone_state;
    private RoundedImageView img1,img2,img3,img4,img5,img6;
    private List<RoundedImageView> roundedImageViewList;
    private MyAdsModel myAdsModel;
    private DepartmentSingletone departmentSingletone;
    private List<DepartmentsModel> departmentsModelList;
    private List<Spinner_DeptModel> deptList;
    private Bitmap bitmap1,bitmap2,bitmap3,bitmap4,bitmap5,bitmap6;
    private String isVisible;
    private ImageView image1_delete,image2_delete,image3_delete,image4_delete,image5_delete,image6_delete;
    private ImageView image1_delete_from_gallery,image2_delete_from_gallery,image3_delete_from_gallery,image4_delete_from_gallery,image5_delete_from_gallery,image6_delete_from_gallery;
    private List<ImageView> imageDeleteList;
    private SpinnerDeptAdapter deptAdapter;
    private SpinnerBranchAdapter branchAdapter;
    private List<Spinner_branchModel> spinner_branchModelList;
    private String [] type;
    private ArrayAdapter<String> arrayAdapter;
    private final  int img1_req=1;
    private final  int img2_req=2;
    private final  int img3_req=3;
    private final  int img4_req=4;
    private final  int img5_req=5;
    private final  int img6_req=6;
    private String mType="",m_dept_id="",m_branch_id="";
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private ProgressDialog dialog;
    private GetLocationDetails getLocationDetails;
    private final String read_perm = Manifest.permission.READ_EXTERNAL_STORAGE;
    private List<Uri> uriList;
    private List<MyAdsModel.Images> imagesList;
    private boolean canAddImg1=true;
    private boolean canAddImg2=true;
    private boolean canAddImg3=true;
    private boolean canAddImg4=true;
    private boolean canAddImg5=true;
    private boolean canAddImg6=true;
    private Map<Integer,Uri > map;
    private String current_language;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ads);
        //typeface = Typeface.createFromAsset(getAssets(), "OYA-Regular.ttf");
        CreateProgress_dialog();
        initView();
        uriList = new ArrayList<>();
        userSingleTone = UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        departmentSingletone = DepartmentSingletone.getInstansce();
        departmentSingletone.getDepartmentData(this);
        getDataFromIntent();
    }



    private void
    initView() {

        current_language = Paper.book().read("language", Locale.getDefault().getLanguage());

        back = findViewById(R.id.back);

        if (current_language.equals("ar"))
        {
            back.setRotation(180f);
        }


        map = new HashMap<>();
        deptList = new ArrayList<>();
        roundedImageViewList = new ArrayList<>();
        imageDeleteList = new ArrayList<>();
        spinner_branchModelList = new ArrayList<>();
        updateBtn = findViewById(R.id.update_btn);
        depart_spinner = findViewById(R.id.depart_spinner);
        branch_spinner = findViewById(R.id.branch_spinner);
        location = findViewById(R.id.location);
        ad_type = findViewById(R.id.ad_type);
        ad_title = findViewById(R.id.ad_title);
        ad_address = findViewById(R.id.ad_address);
        ad_cost = findViewById(R.id.ad_cost);
        ad_content = findViewById(R.id.ad_content);
        edt_phone = findViewById(R.id.edt_phone);
        checkbox_phone_state = findViewById(R.id.checkbox_phone_state);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);

        roundedImageViewList.add(img1);
        roundedImageViewList.add(img2);
        roundedImageViewList.add(img3);
        roundedImageViewList.add(img4);
        roundedImageViewList.add(img5);
        roundedImageViewList.add(img6);

        image1_delete_from_gallery = findViewById(R.id.image1_delete_from_gallery);
        image2_delete_from_gallery = findViewById(R.id.image2_delete_from_gallery);
        image3_delete_from_gallery = findViewById(R.id.image3_delete_from_gallery);
        image4_delete_from_gallery = findViewById(R.id.image4_delete_from_gallery);
        image5_delete_from_gallery = findViewById(R.id.image5_delete_from_gallery);
        image6_delete_from_gallery = findViewById(R.id.image6_delete_from_gallery);

        image1_delete = findViewById(R.id.image1_delete);
        image2_delete = findViewById(R.id.image2_delete);
        image3_delete = findViewById(R.id.image3_delete);
        image4_delete = findViewById(R.id.image4_delete);
        image5_delete = findViewById(R.id.image5_delete);
        image6_delete = findViewById(R.id.image6_delete);

        imageDeleteList.add(image1_delete);
        imageDeleteList.add(image2_delete);
        imageDeleteList.add(image3_delete);
        imageDeleteList.add(image4_delete);
        imageDeleteList.add(image5_delete);
        imageDeleteList.add(image6_delete);

        back.setOnClickListener(view -> finish());
        type = getResources().getStringArray(R.array.ad_state);
        arrayAdapter = new ArrayAdapter<>(this,R.layout.spinner_item,type);
        ad_type.setAdapter(arrayAdapter);
        branchAdapter = new SpinnerBranchAdapter(this,R.layout.spinner_item,spinner_branchModelList);
        branch_spinner.setAdapter(branchAdapter);

        deptAdapter = new SpinnerDeptAdapter(this,R.layout.spinner_item,deptList);
        depart_spinner.setAdapter(deptAdapter);
        checkbox_phone_state.setOnClickListener(view -> {
            if (checkbox_phone_state.isChecked())
            {
                isVisible=Tags.show_phone;
            }else
                {
                    isVisible=Tags.disApearPhone;
                }
        });
        depart_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                m_dept_id = deptList.get(i).getId();

                if (i==0||depart_spinner.getSelectedItem().toString().equals(getString(R.string.dept))|| TextUtils.isEmpty(m_dept_id))
                {
                    spinner_branchModelList.clear();
                    m_dept_id="";
                    Spinner_branchModel spinner_branchModel = new Spinner_branchModel("",getString(R.string.branch));
                    spinner_branchModelList.add(spinner_branchModel);
                    branch_spinner.setSelection(0);
                    branchAdapter.notifyDataSetChanged();
                }else
                    {
                        String id = m_dept_id;
                        UpdateBranchAdapter(id);
                    }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        branch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String id = spinner_branchModelList.get(i).getId();
                if (i==0&&id.equals(getString(R.string.branch)))
                {
                    m_branch_id = "";
                }
                else if (id.equals(getString(R.string.no_branch)))
                {
                    m_branch_id="0";

                }else
                {
                    m_branch_id=id;
                }
                Log.e("branch id",m_branch_id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ad_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String t = ad_type.getSelectedItem().toString();

                if (i==0&&ad_type.getSelectedItem().toString().equals(getString(R.string.no_branch)))
                {
                    mType="";
                }
                else if (t.equals(Tags.ad_new))
                {
                    mType = Tags.ad_new;
                }else if (t.equals(Tags.ad_old))
                {
                    mType = Tags.ad_old;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        image1_delete.setOnClickListener(this);
        image2_delete.setOnClickListener(this);
        image3_delete.setOnClickListener(this);
        image4_delete.setOnClickListener(this);
        image5_delete.setOnClickListener(this);
        image6_delete.setOnClickListener(this);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);

        image1_delete_from_gallery.setOnClickListener(this);
        image2_delete_from_gallery.setOnClickListener(this);
        image3_delete_from_gallery.setOnClickListener(this);
        image4_delete_from_gallery.setOnClickListener(this);
        image5_delete_from_gallery.setOnClickListener(this);
        image6_delete_from_gallery.setOnClickListener(this);

        updateBtn.setOnClickListener(this);
        location.setOnClickListener(this);

    }
    private void CheckReadPermission(int req)
    {
        if (ContextCompat.checkSelfPermission(this,read_perm )!= PackageManager.PERMISSION_GRANTED)
        {
            String [] perm = {read_perm};
            ActivityCompat.requestPermissions(this,perm,req);
        }else
            {
                SelectImage(req);
            }
    }
    private void CreateProgress_dialog() {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.updating_ad));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIndeterminateDrawable(drawable);
    }
    private void UpdateBranchAdapter(String id) {
        spinner_branchModelList.clear();
        Spinner_branchModel spinner_branchModel2 = new Spinner_branchModel("",getString(R.string.branch));
        spinner_branchModelList.add(spinner_branchModel2);
        for (DepartmentsModel departmentsModel :departmentsModelList)
        {
            if (departmentsModel.getMain_department_id().equals(id))
            {
                List<DepartmentsModel.SubdepartObject> subdepartObjectList = departmentsModel.getSubdepartObjectList();
                if (subdepartObjectList.size()>0)
                {
                    for (DepartmentsModel.SubdepartObject object:subdepartObjectList)
                    {
                        Spinner_branchModel spinner_branchModel = new Spinner_branchModel(object.getSub_department_id(),object.getSub_department_name());
                        spinner_branchModelList.add(spinner_branchModel);
                    }
                }else
                    {
                        Spinner_branchModel spinner_branchModel3 = new Spinner_branchModel(getString(R.string.no_branch),getString(R.string.no_branch));

                        spinner_branchModelList.add(spinner_branchModel3);

                    }

                branchAdapter.notifyDataSetChanged();
                branch_spinner.setSelection(0);

            }
        }
        branchAdapter.notifyDataSetChanged();
        branch_spinner.setSelection(0);



    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        if (intent!=null)
        {
            myAdsModel = (MyAdsModel) intent.getSerializableExtra("ad_details");
            UpdateUi(myAdsModel);
        }
    }

    private void UpdateUi(MyAdsModel myAdsModel)
    {
        ad_title.setText(myAdsModel.getAdvertisement_title());
        ad_address.setText(myAdsModel.getCity());
        ad_content.setText(myAdsModel.getAdvertisement_content());
        ad_cost.setText(myAdsModel.getAdvertisement_price()+" ريال");
        edt_phone.setText(myAdsModel.getPhone());
        if (myAdsModel.getShow_phone().equals(Tags.show_phone))
        {
            checkbox_phone_state.setChecked(true);
            isVisible = Tags.show_phone;
        }
        else if (myAdsModel.getShow_phone().equals(Tags.disApearPhone))
        {
                checkbox_phone_state.setChecked(false);
                isVisible = Tags.disApearPhone;
        }

        if (myAdsModel.getAdvertisement_type().equals(Tags.ad_new))
        {
            ad_type.setSelection(1);
            mType=Tags.ad_new;
        }else if (myAdsModel.getAdvertisement_type().equals(Tags.ad_old))
        {
            mType=Tags.ad_old;

            ad_type.setSelection(2);
        } else
        {
            mType="";

            ad_type.setSelection(0);
        }



        imagesList = myAdsModel.getAdvertisement_image();
        if (imagesList.size()>0)
        {
            for (int position =0;position<imagesList.size();position++)
            {
                MyAdsModel.Images images =imagesList.get(position);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+images.getPhoto_name())).into(roundedImageViewList.get(position));
                imageDeleteList.get(position).setVisibility(View.VISIBLE);

            }

            if (imagesList.size()==0)
            {
                canAddImg1 = true;
                canAddImg2 = true;
                canAddImg3 = true;
                canAddImg4 = true;
                canAddImg5 = true;
                canAddImg6 = true;

            }else if (imagesList.size()==1)
            {
                canAddImg1 = false;
                canAddImg2 = true;
                canAddImg3 = true;
                canAddImg4 = true;
                canAddImg5 = true;
                canAddImg6 = true;
            }else if (imagesList.size()==2)
            {
                canAddImg1 = false;
                canAddImg2 = false;
                canAddImg3 = true;
                canAddImg4 = true;
                canAddImg5 = true;
                canAddImg6 = true;
            }else if (imagesList.size()==3)
            {
                canAddImg1 = false;
                canAddImg2 = false;
                canAddImg3 = false;
                canAddImg4 = true;
                canAddImg5 = true;
                canAddImg6 = true;
            }else if (imagesList.size()==4)
            {
                canAddImg1 = false;
                canAddImg2 = false;
                canAddImg3 = false;
                canAddImg4 = false;
                canAddImg5 = true;
                canAddImg6 = true;
            }else if (imagesList.size()==5)
            {
                canAddImg1 = false;
                canAddImg2 = false;
                canAddImg3 = false;
                canAddImg4 = false;
                canAddImg5 = false;
                canAddImg6 = true;
            }
            else if (imagesList.size()==6)
            {
                canAddImg1 = false;
                canAddImg2 = false;
                canAddImg3 = false;
                canAddImg4 = false;
                canAddImg5 = false;
                canAddImg6 = false;
            }

        }


    }

    @Override
    public void onSuccess(List<DepartmentsModel> departmentsModelList) {
        try {
            this.departmentsModelList = departmentsModelList;
            deptList.clear();
            if (departmentsModelList.size()>0)
            {  Spinner_DeptModel spinner_deptModel = new Spinner_DeptModel("",getString(R.string.dept));
                deptList.add(spinner_deptModel);
                for (int i=0;i<departmentsModelList.size();i++)
                {
                    Spinner_DeptModel spinner_deptModel2 = new Spinner_DeptModel(departmentsModelList.get(i).getMain_department_id(),departmentsModelList.get(i).getMain_department_name());
                    deptList.add(spinner_deptModel2);
                }
                deptAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e)
        {
            getDepartment();
        }


    }

    private void getDepartment() {
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<List<DepartmentsModel>> call = retrofit.create(Services.class).getDepartmentData();
        call.enqueue(new Callback<List<DepartmentsModel>>() {
            @Override
            public void onResponse(Call<List<DepartmentsModel>> call, Response<List<DepartmentsModel>> response) {
                if (response.isSuccessful())
                {
                    departmentsModelList = new ArrayList<>();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            departmentSingletone.setDpartmentData(response.body());

                            if (departmentsModelList.size()==0)
                            {
                                Toast.makeText(UpdateAdsActivity.this,R.string.no_dept, Toast.LENGTH_LONG).show();

                            }else if (departmentsModelList.size()>0)
                            {

                                departmentSingletone.getDepartmentData(UpdateAdsActivity.this);

                            }
                        }
                    },500);

                }
            }

            @Override
            public void onFailure(Call<List<DepartmentsModel>> call, Throwable t) {
                try {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(UpdateAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e)
                {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(UpdateAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(UpdateAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void CreateDeleteAlertDialog(String img_id,int position)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();

        View view = LayoutInflater.from(this).inflate(R.layout.custom_delete_dialog,null);
        Button deleteBtn = view.findViewById(R.id.deleteBtn);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                deleteImage(img_id,position);
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.getWindow().getAttributes().windowAnimations=R.style.dialog;
        alertDialog.show();

    }

    private void deleteImage(String img_id,int position)
    {
        ProgressDialog dialog  = Common.createProgressDialog(this,getString(R.string.deltng));
        dialog.show();
        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .deleteAdsImage(userModel.getUser_id(),img_id)
                .enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                        if (response.isSuccessful())
                        {
                            dialog.dismiss();

                            if (response.body().getSuccess_delete()==1)
                            {
                                imageDeleteList.get(position).setVisibility(View.GONE);

                                roundedImageViewList.get(position).setImageBitmap(null);
                                roundedImageViewList.get(position).setImageResource(R.drawable.imgs_bg);
                                if (position==0)
                                {
                                    bitmap1=null;
                                }else if (position==1)
                                {
                                    bitmap2=null;

                                }
                                else if (position==2)
                                {
                                    bitmap3=null;

                                }
                                else if (position==3)
                                {
                                    bitmap4=null;

                                }
                                else if (position==4)
                                {
                                    bitmap5=null;

                                }
                                else if (position==5)
                                {
                                    bitmap6=null;

                                }
                                finish();
                            }else
                                {
                                    Toast.makeText(UpdateAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                                }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel> call, Throwable t) {
                        Log.e("error",t.getMessage());
                        dialog.dismiss();
                        Toast.makeText(UpdateAdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.img1:
                if (canAddImg1)
                {
                    CheckReadPermission(img1_req);

                }
                break;
            case R.id.img2:
                if (canAddImg2)
                {
                    CheckReadPermission(img2_req);

                }
                break;
            case R.id.img3:
                if (canAddImg3)
                {
                    CheckReadPermission(img3_req);

                }
                break;
            case R.id.img4:
                if (canAddImg4)
                {
                    CheckReadPermission(img4_req);

                }
                break;
            case R.id.img5:
                if (canAddImg5)
                {
                    CheckReadPermission(img5_req);

                }
                break;
            case R.id.img6:
                if (canAddImg6)
                {
                    CheckReadPermission(img6_req);

                }
                break;

            case R.id.image1_delete:
                CreateDeleteAlertDialog(imagesList.get(0).getId_photo(),0);
                break;
            case R.id.image2_delete:
                CreateDeleteAlertDialog(imagesList.get(1).getId_photo(),1);

                break;
            case R.id.image3_delete:
                CreateDeleteAlertDialog(imagesList.get(2).getId_photo(),2);

                break;
            case R.id.image4_delete:
                CreateDeleteAlertDialog(imagesList.get(3).getId_photo(),3);

                break;
            case R.id.image5_delete:
                CreateDeleteAlertDialog(imagesList.get(4).getId_photo(),4);

                break;
            case R.id.image6_delete:
                CreateDeleteAlertDialog(imagesList.get(5).getId_photo(),5);

                break;

                ////////////////////////
            case R.id.image1_delete_from_gallery:

                bitmap1=null;
                img1.setImageBitmap(bitmap1);
                img1.setImageResource(R.drawable.imgs_bg);
                map.remove(1);
                image1_delete_from_gallery.setVisibility(View.GONE);
                break;
            case R.id.image2_delete_from_gallery:
                bitmap2=null;
                img2.setImageBitmap(bitmap2);
                img2.setImageResource(R.drawable.imgs_bg);
                map.remove(2);
                image2_delete_from_gallery.setVisibility(View.GONE);

                break;
            case R.id.image3_delete_from_gallery:
                bitmap3=null;
                img3.setImageBitmap(bitmap3);
                img3.setImageResource(R.drawable.imgs_bg);
                map.remove(3);
                image3_delete_from_gallery.setVisibility(View.GONE);

                break;
            case R.id.image4_delete_from_gallery:
                bitmap4=null;
                img4.setImageBitmap(bitmap4);
                img4.setImageResource(R.drawable.imgs_bg);
                map.remove(4);
                image4_delete_from_gallery.setVisibility(View.GONE);

                break;
            case R.id.image5_delete_from_gallery:
                bitmap5=null;
                img5.setImageBitmap(bitmap5);
                img5.setImageResource(R.drawable.imgs_bg);
                map.remove(5);
                image5_delete_from_gallery.setVisibility(View.GONE);

                break;
            case R.id.image6_delete_from_gallery:
                bitmap6=null;
                img6.setImageBitmap(bitmap6);
                img6.setImageResource(R.drawable.imgs_bg);
                map.remove(6);
                image6_delete_from_gallery.setVisibility(View.GONE);

                break;
            case R.id.update_btn:
                UpdateAds();
                break;
            case R.id.location:
                getLocation();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==img1_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage(img1_req);
                }else
                    {
                        Toast.makeText(this,R.string.perm_read_denied, Toast.LENGTH_SHORT).show();
                    }
            }
        }else if (requestCode==img2_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage(img2_req);
                }else
                {
                    Toast.makeText(this,R.string.perm_read_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode==img3_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage(img3_req);
                }else
                {
                    Toast.makeText(this,R.string.perm_read_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode==img4_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage(img4_req);
                }else
                {
                    Toast.makeText(this,R.string.perm_read_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode==img5_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage(img5_req);
                }else
                {
                    Toast.makeText(this,R.string.perm_read_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (requestCode==img6_req)
        {
            if (grantResults.length>0)
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    SelectImage(img6_req);
                }else
                {
                    Toast.makeText(this,R.string.perm_read_denied, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void getLocation()
    {
        if (getLocationDetails==null)
        {
            getLocationDetails = new GetLocationDetails();
        }
        if (TextUtils.isEmpty(userModel.getUser_google_lat())||userModel.getUser_google_lat()!=null||!userModel.getUser_google_lat().equals("0")||!userModel.getUser_google_lat().equals("0.0"))
        {
            if (TextUtils.isEmpty(userModel.getUser_google_long())||userModel.getUser_google_long()!=null||!userModel.getUser_google_long().equals("0")||!userModel.getUser_google_long().equals("0.0"))
            {
                getLocationDetails.getLocation(this,"2",Double.parseDouble(userModel.getUser_google_lat()),Double.parseDouble(userModel.getUser_google_long()));

            }
        }
    }
    public void setFullLocation(String location)
    {
        this.location.setText(location);

    }
    private void UpdateAds() {

        String phone_regex = "^[+]?[0-9]{6,}$";

        String m_name = ad_title.getText().toString().trim();
        String m_address= ad_address.getText().toString().trim();
        String m_price = ad_cost.getText().toString().trim();
        String m_phone = edt_phone.getText().toString().trim();
        String m_content = ad_content.getText().toString().trim();


        if (!TextUtils.isEmpty(m_name)&&
                !TextUtils.isEmpty(m_address)&&!TextUtils.isEmpty(m_price) &&
                !TextUtils.isEmpty(m_phone)&&
                m_phone.matches(phone_regex)&&
                !TextUtils.isEmpty(m_content)&&
                !TextUtils.isEmpty(m_dept_id)&&
                !TextUtils.isEmpty(m_branch_id)&&!m_branch_id.equals("0")&&
                !TextUtils.isEmpty(m_price)&&
                !TextUtils.isEmpty(mType)
        )
        {
            dialog.show();

            if (map.size()==0)
            {
                Toast.makeText(this, R.string.ch_ad_imgs, Toast.LENGTH_SHORT).show();
            }else if (map.size()>0)
            {
                UpdateAdsWithImage(m_price,m_name,m_content,m_address,m_phone);
            }
        }else
            {
                if (TextUtils.isEmpty(m_name))
                {
                    ad_title.setError(getString(R.string.field_req));
                }else
                    {
                        ad_title.setError(null);
                    }

                if (TextUtils.isEmpty(m_address))
                {
                    ad_address.setError(getString(R.string.field_req));
                }else
                    {
                        ad_address.setError(null);
                    }


                if (TextUtils.isEmpty(m_price))
                {
                    ad_cost.setError(getString(R.string.field_req));


                }

                if (TextUtils.isEmpty(m_phone))
                {
                    edt_phone.setError(getString(R.string.field_req));

                }else if (!m_phone.matches(phone_regex))
                {
                    edt_phone.setError(getString(R.string.inv_phone));
                }else
                    {
                        edt_phone.setError(null);
                    }

                if (TextUtils.isEmpty(mType))
                {

                    Toast.makeText(this, R.string.ch_ad_type, Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(m_dept_id))
                {
                    Toast.makeText(this, R.string.ch_dept, Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(m_branch_id))
                {

                    Toast.makeText(this, R.string.ch_branch, Toast.LENGTH_SHORT).show();

                }

                if (m_branch_id.equals("0"))
                {

                    Toast.makeText(this, R.string.nobranch_to_dept, Toast.LENGTH_SHORT).show();

                }
            }


    }

    private void UpdateAdsWithImage(String m_price,String m_name,String m_content,String m_address,String m_phone) {

        for (Integer key : map.keySet())
        {
            uriList.add(map.get(key));
            Log.e("url",map.get(key)+"");
        }
        String[] split = m_price.split(" ");
        Log.e("cost",split[0]);

        RequestBody dept_id_part = Common.getRequestBody(m_dept_id);
        RequestBody branch_id_part = Common.getRequestBody(m_branch_id);
        RequestBody name_part = Common.getRequestBody(m_name);
        RequestBody content_part = Common.getRequestBody(m_content);
        RequestBody price_part = Common.getRequestBody(split[0]);
        RequestBody type_part = Common.getRequestBody(mType);
        RequestBody lat_part = Common.getRequestBody(userModel.getUser_google_lat());
        RequestBody lng_part = Common.getRequestBody(userModel.getUser_google_long());
        RequestBody city_part = Common.getRequestBody(m_address);
        RequestBody phone_part = Common.getRequestBody(m_phone);
        RequestBody show_phone_part = Common.getRequestBody(isVisible);

        List<MultipartBody.Part> partList = getMultipartBodyList(uriList);

        Map<String, RequestBody> map = new HashMap<>();
        map.put("main_department",dept_id_part);
        map.put("sub_department",branch_id_part);
        map.put("advertisement_title",name_part);
        map.put("advertisement_content",content_part);
        map.put("advertisement_price",price_part);
        map.put("advertisement_type",type_part);
        map.put("google_lat",lat_part);
        map.put("google_long",lng_part);
        map.put("city",city_part);
        map.put("phone",phone_part);
        map.put("show_phone",show_phone_part);

        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
        Call<MyAdsModel> call = retrofit.create(Services.class).updateMyAdsWithImage(myAdsModel.getId_advertisement(), map, partList);
        call.enqueue(new Callback<MyAdsModel>() {
            @Override
            public void onResponse(Call<MyAdsModel> call, Response<MyAdsModel> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getSuccess()==1)
                    {
                        dialog.dismiss();
                        Toast.makeText(UpdateAdsActivity.this, R.string.ad_upd_succ, Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        intent.putExtra("d","d");
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    }
                    else if (response.body().getSuccess()==0)
                    {
                        dialog.dismiss();
                        Toast.makeText(UpdateAdsActivity.this, R.string.error, Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<MyAdsModel> call, Throwable t) {
                Log.e("Error",t.getMessage());
                Toast.makeText(UpdateAdsActivity.this,R.string.error, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void SelectImage(int req)
    {
        Intent intent;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
        {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        }else
        {
            intent = new Intent(Intent.ACTION_GET_CONTENT);

        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        startActivityForResult(intent.createChooser(intent,getString(R.string.sel_image)),req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==img1_req && resultCode==RESULT_OK && data!=null)
        {

            Uri uri = data.getData();
            //uriList.add(uri);
            bitmap1 = BitmapFactory.decodeFile(Common.getImagePath(this,uri));
            img1.setImageBitmap(bitmap1);
            map.put(1,uri);
            image1_delete_from_gallery.setVisibility(View.VISIBLE);

        }else if (requestCode==img2_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            //uriList.add(uri);
            bitmap2 = BitmapFactory.decodeFile(Common.getImagePath(this,uri));
            img2.setImageBitmap(bitmap2);
            map.put(2,uri);
            image2_delete_from_gallery.setVisibility(View.VISIBLE);


        }
        else if (requestCode==img3_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            //uriList.add(uri);
            bitmap3 = BitmapFactory.decodeFile(Common.getImagePath(this,uri));
            img3.setImageBitmap(bitmap3);
            map.put(3,uri);
            image3_delete_from_gallery.setVisibility(View.VISIBLE);



        }else if (requestCode==img4_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            //uriList.add(uri);
            bitmap4 = BitmapFactory.decodeFile(Common.getImagePath(this,uri));
            img4.setImageBitmap(bitmap4);
            map.put(4,uri);
            image4_delete_from_gallery.setVisibility(View.VISIBLE);


        }else if (requestCode==img5_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            //uriList.add(4,uri);
            bitmap5 = BitmapFactory.decodeFile(Common.getImagePath(this,uri));
            img5.setImageBitmap(bitmap5);
            map.put(5,uri);
            image5_delete_from_gallery.setVisibility(View.VISIBLE);


        }else if (requestCode==img6_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            //uriList.add(uri);
            bitmap6 = BitmapFactory.decodeFile(Common.getImagePath(this,uri));
            img6.setImageBitmap(bitmap6);
            map.put(6,uri);
            image6_delete_from_gallery.setVisibility(View.VISIBLE);


        }

    }

    private List<MultipartBody.Part> getMultipartBodyList(List<Uri> uriList)
    {
        List<MultipartBody.Part> partList = new ArrayList<>();
        for (Uri uri:uriList) {
            MultipartBody.Part part = Common.getListMultiPartBody(uri,this);
            partList.add(part);
        }
        return partList;
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }


}
