package com.mobitv.ott.pojo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("/apiv2/channels/get_list")
    Call<ListChannelCategoryResponse> doGetLiveTvList();

    @GET("/apiv2/channels/daily_epg")
    Call<DailyEPGResponse> doGetDailyEPG(@Query("channel_number") int channelNumber,
                                         @Query("device_timezone") int deviceTimeZone,
                                         @Query("day") int day);

    @GET("/apiv2/channels/get_detail/{channel_id}")
    Call<LiveTvChannelDetailsResponse> doGetChannelDetails(@Path("channel_id") int channelNumber,
                                                           @Query("device_timezone") int deviceTimeZone,
                                                           @Query("day") int day);

    //list in home page
    @GET("/apiv2/vod/films")
    Call<VodCategoryResponse> doGetMoviesList();

    @GET("/apiv2/vod/tvshows_season")
    Call<VodCategoryResponse> doGetTvShowsList();

    @GET("/apiv2/vod/vodbycategory")
    Call<VodResponse> doGetListVodByCategory(@Query("cateid") String categoryID,
                                             @Query("page") int page,
                                             @Query("limit") int limit);
    //hienvd: Debug 23062020
    @GET("/apiv2/vod/getvoddetail/{id}")
    Call<VodDetailsResponse> doGetVodDetails(@Path("id") int vodID);
    //hienvd: Debug 23062020
    @GET("/apiv2/vod/tvshow_details_byseasons/{id}")
    Call<TvSeasonDetailsResponse> doGetTvSeasonDetails(@Path("id") int vodID);


    @GET("/apiv2/settings/basic_settings")
    Call<BasicSettingsResponse> doGetBasicSettings();

    @POST("/api/app/v1/AppGetConfig")
    Call<AccessResponse> checkAccess(@Body AccessParams accessParams);

    @POST("/apiv2/auth/credentials/register")
    Call<RegisterResponse> doRegister(@Body RegisterParams registerParams);

    @POST("/apiv2/auth/otp/confirm")
    Call<ConfirmOtpSignUpResponse> doConfirmOTPSingUp(@Body ConfirmOtpSignUpParams params);

    @POST("/apiv2/auth/otp/resent")
    Call<ResendOtpSignUpResponse> doResendOTPSignUp(@Body ResendOTPSignUpParams params);

    @POST("/apiv2/auth/credentials/login")
    Call<LogInResponse> doLogIn(@Body LogInParams params);

    @POST("/apiv2/auth/credentials/logout")
    Call<LogOutResponse> doLogOut();

    @POST("/apiv2/auth/updatePass/confirm")
    Call<ConfirmOtpNewPassResponse> doConfirmOTPNewPass(@Body ConfirmOtpNewPassParams params);

    @POST("/apiv2/auth/updatePass/requestPassword")
    Call<ResendOtpNewPassResponse> doRequestOTPNewPass(@Body ResendOTPNewPassParams params);

    @POST("/apiv2/auth/updatePass/resendOTP")
    Call<ResendOtpNewPassResponse> doResendOTPNewPass(@Body ResendOTPNewPassParams params);

    @POST("/apiv2/auth/updatePass/setPass")
    Call<NewPassResponse> doUpdatePassword(@Body NewPassParams params);

    @GET("/apiv2/vod/increase_click/{vodID}")
    Call<UpdateClickResponse> increaseClick(@Path("vodID") int vodID);

    //hienvd: search trending
    @GET("/apiv2/search/trendingSuggest")
    Call<SearchSuggestionResponse> doGetTrendingSearch(@Query("page") int page,
                                                       @Query("limit") int limit);

    //hienvd: Search suggestion
    @GET("/apiv2/search/suggestion")
    Call<SearchSuggestionResponse> doGetSuggestionSearch(@Query("search_string") String keyword,
                                                         @Query("page") int page,
                                                         @Query("limit") int limit);

    //hienvdL search du lieu get
    @GET("/apiv2/search/get")
    Call<SearchResultRespone> doGetSearchResult(@Query("search_string") String keyword,
                                                @Query("page") int page,
                                                @Query("limit") int limit);

    @GET("/apiv2/vod/getvodcomment/{vodid}")
    Call<CommentResponse> doGetAllComment(@Path("vodid") int vodID,
                                          @Query("page") int page,
                                          @Query("limit") int limit);

    @POST("/apiv2/vod/vodcomment")
    Call<SendCommentResponse> doUpComment(@Body CommentParams params);
    //hienvd: 20072020 danh sach da xem
    @POST("/apiv2/vod/list_byid")
    Call<VodCategoryResponse> doGetListVodWatched(@Body ListVodIdParams params);
    //hienvd: End 20072020
    @GET("/apiv2/vod/favorites")
    Call<VodCategoryResponse> doGetListVodFavourite();

    @POST("/apiv2/vod/favorites")
    Call<CommonResponse> doAddFavourite(@Body VodFavouriteParams params);

    @POST("/apiv2/vod/removefavorites/{vodid}")
    Call<CommonResponse> doRemoveFavourite(@Path("vodid") int vodID);

    @GET("/apiv2/auth/scard_register/{tel}/{scardNum}")
    Call<ScardRegisterResponse> doScardRegister(@Path("tel") String tel,@Path("scardNum") String scardNum);

    @GET("/apiv2/auth/update_scard/{tel}/{scardNum}/{scardNumOld}")
    Call<ScardRegisterResponse> updateScardNumber(@Path("tel") String tel,@Path("scardNum") String scardNum, @Path("scardNumOld") String scardNumOld);

    @GET("/apiv2/auth/update_scard_login/{scardNum}/{scardExpired}/{scardStatus}")
    Call<ScardRegisterResponse> updateScardLogin(@Path("scardNum") String scardNum, @Path("scardExpired") String scardExpired, @Path("scardStatus") String scardStatus);

    @GET("/apiv2/auth/get_scard_number")
    Call<ScardRegisterResponse> getScardNumber(@Query("scard_number") String scard_number,
                                                 @Query("user_id") String user_id,
                                                    @Query("phone") String phone);

    @GET("/apiv2/auth/delete_scard_number")
    Call<ScardRegisterResponse> deleteScardNumber(@Query("scard_number") String scard_number);
}
