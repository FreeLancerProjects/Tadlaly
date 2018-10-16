package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.semicolon.tadlaly.Adapters.SearchAdsAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener ,UserSingleTone.OnCompleteListener{
    private ImageView back;
    private ProgressBar progressBar;
    private AutoCompleteTextView searchView;
    private TextView no_search_result;
    private LinearLayout no_result_container;
    private String user_type = "";
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double myLat = 0.0, myLng = 0.0;
    private String user_id="all";
    private UserModel userModel;
    private UserSingleTone userSingleTone;
    private int page_index=2;
    private List<MyAdsModel>myAdsModelList;
    private RecyclerView recView;
    private RecyclerView.LayoutManager manager;
    private SearchAdsAdapter searchAdsAdapter;
    private String query="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        myAdsModelList = new ArrayList<>();
        back = findViewById(R.id.back);
        progressBar = findViewById(R.id.progBar);
        searchView = findViewById(R.id.searchView);
        no_result_container = findViewById(R.id.no_result_container);
        no_search_result = findViewById(R.id.no_search_result);
        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        searchAdsAdapter = new SearchAdsAdapter(recView,this,myAdsModelList);
        recView.setAdapter(searchAdsAdapter);
        searchView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (EditorInfo.IME_ACTION_SEARCH == i
                    || KeyEvent.ACTION_DOWN == keyEvent.getAction()
                    || KeyEvent.KEYCODE_ENTER == keyEvent.getAction()
                    )

            {
                HideKeyBoard();
                query = searchView.getText().toString();
                Search(query,1);
            }
            return false;
        });
        back.setOnClickListener(view -> finish());

        getDataFromIntent();
        initGoogleApiClient();
        initOnLoadMore();
    }
    private void initOnLoadMore() {
        searchAdsAdapter.setLoadListener(new SearchAdsAdapter.OnLoadListener() {
            @Override
            public void onLoadMore() {
                if (myAdsModelList.size()>0)
                {
                    myAdsModelList.add(null);
                    searchAdsAdapter.notifyItemInserted(myAdsModelList.size()-1);
                    page_index++;
                    onLoadSearch(query,page_index);
                }

            }
        });

    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            user_type = intent.getStringExtra("user_type");
            if (user_type.equals(Tags.app_user))
            {
                userSingleTone = UserSingleTone.getInstance();
                userSingleTone.getUser(this);
            }else if (user_type.equals(Tags.app_visitor))
            {
                user_id ="all";
            }
            Log.e("type", user_type);
        }
    }

    private void initGoogleApiClient() {
        if (googleApiClient==null)
        {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addOnConnectionFailedListener(this)
                    .addConnectionCallbacks(this)
                    .build();
        }

        googleApiClient.connect();
    }

    private void Search(String query,int page_index) {

        if (!TextUtils.isEmpty(query)) {
            Log.e("q",query);
            Log.e("ind",page_index+"");
            myAdsModelList.clear();
            progressBar.setVisibility(View.VISIBLE);

            Api.getRetrofit(Tags.Base_Url)
                    .create(Services.class)
                    .searchby_name(page_index,user_id,query,myLat,myLng)
                    .enqueue(new Callback<List<MyAdsModel>>() {
                        @Override
                        public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                            if (response.isSuccessful())
                            {
                                progressBar.setVisibility(View.GONE);
                                if (response.body().size()>0)
                                {

                                    myAdsModelList.addAll(response.body());
                                    searchAdsAdapter.notifyDataSetChanged();
                                    no_result_container.setVisibility(View.GONE);
                                    no_search_result.setVisibility(View.VISIBLE);


                                }else
                                    {
                                        no_result_container.setVisibility(View.VISIBLE);
                                        no_search_result.setVisibility(View.VISIBLE);

                                    }
                            }
                        }

                        @Override
                        public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                            Log.e("Error",t.getMessage());
                        }
                    });
        }else
            {
                HideKeyBoard();
                Toast.makeText(this, R.string.enter_proname, Toast.LENGTH_LONG).show();
            }
    }
    private void onLoadSearch(String query,int page_index)
    {
        Api.getRetrofit(Tags.Base_Url)
                .create(Services.class)
                .searchby_name(page_index,user_id,query,myLat,myLng)
                .enqueue(new Callback<List<MyAdsModel>>() {
                    @Override
                    public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                        if (response.isSuccessful()) {

                            if (response.body().size()>0)
                            {
                                int lastPos = myAdsModelList.size()-1;
                                SearchActivity.this.page_index =SearchActivity.this.page_index+1;
                                myAdsModelList.remove(myAdsModelList.size() - 1);
                                searchAdsAdapter.notifyItemRemoved(myAdsModelList.size());
                                searchAdsAdapter.setLoaded();
                                myAdsModelList.addAll(response.body());
                                searchAdsAdapter.notifyItemRangeInserted(lastPos,myAdsModelList.size()-1);
                            }else
                                {
                                    myAdsModelList.remove(myAdsModelList.size() - 1);
                                    searchAdsAdapter.notifyItemRemoved(myAdsModelList.size());
                                    searchAdsAdapter.setLoaded();
                                }


                        }
                    }

                    @Override
                    public void onFailure(Call<List<MyAdsModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                    }
                });

       /* if (myAdsModelList.size()>0)
        {
            myAdsModelList.remove(myAdsModelList.size()-1);
            searchAdsAdapter.notifyItemRemoved(myAdsModelList.size());
            searchAdsAdapter.setLoaded();
        }
        if (myAdsModelList.size()==0)
        {

        }else
        {
            no_result_container.setVisibility(View.GONE);
            no_search_result.setVisibility(View.GONE);
            myAdsModelList.remove(myAdsModelList.size()-1);
            searchAdsAdapter.notifyItemRemoved(myAdsModelList.size());
            searchAdsAdapter.setLoaded();


        }*/
    }
    private void HideKeyBoard()
    {
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(searchView.getWindowToken(),0);
    }
    public void SetMyadsData(MyAdsModel myAdsModel)
    {
        try {
            Intent intent = new Intent(this,AdsDetailsActivity.class);
            intent.putExtra("ad_details",myAdsModel);
            intent.putExtra("whoVisit",Tags.visitor);
            startActivity(intent);
        }catch (NullPointerException e)
        {

        }catch (Exception e){}

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startlocationUpdate();
    }

    private void startlocationUpdate() {
        initlocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location!=null)
        {
            myLat = location.getLatitude();
            myLng = location.getLongitude();
            Log.e("lng2",myLng+"");
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);
    }

    private void initlocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (googleApiClient!=null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location!=null)
        {
            myLat = location.getLatitude();
            myLng = location.getLongitude();
            Log.e("lat2",myLat+"");
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (googleApiClient!=null&&googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onSuccess(UserModel userModel) {
        this.userModel = userModel;
        this.user_id = this.userModel.getUser_id();
    }
}
