/*
 * Created by Tareq Islam on 8/16/18 2:47 AM
 *
 *  Last modified 8/16/18 2:37 AM
 */

package com.mti.pushnotifier.api;



import com.mti.pushnotifier.model.NotificationRegistration;
import com.mti.pushnotifier.model.DeviceRegistrationModel;
import com.mti.pushnotifier.model.UserModel;
import com.mti.pushnotifier.model.UserRegisterModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserApiServices {


    @FormUrlEncoded
    @POST("RegisterUser.php")
    Call<UserRegisterModel> userRegisterResponse(@Field("u_name") String userName, @Field("u_email") String userEmail, @Field("u_phone") String userPhone);


    @FormUrlEncoded
    @POST("RegisterDevice.php")
    Call<DeviceRegistrationModel> deviceRegisterResponse(@Field("d_unique_id") String email, @Field("token") String token, @Field("u_id") int u_id);


    @FormUrlEncoded
    @POST("SetNotification.php")
    Call<NotificationRegistration> setNotificationResponse(@Field("title") String title, @Field("message") String message, @Field("calender") String calender, @Field("email") String email);


    @GET("GetUser.php")
    Call<UserModel> getUserResponse(@Query("u_email") String userEmail);



    //region Hints for retrofit calls
/*    So, using this route the retrofit will generate the following URL:
    http://api.themoviedb.org/3/movie/top_rated?api_key=12345678910111213

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    //for variable in path
      @GET("movie/{id}")
    Call<MoviesResponse> getMovieDetails(@Path("id") int id, @Query("api_key") String apiKey);


    HERE are some example of annotation in retrofit2:
      Take a look to other annotations:

@Path – variable substitution for the API endpoint. For example movie id will be swapped for{id} in the URL endpoint.

@Query – specifies the query key name with the value of the annotated parameter.

@Body – payload for the POST call

@Header – specifies the header with the value of the annotated parameter
    */
    //endregion

}