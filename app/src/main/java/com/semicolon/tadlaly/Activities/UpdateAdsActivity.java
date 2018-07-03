package com.semicolon.tadlaly.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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
import com.lamudi.phonefield.PhoneInputLayout;
import com.semicolon.tadlaly.Adapters.SpinnerBranchAdapter;
import com.semicolon.tadlaly.Adapters.SpinnerDeptAdapter;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UpdateAdsActivity extends AppCompatActivity implements DepartmentSingletone.onCompleteListener,View.OnLongClickListener,View.OnClickListener,UserSingleTone.OnCompleteListener{
    private ImageView back;
    private Button updateBtn;
    private TextView location;
    private Spinner depart_spinner,branch_spinner,ad_type;
    private EditText ad_title,ad_address,ad_cost,ad_content;
    private PhoneInputLayout phone;
    private CheckBox checkbox_phone_state;
    private RoundedImageView img1,img2,img3,img4,img5,img6;
    private MyAdsModel myAdsModel;
    private DepartmentSingletone departmentSingletone;
    private List<DepartmentsModel> departmentsModelList;
    private List<Spinner_DeptModel> deptList;
    private Bitmap bitmap1,bitmap2,bitmap3,bitmap4,bitmap5,bitmap6;
    private String isVisible;
    private Target target1,target2,target3,target4,target5,target6;
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
    private List<Bitmap> bitmapList;
    private Typeface typeface;
    private String mType="",m_dept_id="",m_branch_id="";
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private List<String> encodedImages ;
    private ProgressDialog dialog;
    private GetLocationDetails getLocationDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_ads);
        typeface = Typeface.createFromAsset(getAssets(), "OYA-Regular.ttf");
        CreateProgress_dialog();
        initView();
        userSingleTone = UserSingleTone.getInstance();
        userSingleTone.getUser(this);
        departmentSingletone = DepartmentSingletone.getInstansce();
        departmentSingletone.getDepartmentData(this);
        getDataFromIntent();
    }



    private void initView() {
        encodedImages = new ArrayList<>();
        deptList = new ArrayList<>();
        bitmapList = new ArrayList<>();
        spinner_branchModelList = new ArrayList<>();
        back = findViewById(R.id.back);
        updateBtn = findViewById(R.id.update_btn);
        updateBtn.setTypeface(typeface);
        depart_spinner = findViewById(R.id.depart_spinner);
        branch_spinner = findViewById(R.id.branch_spinner);
        location = findViewById(R.id.location);
        ad_type = findViewById(R.id.ad_type);
        ad_title = findViewById(R.id.ad_title);
        ad_title.setTypeface(typeface);
        ad_address = findViewById(R.id.ad_address);
        ad_address.setTypeface(typeface);
        ad_cost = findViewById(R.id.ad_cost);
        ad_cost.setTypeface(typeface);
        ad_content = findViewById(R.id.ad_content);
        ad_content.setTypeface(typeface);
        phone = findViewById(R.id.phone);
        checkbox_phone_state = findViewById(R.id.checkbox_phone_state);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);

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
                Log.e("branchid",id);
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

        img1.setOnLongClickListener(this);
        img2.setOnLongClickListener(this);
        img3.setOnLongClickListener(this);
        img4.setOnLongClickListener(this);
        img5.setOnLongClickListener(this);
        img6.setOnLongClickListener(this);

        img1.setOnClickListener(this);
        img2.setOnClickListener(this);
        img3.setOnClickListener(this);
        img4.setOnClickListener(this);
        img5.setOnClickListener(this);
        img6.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        location.setOnClickListener(this);

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

    private void UpdateUi(MyAdsModel myAdsModel) {
        ad_title.setText(myAdsModel.getAdvertisement_title());
        ad_address.setText(myAdsModel.getCity());
        ad_content.setText(myAdsModel.getAdvertisement_content());
        ad_cost.setText(myAdsModel.getAdvertisement_price()+" ريال");
        Log.e("phone",myAdsModel.getPhone());
        phone.getTextInputLayout().getEditText().setText(myAdsModel.getPhone());
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



        List<MyAdsModel.Images> imagesList = myAdsModel.getAdvertisement_image();
        if (imagesList.size()>0)
        {
            if (imagesList.size()==1)
            {
                bitmapList.clear();

                target1 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap1 = bitmap;
                        img1.setImageBitmap(bitmap1);
                        bitmapList.add(bitmap1);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(this).load(Uri.parse(Tags.Image_Url)+imagesList.get(0).getPhoto_name()).into(target1);
            }else if (imagesList.size()==2)
            {
                bitmapList.clear();
                target1 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap1 = bitmap;
                        img1.setImageBitmap(bitmap1);
                        bitmapList.add(bitmap);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target2 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap2 = bitmap;
                        img2.setImageBitmap(bitmap);
                        bitmapList.add(bitmap);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(0).getPhoto_name())).into(target1);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(1).getPhoto_name())).into(target2);




            }
            else if (imagesList.size()==3)
            {
                bitmapList.clear();
                target1 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap1 = bitmap;
                        img1.setImageBitmap(bitmap1);
                        bitmapList.add(bitmap1);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target2 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap2 = bitmap;
                        img2.setImageBitmap(bitmap2);
                        bitmapList.add(bitmap2);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target3 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap3 = bitmap;
                        img3.setImageBitmap(bitmap3);
                        bitmapList.add(bitmap3);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(0).getPhoto_name())).into(target1);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(1).getPhoto_name())).into(target2);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(2).getPhoto_name())).into(target3);

            }
            else if (imagesList.size()==4)
            {
                bitmapList.clear();
                target1 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap1 = bitmap;
                        img1.setImageBitmap(bitmap1);
                        bitmapList.add(bitmap1);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target2 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap2 = bitmap;
                        img2.setImageBitmap(bitmap2);
                        bitmapList.add(bitmap2);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target3 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap3 = bitmap;
                        img3.setImageBitmap(bitmap3);
                        bitmapList.add(bitmap3);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target4 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap4 = bitmap;
                        img4.setImageBitmap(bitmap4);
                        bitmapList.add(bitmap4);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(0).getPhoto_name())).into(target1);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(1).getPhoto_name())).into(target2);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(2).getPhoto_name())).into(target3);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(3).getPhoto_name())).into(target4);

            }
            else if (imagesList.size()==5)
            {
                bitmapList.clear();
                target1 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap1 = bitmap;
                        img1.setImageBitmap(bitmap1);
                        bitmapList.add(bitmap1);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target2 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap2 = bitmap;
                        img2.setImageBitmap(bitmap2);
                        bitmapList.add(bitmap2);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target3 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap3 = bitmap;
                        img3.setImageBitmap(bitmap3);
                        bitmapList.add(bitmap3);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target4 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap4 = bitmap;
                        img4.setImageBitmap(bitmap4);
                        bitmapList.add(bitmap4);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target5 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap5 = bitmap;
                        img5.setImageBitmap(bitmap5);
                        bitmapList.add(bitmap5);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(0).getPhoto_name())).into(target1);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(1).getPhoto_name())).into(target2);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(2).getPhoto_name())).into(target3);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(3).getPhoto_name())).into(target4);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(4).getPhoto_name())).into(target5);

            }
            else if (imagesList.size()==6)
            {
                target1 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap1 = bitmap;
                        img1.setImageBitmap(bitmap1);
                        bitmapList.add(bitmap1);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target2 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap2 = bitmap;
                        img2.setImageBitmap(bitmap2);
                        bitmapList.add(bitmap2);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target3 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap3 = bitmap;
                        img3.setImageBitmap(bitmap3);
                        bitmapList.add(bitmap3);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target4 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap4 = bitmap;
                        img4.setImageBitmap(bitmap4);
                        bitmapList.add(bitmap4);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target5 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap5 = bitmap;
                        img5.setImageBitmap(bitmap5);
                        bitmapList.add(bitmap5);


                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                target6 = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bitmap6 = bitmap;
                        img6.setImageBitmap(bitmap6);
                        bitmapList.add(bitmap6);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(0).getPhoto_name())).into(target1);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(1).getPhoto_name())).into(target2);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(2).getPhoto_name())).into(target3);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(3).getPhoto_name())).into(target4);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(4).getPhoto_name())).into(target5);
                Picasso.with(this).load(Uri.parse(Tags.Image_Url+imagesList.get(5).getPhoto_name())).into(target6);

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelRequest(target1);
        Picasso.with(this).cancelRequest(target2);
        Picasso.with(this).cancelRequest(target3);
        Picasso.with(this).cancelRequest(target4);
        Picasso.with(this).cancelRequest(target5);
        Picasso.with(this).cancelRequest(target6);

    }

    @Override
    public boolean onLongClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.img1:
                if (bitmapList.size()>0)
                {
                    img1.setImageBitmap(null);
                    img1.setBackgroundResource(R.drawable.imgs_bg);
                    bitmap1=null;
                    bitmapList.remove(0);
                }


                break;
            case R.id.img2:
                if (bitmapList.size()>=2)
                {
                    img2.setImageBitmap(null);
                    img2.setBackgroundResource(R.drawable.imgs_bg);
                    bitmap2=null;
                    bitmapList.remove(1);
                }





                break;
            case R.id.img3:
                if (bitmapList.size()>=3)
                {
                    img3.setImageBitmap(null);
                    img3.setBackgroundResource(R.drawable.imgs_bg);
                    bitmap3=null;
                    bitmapList.remove(2);
                }




                break;
            case R.id.img4:
                if (bitmapList.size()>=4)
                {
                    img4.setImageBitmap(null);
                    img4.setBackgroundResource(R.drawable.imgs_bg);
                    bitmap4=null;
                    bitmapList.remove(3);
                }




                break;
            case R.id.img5:
                if (bitmapList.size()>=5)
                {
                    img5.setImageBitmap(null);
                    img5.setBackgroundResource(R.drawable.imgs_bg);
                    bitmap5=null;
                    bitmapList.remove(4);
                }




                break;
            case R.id.img6:
                if (bitmapList.size()==6)
                {
                    img6.setImageBitmap(null);
                    img6.setBackgroundResource(R.drawable.imgs_bg);
                    bitmap6=null;
                    bitmapList.remove(5);
                }




                break;

        }
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.img1:
                SelectImage(img1_req);
                break;
            case R.id.img2:
                SelectImage(img2_req);
                break;
            case R.id.img3:
                SelectImage(img3_req);
                break;
            case R.id.img4:
                SelectImage(img4_req);
                break;
            case R.id.img5:
                SelectImage(img5_req);
                break;
            case R.id.img6:
                SelectImage(img6_req);
                break;
            case R.id.update_btn:
                UpdateAds();
                break;
            case R.id.location:
                getLocation();
                break;
        }
    }

    private void getLocation() {
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
        String m_name = ad_title.getText().toString();
        String m_address= ad_address.getText().toString();
        String m_price = ad_cost.getText().toString();
        String m_phone = phone.getPhoneNumber();
        String m_content = ad_content.getText().toString();

        if (TextUtils.isEmpty(m_name))
        {
            ad_title.setError(getString(R.string.enter_ad_name));
        }
        else if (TextUtils.isEmpty(m_address))
        {
            ad_title.setError(null);
            ad_address.setError(getString(R.string.enter_address));
        }else if (TextUtils.isEmpty(m_dept_id))
        {
            ad_address.setError(null);
            ad_title.setError(null);
            Toast.makeText(this, R.string.ch_dept, Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(m_branch_id))
        {
            ad_address.setError(null);
            ad_title.setError(null);
            Toast.makeText(this, R.string.ch_branch, Toast.LENGTH_SHORT).show();

        }else if (m_branch_id.equals("0"))
        {
            ad_address.setError(null);
            ad_title.setError(null);
            Toast.makeText(this, R.string.nobranch_to_dept, Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(m_price))
        {
            ad_cost.setError(getString(R.string.enter_price));
            ad_address.setError(null);
            ad_title.setError(null);

        }
        else if (TextUtils.isEmpty(mType))
        {
            ad_cost.setError(null);
            ad_address.setError(null);
            ad_title.setError(null);
            Toast.makeText(this, R.string.ch_ad_type, Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(m_phone))
        {
            phone.getTextInputLayout().getEditText().setError(getString(R.string.enter_phone));
            ad_cost.setError(null);
            ad_address.setError(null);
            ad_title.setError(null);
        }else if (!phone.isValid())
        {
            phone.getTextInputLayout().getEditText().setError(getString(R.string.inv_phone));
            ad_cost.setError(null);
            ad_address.setError(null);
            ad_title.setError(null);
        }else if (TextUtils.isEmpty(m_content))
        {
            phone.getTextInputLayout().getEditText().setError(null);
            ad_cost.setError(null);
            ad_address.setError(null);
            ad_title.setError(null);
            ad_content.setError(getString(R.string.enter_ad_content));
        }else
            {
                dialog.show();
                String[] split = m_price.split(" ");
                Log.e("cost",split[0]);
                encodedImages = EncodedImage(bitmapList);
                Map<String,String> map = new HashMap<>();
                map.put("main_department",m_dept_id);
                map.put("sub_department",m_branch_id);
                map.put("advertisement_title",m_name);
                map.put("advertisement_content",m_content);
                map.put("advertisement_price",split[0]);
                map.put("advertisement_type",mType);
                map.put("google_lat",userModel.getUser_google_lat());
                map.put("google_long",userModel.getUser_google_long());
                map.put("city",m_address);
                map.put("phone",m_phone);
                map.put("show_phone",isVisible);

                Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
                Call<MyAdsModel> call = retrofit.create(Services.class).updateMyAds(myAdsModel.getId_advertisement(), map, encodedImages);
                call.enqueue(new Callback<MyAdsModel>() {
                    @Override
                    public void onResponse(Call<MyAdsModel> call, Response<MyAdsModel> response) {
                        if (response.isSuccessful())
                        {
                            if (response.body().getSuccess()==1)
                            {
                                dialog.dismiss();
                                Toast.makeText(UpdateAdsActivity.this, R.string.ad_upd_succ, Toast.LENGTH_SHORT).show();
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

    }

    private void SelectImage(int req)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,getString(R.string.sel_image)),req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==img1_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            try {
                bitmap1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                img1.setImageBitmap(bitmap1);
                bitmapList.add(0,bitmap1);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode==img2_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            try {
                bitmap2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                img2.setImageBitmap(bitmap2);
                bitmapList.add(1,bitmap2);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode==img3_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            try {
                bitmap3 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                img3.setImageBitmap(bitmap3);
                bitmapList.add(2,bitmap3);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode==img4_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            try {
                bitmap4 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                img4.setImageBitmap(bitmap4);
                bitmapList.add(3,bitmap4);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode==img5_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            try {
                bitmap5 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                img5.setImageBitmap(bitmap5);
                bitmapList.add(4,bitmap5);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else if (requestCode==img6_req && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            try {
                bitmap6 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                img6.setImageBitmap(bitmap6);
                bitmapList.add(5,bitmap6);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private List<String> EncodedImage(List<Bitmap> bitmaps)
    {
        List<String> encodedImages = new ArrayList<>();
        for (Bitmap bitmap :bitmaps)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,90,outputStream);
            byte [] bytes = outputStream.toByteArray();

            encodedImages.add(Base64.encodeToString(bytes,Base64.DEFAULT));
        }
        return encodedImages;
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
    }
}
