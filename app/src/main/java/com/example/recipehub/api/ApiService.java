package com.example.recipehub.api;

import com.example.recipehub.model.AuthResponse;

import com.example.recipehub.model.CategoryResponse;
import com.example.recipehub.model.DirectRecipeResponse;
import com.example.recipehub.model.FavoritesResponse;
import com.example.recipehub.model.GoogleAuthRequest;
import com.example.recipehub.model.LoginRequest;
import com.example.recipehub.model.MyRecipesResponse;
import com.example.recipehub.model.RecipeDetailResponse;
import com.example.recipehub.model.RegisterRequest;
import com.example.recipehub.model.SearchRecipesResponse;
import com.example.recipehub.model.SimpleResponse;
import com.example.recipehub.model.UpdateProfileRequest;
import com.example.recipehub.model.UpdateProfileResponse;
import com.example.recipehub.model.UserResponse;

import java.util.List;
import java.util.Map;

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
import retrofit2.http.QueryMap;

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
    @GET("categories")
    Call<CategoryResponse> getCategories();
    @Multipart
    @POST("recipes") // Убедитесь что это правильный endpoint
    Call<SimpleResponse> createRecipe(
            @Header("Authorization") String authorization,
            @Part MultipartBody.Part image,
            @Part("category_id") RequestBody categoryId,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("difficulty") RequestBody difficulty,
            @Part("prep_time") RequestBody prepTime,
            @Part("serving") RequestBody serving,
            @Part("steps") RequestBody steps,
            @Part("ingredients") RequestBody ingredients
    );

    @GET("recipes/search")
    Call<SearchRecipesResponse> searchRecipes(@QueryMap Map<String, String> params);
    @GET("recipes/{id}")
    Call<DirectRecipeResponse> getRecipeDetails(@Path("id") int recipeId);
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

    @GET("/api/favorites")
    Call<FavoritesResponse> getFavorites(@Header("Authorization") String token);

    @POST("/api/favorites/{recipeId}/add")
    Call<SimpleResponse> addToFavorites(
            @Header("Authorization") String token,
            @Path("recipeId") int recipeId
    );

    @DELETE("/api/favorites/{recipeId}/delete")
    Call<SimpleResponse> removeFromFavorites(
            @Header("Authorization") String token,
            @Path("recipeId") int recipeId
    );

    // Метод для проверки избранного - будем использовать getFavorites и фильтровать
    @GET("/api/favorites")
    Call<FavoritesResponse> checkIfFavorite(
            @Header("Authorization") String token
    );

    @GET("recipes/my")
    Call<SearchRecipesResponse> getMyRecipes(@Header("Authorization") String token);

    @DELETE("recipes/{id}")
    Call<SimpleResponse> deleteRecipe(@Header("Authorization") String token, @Path("id") int recipeId);

    @GET("recipes/{id}")
    Call<RecipeDetailResponse> getRecipeById(@Header("Authorization") String token, @Path("id") int recipeId);



    @GET("recipes/latest")
    Call<SearchRecipesResponse> getLatestRecipes();

    @POST("favorites/{id}/add")
    Call<SimpleResponse> addFavorite(@Header("Authorization") String token, @Path("id") int recipeId);

    @DELETE("favorites/{id}/delete")
    Call<SimpleResponse> deleteFavorite(@Header("Authorization") String token, @Path("id") int recipeId);
}