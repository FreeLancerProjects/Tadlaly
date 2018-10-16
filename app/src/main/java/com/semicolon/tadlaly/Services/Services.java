package com.semicolon.tadlaly.Services;

import com.semicolon.tadlaly.Models.AboutAppModel;
import com.semicolon.tadlaly.Models.BankModel;
import com.semicolon.tadlaly.Models.ContactsModel;
import com.semicolon.tadlaly.Models.DepartmentsModel;
import com.semicolon.tadlaly.Models.MyAdsModel;
import com.semicolon.tadlaly.Models.MyLocation;
import com.semicolon.tadlaly.Models.PeopleMsgModel;
import com.semicolon.tadlaly.Models.ResponseModel;
import com.semicolon.tadlaly.Models.RulesModel;
import com.semicolon.tadlaly.Models.SlideShowModel;
import com.semicolon.tadlaly.Models.UserModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface Services {

    @FormUrlEncoded
    @POST("Api/Registration")
    Call<UserModel> Register(@FieldMap Map<String,String> map);

    @FormUrlEncoded
    @POST("Api/Login")
    Call<UserModel> Login(@Field("user_name") String username,@Field("user_pass") String pass);

    @GET("Api/Logout/{id}")
    Call<ResponseModel>Logout(@Path("id")String user_id);

    @GET("Api/ContactUs")
    Call<ContactsModel> getContacts();

    @FormUrlEncoded
    @POST("Api/ContactUs")
    Call<ResponseModel> ContactUs(@FieldMap Map<String,String> map);

    @FormUrlEncoded
    @POST("Api/RestMyPass")
    Call<ResponseModel> ResetPassword(@FieldMap Map<String,String> map);

    @FormUrlEncoded
    @POST("Api/UpdateProfile/{user_id}")
    Call<UserModel> updatePhoto(@Path("user_id") String user_id,@Field("user_photo")String photo);

    @FormUrlEncoded
    @POST("Api/UpdateProfile/{user_id}")
    Call<UserModel> updateName(@Path("user_id") String user_id,@Field("user_full_name")String name);


    @FormUrlEncoded
    @POST("Api/UpdateProfile/{user_id}")
    Call<UserModel> updatePhone(@Path("user_id") String user_id,@Field("user_phone")String user_phone);

    @FormUrlEncoded
    @POST("Api/UpdateProfile/{user_id}")
    Call<UserModel> updateEmail(@Path("user_id") String user_id,@Field("user_email")String user_email);

    @FormUrlEncoded
    @POST("Api/UpdateProfile/{user_id}")
    Call<UserModel> updateCity(@Path("user_id") String user_id,@Field("user_city")String user_city);

    @FormUrlEncoded
    @POST("Api/UpdateProfile/{user_id}")
    Call<UserModel> updateUser_name(@Path("user_id") String user_id,@Field("user_name")String user_name);

    @FormUrlEncoded
    @POST("Api/UpdatePass/{user_id}")
    Call<UserModel> updatePassword(@Path("user_id") String user_id,@Field("user_old_pass")String user_old_pass,@Field("user_new_pass")String user_new_pass);

    @GET("Api/SlidShow")
    Call<List<SlideShowModel>> getSlideShowData();

    @GET("Api/ShowDepartment")
    Call<List<DepartmentsModel>> getDepartmentData();

    @GET("Api/AboutUs")
    Call<AboutAppModel> AboutApp();

    @FormUrlEncoded
    @POST("Api/AddMyAdvertisement/{user_id}")
    Call<ResponseModel> Add_Ad(@Path("user_id") String user_id,@FieldMap Map<String,String> map,@Field("images[]") List<String> encodedImages);

    @GET("Api/CurrentAdvertisement/{user_id}/{page_index}")
    Call<List<MyAdsModel>> getCurrentAds(@Path("user_id") String user_id,@Path("page_index") int page_index);

    @GET("Api/OldAdvertisement/{user_id}/{page_index}")
    Call<List<MyAdsModel>> getPreviousAds(@Path("user_id") String user_id,@Path("page_index") int page_index);

    @GET()
    Call<MyLocation> getMyLocationAddress(@Url String url);

    @FormUrlEncoded
    @POST("Api/UpdateAdvertisement/{advertisement_id}")
    Call<MyAdsModel> updateMyAds(@Path("advertisement_id") String advertisement_id,@FieldMap Map<String,String> map,@Field("images[]") List<String> imgs);
    /*@GET()
    Observable<PlacesDistanceModel> getDistance(@Url String url);*/

    @GET("Api/AllAdvertisement/{display}/{user_id}/{sub_department_id}/{page_index}")
    Call<List<MyAdsModel>> getSubDept_Ads(@Path("display")String display,@Path("user_id")String user_id,@Path("sub_department_id")String sub_department_id,@Path("page_index")int page_index);

    @FormUrlEncoded
    @POST("Api/SearchFilter/{display}/{user_id}/{page_index}")
    Call<List<MyAdsModel>> Search(@Path("display")String display,@Path("user_id")String user_id,@Path("page_index")int page_index,@Field("main_department")String main_department,@Field("sub_department")String sub_department);

    @GET("Api/AppDetails/3")
    Call<RulesModel> getRules();

    @FormUrlEncoded
    @POST("Api/UpdateLocation/{user_id}")
    Call<UserModel> updateLocation(@Path("user_id")String user_id,@Field("user_google_lat")String user_google_lat,@Field("user_google_long")String user_google_long);

    @FormUrlEncoded
    @POST("Api/UpdateTokenId/{user_id}")
    Call<UserModel> UpdateToken(@Path("user_id") String user_id,@Field("user_token_id") String user_token_id);

    @FormUrlEncoded
    @POST("Api/CountShare/{id_advertisement}")
    Call<MyAdsModel> IncreaseShare_Viewers(@Path("id_advertisement") String id_advertisement ,@Field("count") String count);

    @GET("Api/BankAccounts")
    Call<List<BankModel>> getBanks();

    @FormUrlEncoded
    @POST("Api/DeleteAdvertisement")
    Call<ResponseModel> deleteAds(@Field("reason") String reason,@Field("ids_advertisement[]") List<String> ads_ids);

    @FormUrlEncoded
    @POST("Api/Payment/{user_id}")
    Call<ResponseModel> transMoney(@Path("user_id")String user_id,
                                   @Field("user_name")String user_name,
                                   @Field("amount")String amount,
                                   @Field("bank")String bank,
                                   @Field("date")String date,
                                   @Field("transform_person")String transform_person,
                                   @Field("transform_image")String transform_image,
                                   @Field("advertisement_code")String advertisement_code);
    @GET("Api/LastMessage/{user_id}")
    Call<List<PeopleMsgModel>> getAllPeople_chat(@Path("user_id") String user_id);

    @GET("Api/BetweenMessage/{user_id}/{chat_user_id}")
    Call<List<PeopleMsgModel>> getAllMsg(@Path("user_id")String user_id,@Path("chat_user_id")String chat_user_id);

    @FormUrlEncoded
    @POST("Api/SendMessage/{user_id}")
    Call<ResponseModel> sendMsg(@Path("user_id")String user_id,
                                @Field("to_user_id")String to_user_id,
                                @Field("message_content")String message_content);
    @GET("Api/DeleteBetweenMessages/{user_id}/{chat_user_id}")
    Call<ResponseModel> DeleteAllMsg(@Path("user_id")String user_id,@Path("chat_user_id")String chat_user_id);

    @FormUrlEncoded
    @POST("Api/DeleteMessages")
    Call<ResponseModel> deleteSingleMsg(@Field("ids_message[]")List<String> msg_ids);

    @GET("Api/Advertisements/{display}/{user_id}/{page_index}")
    Call<List<MyAdsModel>> getAllAppAds(@Path("display")String display,@Path("user_id")String user_id,@Path("page_index")int page_index);

    @FormUrlEncoded
    @POST("Api/DepAdvertisement/{display}/{sub_department_id}/{page_index}")
    Call<List<MyAdsModel>> visitor_getSubDeptAds(@Path("display") String display,@Path("sub_department_id") String sub_department_id,@Path("page_index") int page_index ,@Field("user_google_lat") String user_google_lat,@Field("user_google_long") String user_google_long);

    @FormUrlEncoded
    @POST("Api/OurAdvertisements/{display}/{page_index}")
    Call<List<MyAdsModel>> visitor_getAllAppAds(@Path("display") String display,@Path("page_index") int page_index ,@Field("user_google_lat") String user_google_lat,@Field("user_google_long") String user_google_long);

    @FormUrlEncoded
    @POST("Api/OurAdvertisements/{display}/{page_index}")
    Call<List<MyAdsModel>> visitor_Search(@Path("display") String display,@Path("page_index") int page_index ,@Field("main_department") String main_department_id,@Field("sub_department") String sub_department_id,@Field("user_google_lat") String user_google_lat,@Field("user_google_long") String user_google_long);

    @FormUrlEncoded
    @POST("Api/FindAdvertisement/{page_index}")
    Call<List<MyAdsModel>> searchby_name(@Path("page_index") int page_index,@Field("user_id") String user_id,@Field("search_title") String search_title,@Field("user_google_lat") double user_google_lat,@Field("user_google_long") double user_google_long);
}

