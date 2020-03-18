package com.misterymatch.app.webservice;

import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.chat.Fcm;
import com.misterymatch.app.model.AcceptMatchModel;
import com.misterymatch.app.model.CardList;
import com.misterymatch.app.model.FindMatch;
import com.misterymatch.app.model.FindMatchModel;
import com.misterymatch.app.model.Likes;
import com.misterymatch.app.model.MatchList;
import com.misterymatch.app.model.Message;
import com.misterymatch.app.model.Notifications;
import com.misterymatch.app.model.PremiumRes;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.Report;
import com.misterymatch.app.model.RequestMatchModel;
import com.misterymatch.app.model.SendPremiumRes;
import com.misterymatch.app.model.Setting;
import com.misterymatch.app.model.Token;
import com.misterymatch.app.model.User;
import com.misterymatch.app.model.Wallet;
import com.misterymatch.app.model.WalletHistory;
import com.misterymatch.app.model.WhoLikesMe;
import com.misterymatch.app.model.dto.response.ChangePasswordResponse;
import com.misterymatch.app.model.dto.response.ForgotPasswordResponse;
import com.misterymatch.app.twilio_video.TwilloVideoActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by santhosh@appoets.com on 23-11-2017.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/user/register")
    Call<Token> register(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("oauth/token")
    Call<Token> login(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/forgotPassword")
    Call<ForgotPasswordResponse> forgotPwd(@Field("email") String email);

    @FormUrlEncoded
    @POST("api/user/changePassword")
    Call<ChangePasswordResponse> changePwd(@Header("Authorization") String authorization,
                                           @FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/resetPassword")
    Call<Message> resetPassword(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/facebook")
    Call<Token> loginFacebook(@FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/linkedin")
    Call<Token> loginLinkedin(@FieldMap HashMap<String, String> params);

    @GET("api/user/get_profile")
    Call<Profile> getProfile(@Header("Authorization") String authorization,
                             @QueryMap() HashMap<String, Object> query);

    @Multipart
    @POST("api/user/update_profile")
    Call<Profile> updateProfile(@Header("Authorization") String authorization,
                                @PartMap() Map<String, RequestBody> partMap,
                                @Part MultipartBody.Part filename);

    @FormUrlEncoded
    @POST("api/user/update_profile")
    Call<Profile> updateUsername(@Header("Authorization") String authorization,
                                 @Field("username") String username);


    @GET("api/user/get_setting")
    Call<Setting> getSettings(@Header("Authorization") String authorization);

    @POST("api/user/update_setting")
    @FormUrlEncoded
    Call<Setting> updateSetting(@Header("Authorization") String authorization,
                                @FieldMap HashMap<String, Object> params);

    @GET("api/user/find_match")
    Call<FindMatch> findMatch(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/user/likes")
    Call<Likes> sendStatus(@Header("Authorization") String authorization,
                           @FieldMap HashMap<String, String> params);

    @GET("api/user/who_likes_me")
    Call<List<WhoLikesMe>> findWhoLikesMe(@Header("Authorization") String authorization);

    @GET("api/user/logout")
    Call<Message> logout(@Header("Authorization") String authorization);

    @GET("api/user/delete_account")
    Call<Message> deleteAccount(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/user/card")
    Call<Message> addCard(@Header("Authorization") String authorization,
                          @Field("stripe_token") String stripeToken);

    @GET("api/user/card_list")
    Call<CardList> getCards(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/user/add_money")
    Call<Wallet> addMoney(@Header("Authorization") String authorization, @FieldMap HashMap<String
            , String> params);

    @GET("api/user/match_list")
    Call<List<MatchList>> getMatchList(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/user/payment_process")
    Call<Wallet> paymentProcess(@Header("Authorization") String authorization,
                                @FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/card/destroy")
    Call<Message> deleteCard(@Header("Authorization") String authorization,
                             @FieldMap HashMap<String, String> params);

    @FormUrlEncoded
    @POST("api/user/transaction_history")
    Call<WalletHistory> TransactionHistory(@Header("Authorization") String authorization,
                                           @Field("status") String status);

    @FormUrlEncoded
    @POST("api/user/update_profile")
    Call<Profile> updateInterest(@Header("Authorization") String authorization,
                                 @FieldMap() Map<String, String> map);

    @GET("api/user/notifications")
    Call<Notifications> getNotification(@Header("Authorization") String authorization);

    @GET("api/user/premium")
    Call<PremiumRes> getPremium(@Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST("api/user/user_premium")
    Call<SendPremiumRes> addPremium(@Header("Authorization") String authorization, @Field(
            "premium_id") Integer premium_id);

    @FormUrlEncoded
    @POST("api/user/single_user")
    Call<User> getSingleUser(@Header("Authorization") String authorization,
                             @Field("user_id") Integer userId);

    @FormUrlEncoded
    @POST("api/user/user_report")
    Call<Report> reportUser(@Header("Authorization") String authorization,
                            @Field("report_id") Integer report_id,
                            @Field("reason") String reason);

    @Headers({
            "Content-Type: application/json",
            "Authorization: key=" + BuildConfig.FCM_API_TOKEN
    })
    @POST("fcm/send")
    Call<Object> sendFcm(@Body Fcm fcm);

    @GET("api/user/video/access/token")
    Call<TwilloVideoActivity.AccessToken> getTwilloVideoToken(
            @Header("Authorization") String authorization,
            @Query("room_id") Object obj, @Query("id") Object id);

    @POST("api/user/send/request")
    Call<FindMatchModel> findMatches(@Header("Authorization") String authorization);

    @GET("api/user/status/check")
    Call<RequestMatchModel> requestMatches(@Header("Authorization") String authorization);

    @POST("api/user/accept/request/{id}")
    Call<AcceptMatchModel> acceptMatchRequest(@Header("Authorization") String authorization,
                                              @Path("id") Integer requestId);

    @GET("api/user/cancel/request/{id}")
    Call<Object> cancelMatchRequest(@Header("Authorization") String authorization,
                                    @Path("id") Integer requestId);

    @FormUrlEncoded
    @POST("api/user/video/session/status")
    Call<Object> updateVideoCompletion(@Header("Authorization") String authorization,
                                       @Field("request_id") Integer requestId,
                                       @Field("video_status") Integer videoStatus);

    @FormUrlEncoded
    @POST("api/user/likes")
    Call<Likes> updateMatchInterest(@Header("Authorization") String authorization,
                                    @Field("request_id") Integer requestId,
                                    @Field("like_id") Integer userId,
                                    @Field("status") Integer interest);
}
