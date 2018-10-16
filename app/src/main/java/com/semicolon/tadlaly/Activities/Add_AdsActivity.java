package com.semicolon.tadlaly.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.semicolon.tadlaly.Adapters.SpinnerBranchAdapter;
import com.semicolon.tadlaly.Adapters.SpinnerDeptAdapter;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.Spinner_DeptModel;
import com.semicolon.tadlaly.Models.Spinner_branchModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.DepartmentSingletone;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Add_AdsActivity extends AppCompatActivity implements UserSingleTone.OnCompleteListener,DepartmentSingletone.onCompleteListener{
    private ImageView back,uploadImages;
    private RoundedImageView img1,img2,img3,img4,img5,img6;
    private int img_num=0;
    private final int IMG_REQ=2;
    private Bitmap bitmap1,bitmap2,bitmap3,bitmap4,bitmap5,bitmap6;
    private List<Bitmap> bitmapList;
    private List<String> encodeImages;
    private Button send_btn;
    private EditText address,ads_content,ad_title;
    private TextView phone,location,cost;
    private ProgressDialog dialog,dialog2;
    private Spinner depart_spinner,branche_spinner,ad_type;
    private CheckBox checkbox_phone_state;
    private String isAvailable_phone= Tags.disApearPhone;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    private double lat=0.0,lng=0.0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private final int add_request = 1525;
    //private List<String> deptName,branchList;
    private DepartmentSingletone departmentSingletone;
    private List<DepartmentsModel> departmentsModelList;
    private List<DepartmentsModel.SubdepartObject> subdepartObjectList;
    private SpinnerDeptAdapter  deptAdapter;
    private SpinnerBranchAdapter branchAdapter;
    private String m_dept,m_branch,ad_state;
    private List<Spinner_DeptModel> deptList;
    private List<Spinner_branchModel> branchList;
    private String user_type="";
    private AlertDialog.Builder serviceBuilder;
    private CheckBox checkbox_undefined;
    private String price="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__ads);
        /*Calligrapher calligrapher=new Calligrapher(this);
        calligrapher.setFont(this,"OYA-Regular.ttf",true);*/
        CreateProgressDialog2();
        CreateProgressDialog();
        getDataFromIntent();
        initView();
        if (user_type.equals(Tags.app_user)) {
            userSingleTone = UserSingleTone.getInstance();
            userSingleTone.getUser(this);

        }
        departmentSingletone = DepartmentSingletone.getInstansce();
        departmentSingletone.getDepartmentData(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        CreateServiceDialog();
    }



    private void CreateServiceDialog()
    {
        serviceBuilder = new AlertDialog.Builder(this);
        serviceBuilder.setMessage(R.string.ser_not_ava);
        serviceBuilder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            AlertDialog alertDialog=serviceBuilder.create();
            alertDialog.dismiss();
            finish();

        } );

        AlertDialog alertDialog=serviceBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(false);

    }
    private void getDataFromIntent() {
        Intent intent =getIntent();
        if (intent!=null)
        {
            if (intent.hasExtra("user_type"))
            {
                user_type = intent.getStringExtra("user_type");
            }
        }
    }

    private void initView()
    {
        //branchList = new ArrayList<>();
        branchList = new ArrayList<>();
        subdepartObjectList = new ArrayList<>();
        departmentsModelList = new ArrayList<>();
        deptList = new ArrayList<>();
        //deptName = new ArrayList<>();
        encodeImages = new ArrayList<>();
        bitmapList = new ArrayList<>();
        back = findViewById(R.id.back);
        send_btn = findViewById(R.id.send_btn);
        uploadImages = findViewById(R.id.uploadImages);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        img5 = findViewById(R.id.img5);
        img6 = findViewById(R.id.img6);
        ad_title = findViewById(R.id.ad_title);
        phone = findViewById(R.id.phone);
        location = findViewById(R.id.location);
        address = findViewById(R.id.address);
        cost = findViewById(R.id.cost);
        checkbox_undefined = findViewById(R.id.checkbox_undefined);
        ads_content = findViewById(R.id.ads_content);
        depart_spinner = findViewById(R.id.depart_spinner);
        branche_spinner = findViewById(R.id.branche_spinner);
        ad_type = findViewById(R.id.ad_type);
        checkbox_phone_state = findViewById(R.id.checkbox_phone_state);

        ///////////////////////////
        checkbox_undefined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_undefined.isChecked())
                {
                    price=String.valueOf(Tags.undefined_price);
                    cost.setText(R.string.undefined);
                    cost.setError(null);
                    cost.setEnabled(false);
                }else
                    {
                        price="";
                        cost.setText(null);
                        cost.setEnabled(true);
                    }
            }
        });
        ///////////////////////////
        String [] ad_types = getResources().getStringArray(R.array.ad_state);
        ad_type.setAdapter(new ArrayAdapter<>(this,R.layout.spinner_item,ad_types));
        deptAdapter = new SpinnerDeptAdapter(this,R.layout.spinner_item,deptList);
        depart_spinner.setAdapter(deptAdapter);

        branchAdapter = new SpinnerBranchAdapter(this,R.layout.spinner_item,branchList);
        //adapter = new ArrayAdapter<>(Add_AdsActivity.this,R.layout.spinner_item,branchList);
        branche_spinner.setAdapter(branchAdapter);

        depart_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("pos",i+"");
                 Spinner_DeptModel name = deptList.get(i);
                 if (i!=0&&!TextUtils.isEmpty(name.getId()))
                 {
                     m_dept = name.getId();
                     Log.e("id",name.getId());
                 }else
                     {
                         m_dept="";
                     }
                UpdateAdapter(name.getId(),i);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        }) ;

        branche_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String brnch = branchList.get(i).getId();
                if (i==0&&brnch.equals(getString(R.string.branch)))
                {
                    m_branch = "";
                }
                else if(brnch.equals(getString(R.string.no_branch)))
                {
                    m_branch="0";
                }
                else
                {
                        m_branch=brnch;
                }
                    Log.e("branch id",brnch);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        ad_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0)
                {
                    ad_state="";
                }else if (i==1)
                {
                    ad_state=Tags.ad_new;
                }else if (i==2)
                {
                    ad_state=Tags.ad_old;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        uploadImages.setOnClickListener(view ->
                selectImages()
        );
        back.setOnClickListener(view ->
                finish()
        );
        location.setOnClickListener(view ->
                {
                    if (user_type.equals(Tags.app_user))
                    {
                        getLocation();

                    }else
                        {
                            serviceBuilder.show();
                        }
                }
        );
        send_btn.setOnClickListener(view ->
                {
                    if (user_type.equals(Tags.app_user))
                    {
                        String m_title = ad_title.getText().toString();
                        String m_address = address.getText().toString();
                        String m_adsContent = ads_content.getText().toString();
                        String m_loc = location.getText().toString();
                        //price = cost.getText().toString();
                        if (cost.isEnabled())
                        {
                            price = cost.getText().toString();
                        }
                        if (TextUtils.isEmpty(m_title))
                        {
                            ad_title.setError(getString(R.string.enter_ad_name));
                        }
                        else if (TextUtils.isEmpty(m_address))
                        {
                            ad_title.setError(null);
                            address.setError(getString(R.string.enter_address));
                        }else if (TextUtils.isEmpty(m_loc))
                        {
                            location.setError(getString(R.string.enter_location));
                            address.setError(null);
                            ad_title.setError(null);



                        }else if (TextUtils.isEmpty(m_dept))
                        {
                            address.setError(null);
                            ad_title.setError(null);
                            location.setError(null);
                            Toast.makeText(this,R.string.ch_dept, Toast.LENGTH_SHORT).show();
                        }else if (TextUtils.isEmpty(m_branch))
                        {
                            address.setError(null);
                            ad_title.setError(null);
                            location.setError(null);
                            Toast.makeText(this, R.string.ch_branch, Toast.LENGTH_SHORT).show();

                        }
                        else if (m_branch.equals("0"))
                        {
                            address.setError(null);
                            ad_title.setError(null);
                            location.setError(null);
                            Toast.makeText(this, R.string.nobranch_to_dept, Toast.LENGTH_SHORT).show();

                        }
                        else if (TextUtils.isEmpty(price))
                        {
                            cost.setError(getString(R.string.enter_price));
                            address.setError(null);
                            ad_title.setError(null);
                            location.setError(null);

                        }
                        else if (TextUtils.isEmpty(ad_state))
                        {
                            cost.setError(null);
                            address.setError(null);
                            ad_title.setError(null);
                            location.setError(null);
                            Toast.makeText(this, R.string.ch_ad_type, Toast.LENGTH_SHORT).show();
                        }
                        else if (TextUtils.isEmpty(m_adsContent))
                        {
                            cost.setError(null);
                            address.setError(null);
                            ad_title.setError(null);
                            location.setError(null);
                            ads_content.setError(getString(R.string.enter_ad_content));

                        }
                        else if(bitmapList.size()==0)
                        {
                            Toast.makeText(this, R.string.ch_ad_imgs, Toast.LENGTH_SHORT).show();
                            ads_content.setError(null);
                            cost.setError(null);
                            address.setError(null);
                            ad_title.setError(null);
                            location.setError(null);

                        }else
                        {
                            ad_title.setError(null);
                            address.setError(null);
                            location.setError(null);
                            ads_content.setError(null);
                            cost.setError(null);

                            Intent intent = new Intent(Add_AdsActivity.this,PromiseActivity.class);
                            startActivityForResult(intent,add_request);
                        }
                    }else
                        {
                            serviceBuilder.show();
                        }

                }
        );
        checkbox_phone_state.setOnClickListener(view -> {
            if (user_type.equals(Tags.app_user))
            {
                if (checkbox_phone_state.isChecked())
                {
                    isAvailable_phone =Tags.show_phone;

                }else
                {
                    isAvailable_phone =Tags.disApearPhone;
                }
            }else
                {
                    serviceBuilder.show();
                }

        });

    }

    private void UpdateAdapter(String id,int pos) {
        subdepartObjectList.clear();
        branchList.clear();

        if (pos!=0)
        {
            branchList.add(new Spinner_branchModel("",getString(R.string.branch)));

            if (departmentsModelList.size()>0)
            {
                for (DepartmentsModel dpt :departmentsModelList)
                {
                    if (dpt.getMain_department_id().equals(id))
                    {
                        for (DepartmentsModel.SubdepartObject object:dpt.getSubdepartObjectList())
                        {
                            branchList.add(new Spinner_branchModel(object.getSub_department_id(),object.getSub_department_name()));
                        }
                    }
                }
                if (branchList.size()==1)
                {
                    branchList.clear();
                    branchList.add(new Spinner_branchModel("",getString(R.string.branch)));
                    branchList.add(new Spinner_branchModel(getString(R.string.no_branch),getString(R.string.no_branch)));

                    //branchAdapter.notifyDataSetChanged();
                }
                branchAdapter.notifyDataSetChanged();
                branche_spinner.setSelection(0);
            }




        }else if (pos==0)
        {
            branchList.add(new Spinner_branchModel("",getString(R.string.branch)));

            branchAdapter.notifyDataSetChanged();
            branche_spinner.setSelection(0);

        }




    }

    private void CreateProgressDialog()
    {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.add_ads));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.setIndeterminateDrawable(drawable);
        //getString(R.string.adding_ads)
    }
    private void CreateProgressDialog2()
    {
        ProgressBar bar = new ProgressBar(this);
        Drawable drawable = bar.getIndeterminateDrawable().mutate();
        drawable.setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        dialog2 = new ProgressDialog(this);
        dialog2.setMessage(getString(R.string.locating));
        dialog2.setCanceledOnTouchOutside(false);
        dialog2.setCancelable(true);
        dialog2.setIndeterminateDrawable(drawable);
        //getString(R.string.adding_ads)
    }
    private void getLocation()
    {

        try {
            dialog2.show();
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful())
                {
                    try {
                        Location location = task1.getResult();

                        if (location!=null)
                        {
                            lat = location.getLatitude();
                            lng = location.getLongitude();

                            Geocoder geocoder = new Geocoder(Add_AdsActivity.this);
                            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
                            if (addressList.size()>0)
                            {
                                Address address = addressList.get(0);
                                if (address!=null)
                                {
                                    if (!TextUtils.isEmpty(address.getLocality())||address.getLocality()!=null)
                                    {
                                        Add_AdsActivity.this.location.setText(address.getLocality()+" "+address.getCountryName());

                                    }
                                }
                            }

                            dialog2.dismiss();




                        }else
                            {
                                dialog2.dismiss();
                                Toast.makeText(Add_AdsActivity.this,R.string.cnt_find_loc, Toast.LENGTH_LONG).show();

                            }

                    }catch (NullPointerException e)
                    {
                        dialog2.dismiss();

                        Toast.makeText(Add_AdsActivity.this,R.string.cnt_find_loc, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            });

        }catch (SecurityException e)
        {

        }
        catch (NullPointerException e)
        {
            dialog2.dismiss();
            Toast.makeText(Add_AdsActivity.this,R.string.cnt_find_loc, Toast.LENGTH_LONG).show();

        }catch (Exception e)
        {
            dialog2.dismiss();

            Toast.makeText(Add_AdsActivity.this,R.string.cnt_find_loc, Toast.LENGTH_LONG).show();

        }
    }
    private void selectImages()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,getString(R.string.sel_image)),IMG_REQ);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data!=null)
        {
            ClipData clipData = data.getClipData();
            if (clipData!=null)
            {
                if (clipData.getItemCount()>6)
                {
                    Toast.makeText(this, R.string.choose_6_image, Toast.LENGTH_LONG).show();
                }else
                    {
                        for (int i=0;i<clipData.getItemCount();i++)
                        {
                            try {
                                img_num++;
                                ClipData.Item item = clipData.getItemAt(0);
                                Uri uri = item.getUri();
                                if (i==0)
                                {
                                    bitmap1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img1.setImageBitmap(bitmap1);
                                    bitmapList.add(bitmap1);

                                }else if (i==1)
                                {
                                    bitmap2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img2.setImageBitmap(bitmap2);
                                    bitmapList.add(bitmap2);

                                }
                                else if (i==2)
                                {
                                    bitmap3 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img3.setImageBitmap(bitmap3);
                                    bitmapList.add(bitmap3);


                                }
                                else if (i==3)
                                {
                                    bitmap4 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img4.setImageBitmap(bitmap4);
                                    bitmapList.add(bitmap4);


                                }
                                else if (i==4)
                                {
                                    bitmap5 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img5.setImageBitmap(bitmap5);
                                    bitmapList.add(bitmap5);


                                }
                                else if (i==5)
                                {
                                    bitmap6 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img6.setImageBitmap(bitmap6);
                                    bitmapList.add(bitmap6);


                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }

            }else
                {
                    if (img_num>6)
                    {
                        Toast.makeText(this,R.string.choose_6_image, Toast.LENGTH_LONG).show();
                    }else
                        {
                            Uri uri = data.getData();
                            img_num++;

                            if (bitmap1==null)
                            {
                                try {
                                    bitmap1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img1.setImageBitmap(bitmap1);
                                    bitmapList.add(bitmap1);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }else if (bitmap2==null)
                            {
                                try {
                                    bitmap2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img2.setImageBitmap(bitmap2);
                                    bitmapList.add(bitmap2);


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }else if (bitmap3==null)
                            {
                                try {
                                    bitmap3 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img3.setImageBitmap(bitmap3);
                                    bitmapList.add(bitmap3);


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (bitmap4==null)
                            {
                                try {
                                    bitmap4 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img4.setImageBitmap(bitmap4);
                                    bitmapList.add(bitmap4);


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (bitmap5==null)
                            {
                                try {
                                    bitmap5 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img5.setImageBitmap(bitmap5);
                                    bitmapList.add(bitmap5);


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if (bitmap6==null)
                            {
                                try {
                                    bitmap6 = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                                    img6.setImageBitmap(bitmap6);
                                    bitmapList.add(bitmap6);


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                }
        }else if (requestCode == add_request && resultCode == RESULT_OK )
        {
            AddAds();
        }
    }

    private void AddAds() {
        try {
            dialog.show();
            encodeImages = EncodeImage(bitmapList);

            String m_title = ad_title.getText().toString();
            String m_address = address.getText().toString();
            String m_adsContent = ads_content.getText().toString();
            String m_loc = location.getText().toString();
            //String m_cost = cost.getText().toString();
            String m_phone = phone.getText().toString();

            Log.e("1",m_title);
            Log.e("2",m_address);
            Log.e("3",price);
            Log.e("4",m_adsContent);
            Log.e("5",isAvailable_phone);
            Log.e("6",m_dept);
            Log.e("7",m_branch);
            Log.e("8",ad_state);
            Log.e("9",lat+"");
            Log.e("10",lng+"");
            Log.e("11",m_phone);

            Map<String,String> map = new HashMap<>();
            map.put("main_department",m_dept);
            map.put("sub_department",m_branch);
            map.put("advertisement_title",m_title);
            map.put("advertisement_content",m_adsContent);
            map.put("advertisement_price",price);
            map.put("advertisement_type",ad_state);
            map.put("google_lat",String.valueOf(lat));
            map.put("google_long",String.valueOf(lng));
            map.put("city",m_address);
            map.put("phone",m_phone);
            map.put("show_phone",isAvailable_phone);
        Retrofit retrofit = Api.getRetrofit(Tags.Base_Url);
            Call<ResponseModel> call = retrofit.create(Services.class).Add_Ad(userModel.getUser_id(), map, encodeImages);
            call.enqueue(new Callback<ResponseModel>() {
                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                    if (response.isSuccessful())
                    {
                        if (response.body().getSuccess()==1)
                        {
                            dialog.dismiss();
                            Toast.makeText(Add_AdsActivity.this, R.string.ads_added, Toast.LENGTH_SHORT).show();
                            finish();
                        }else if (response.body().getSuccess()==0)
                        {
                            dialog.dismiss();
                            Toast.makeText(Add_AdsActivity.this, R.string.reg_error, Toast.LENGTH_SHORT).show();

                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(Add_AdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();

                }
            });

        }catch (NullPointerException e){}
        catch (Exception e){}

    }

    private List<String> EncodeImage(List<Bitmap> bitmaps)
    {
        List<String> encoded = new ArrayList<>();
        for (Bitmap bitmap :bitmaps)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,90,outputStream);
            byte[] bytes = outputStream.toByteArray();
            String img = Base64.encodeToString(bytes,Base64.DEFAULT);
            Log.e("image",img);
            encoded.add(img);
        }
        return encoded;
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        UpdateUi(userModel);
    }

    private void UpdateUi(UserModel userModel) {
        phone.setText(userModel.getUser_phone());
    }

    @Override
    public void onSuccess(List<DepartmentsModel> departmentsModelList) {
       try {
           this.departmentsModelList = departmentsModelList;

           //deptName.clear();
           deptList.clear();
           Spinner_DeptModel spinner_deptModel = new Spinner_DeptModel("",getString(R.string.dept));
           deptList.add(spinner_deptModel);

           //deptName.add(getString(R.string.dept));
           for (DepartmentsModel model :departmentsModelList)
           {
               Spinner_DeptModel spinner_deptModel2 = new Spinner_DeptModel(model.getMain_department_id(),model.getMain_department_name());
               deptList.add(spinner_deptModel2);
              // deptName.add(model.getMain_department_name());
           }

           deptAdapter.notifyDataSetChanged();

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
                                Toast.makeText(Add_AdsActivity.this,R.string.no_dept, Toast.LENGTH_LONG).show();

                            }else if (departmentsModelList.size()>0)
                            {

                                departmentSingletone.getDepartmentData(Add_AdsActivity.this);

                            }
                        }
                    },500);

                }
            }

            @Override
            public void onFailure(Call<List<DepartmentsModel>> call, Throwable t) {
                try {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(Add_AdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }catch (NullPointerException e)
                {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(Add_AdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                    Log.e("Error",t.getMessage());
                    Toast.makeText(Add_AdsActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
