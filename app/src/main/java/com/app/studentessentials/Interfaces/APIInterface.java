package com.app.studentessentials.Interfaces;

import com.app.studentessentials.Gsons.ForgotGson;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIInterface
{
    @FormUrlEncoded
    @POST("mail.php/")
    Call<ForgotGson> sendMail(@Field("mail") String mail , @Field("otp") String otp);
}
