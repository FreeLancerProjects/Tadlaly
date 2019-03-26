package com.semicolon.tadlaly.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.semicolon.tadlaly.Adapters.SearchAdsAdapter;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.UserModel;
import com.semicolon.tadlaly.R;
import com.semicolon.tadlaly.Services.Api;
import com.semicolon.tadlaly.Services.Preferences;
import com.semicolon.tadlaly.Services.Services;
import com.semicolon.tadlaly.Services.Tags;
import com.semicolon.tadlaly.SingleTone.UserSingleTone;
import com.semicolon.tadlaly.language.LocalManager;
import com.semicolon.tadlaly.share.Common;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener ,UserSingleTone.OnCompleteListener{
    private ImageView back;
    private TextView no_search_result;
    private LinearLayout no_result_container;
    private ProgressBar progBar;
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
    private Toolbar toolbar;
    private MaterialSearchView search_view;
    private Preferences preferences;
    private String [] suggestions;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);

        super.attachBaseContext(LocalManager.updateResources(newBase,LocalManager.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        preferences = new Preferences(this);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        progBar = findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        myAdsModelList = new ArrayList<>();
        back = findViewById(R.id.back);
        search_view = findViewById(R.id.msv);
        no_result_container = findViewById(R.id.no_result_container);
        no_search_result = findViewById(R.id.no_search_result);
        recView = findViewById(R.id.recView);
        manager = new LinearLayoutManager(this);
        recView.setLayoutManager(manager);
        searchAdsAdapter = new SearchAdsAdapter(recView,this,myAdsModelList);
        recView.setAdapter(searchAdsAdapter);
        back.setOnClickListener(view -> finish());
        search_view.showSuggestions();
        suggestions = preferences.getSuggestionList();

        if (suggestions.length>0)
        {
            search_view.setSuggestions(suggestions);

        }
        search_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("clllllecek","ccccccccccssssssssss");
                String suggestion = suggestions[position];
                preferences.setSuggestions(suggestion);
                Search(suggestion,0);
            }
        });


        search_view.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                preferences.setSuggestions(query);
                Common.CloseKeyBoard(SearchActivity.this,search_view);
                search_view.closeSearch();
                Search(query,0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search_view.setSuggestions(preferences.getSuggestionList());

                return false;
            }
        });
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
            progBar.setVisibility(View.VISIBLE);
            Api.getRetrofit(Tags.Base_Url)
                    .create(Services.class)
                    .searchby_name(page_index,user_id,query,myLat,myLng)
                    .enqueue(new Callback<List<MyAdsModel>>() {
                        @Override
                        public void onResponse(Call<List<MyAdsModel>> call, Response<List<MyAdsModel>> response) {
                            if (response.isSuccessful())
                            {
                                progBar.setVisibility(View.GONE);

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
                            progBar.setVisibility(View.GONE);
                            Toast.makeText(SearchActivity.this,R.string.something, Toast.LENGTH_SHORT).show();
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
        Common.CloseKeyBoard(this,search_view);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_search,menu);
        MenuItem menuItem = menu.findItem(R.id.item);
        search_view.setMenuItem(menuItem);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (search_view.isSearchOpen())
        {
            search_view.closeSearch();
        }else
            {
                super.onBackPressed();

            }
    }
}
