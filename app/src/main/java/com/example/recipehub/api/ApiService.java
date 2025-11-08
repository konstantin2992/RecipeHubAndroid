package com.example.recipehub.api;

import com.example.recipehub.model.AuthResponse;

import com.example.recipehub.model.FavoritesResponse;
import com.example.recipehub.model.GoogleAuthRequest;
import com.example.recipehub.model.LoginRequest;
import com.example.recipehub.model.MyRecipesResponse;
import com.example.recipehub.model.RecipeDetailResponse;
import com.example.recipehub.model.RegisterRequest;
import com.example.recipehub.model.SimpleResponse;
import com.example.recipehub.model.UpdateProfileRequest;
import com.example.recipehub.model.UpdateProfileResponse;
import com.example.recipehub.model.UserResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    // ======== RECIPE ENDPOINTS ========

    @Multipart
    @POST("recipes")
    Call<SimpleResponse> createRecipe(
            @Header("Authorization") String token,
            @Part MultipartBody.Part image,
            @Part List<MultipartBody.Part> stepImages,
            @Part("category_id") RequestBody categoryId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("difficulty") RequestBody difficulty,
            @Part("prep_time") RequestBody prepTime,
            @Part("serving") RequestBody serving,
            @Part("steps") RequestBody stepsJson,
            @Part("ingredients") RequestBody ingredientsJson
    );

    @Multipart
    @PUT("recipes/{id}")
    Call<SimpleResponse> updateRecipe(
            @Header("Authorization") String token,
            @Path("id") int recipeId,
            @Part MultipartBody.Part image,
            @Part List<MultipartBody.Part> stepImages,
            @Part("category_id") RequestBody categoryId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("difficulty") RequestBody difficulty,
            @Part("prep_time") RequestBody prepTime,
            @Part("serving") RequestBody serving,
            @Part("steps") RequestBody stepsJson,
            @Part("ingredients") RequestBody ingredientsJson
    );

    @GET("recipes/my")
    Call<MyRecipesResponse> getMyRecipes(@Header("Authorization") String token);

    @DELETE("recipes/{id}")
    Call<SimpleResponse> deleteRecipe(@Header("Authorization") String token, @Path("id") int recipeId);

    @GET("recipes/{id}")
    Call<RecipeDetailResponse> getRecipeById(@Header("Authorization") String token, @Path("id") int recipeId);

    @GET("favorites")
    Call<FavoritesResponse> getFavorites(@Header("Authorization") String token);

    @POST("favorites/{id}/add")
    Call<SimpleResponse> addFavorite(@Header("Authorization") String token, @Path("id") int recipeId);

    @DELETE("favorites/{id}/delete")
    Call<SimpleResponse> deleteFavorite(@Header("Authorization") String token, @Path("id") int recipeId);
}