package com.example.recipehub.api;

import com.example.recipehub.model.AuthResponse;
import com.example.recipehub.model.AvatarResponse;
import com.example.recipehub.model.GoogleAuthRequest;
import com.example.recipehub.model.LoginRequest;
import com.example.recipehub.model.RegisterRequest;
import com.example.recipehub.model.SimpleResponse;
import com.example.recipehub.model.UpdateProfileRequest;
import com.example.recipehub.model.UpdateProfileResponse;
import com.example.recipehub.model.UserResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/google/mobile")
    Call<AuthResponse> googleAuthMobile(@Body GoogleAuthRequest request);

    @GET("user/{id}")
    Call<UserResponse> getUserProfile(
            @Header("Authorization") String token,
            @Path("id") int userId
    );
    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);
    @PUT("user/{id}")
    Call<UpdateProfileResponse> updateProfile(
            @Path("id") int userId,
            @Header("Authorization") String token,
            @Body UpdateProfileRequest request
    );

    @Multipart
    @PUT("user/{id}/avatar")
    Call<SimpleResponse> updateAvatar(
            @Path("id") int userId,
            @Header("Authorization") String token,
            @Part MultipartBody.Part avatar
    );
}