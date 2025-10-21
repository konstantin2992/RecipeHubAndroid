package com.example.recipehub.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.recipehub.R;
import com.example.recipehub.api.ApiService;
import com.example.recipehub.api.RetrofitClient;
import com.example.recipehub.model.UpdateProfileRequest;
import com.example.recipehub.model.UpdateProfileResponse;
import com.example.recipehub.model.SimpleResponse;
import com.example.recipehub.model.User;
import com.example.recipehub.utils.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 101;

    private ImageView avatar;
    private EditText etFirst, etLast, etAbout;
    private Button btnSave;

    private SessionManager session;
    private ApiService apiService;
    private Uri pickedUri;
    private boolean avatarChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        setupUserData();
        setupClickListeners();
    }

    private void initializeViews() {
        avatar = findViewById(R.id.avatar);
        etFirst = findViewById(R.id.inputFirstName);
        etLast = findViewById(R.id.inputLastName);
        etAbout = findViewById(R.id.inputAbout);
        btnSave = findViewById(R.id.btnSave);

        session = new SessionManager(this);
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    private void setupUserData() {
        User u = session.getUser();
        etFirst.setText(u.getFirst_name());
        etLast.setText(u.getLast_name());
        etAbout.setText(u.getAbout_user());

        loadAvatar(u.getAvatar());
    }

    private void loadAvatar(String avatarUrl) {
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)

                    .into(avatar);
        } else {
            avatar.setImageResource(R.drawable.ic_person);
        }
    }

    private void setupClickListeners() {
        avatar.setOnClickListener(v -> checkPermissionAndPickImage());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ - используем READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_READ_STORAGE);
            }
        } else {
            // Android ниже 13 - используем READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_READ_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } catch (Exception e) {
            Log.e("ImagePicker", "Error opening image picker: " + e.getMessage());
            Toast.makeText(this, "Error opening image picker", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {
            pickedUri = data.getData();
            if (pickedUri != null) {
                try {
                    Glide.with(this)
                            .load(pickedUri)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .circleCrop()
                            .into(avatar);
                    avatarChanged = true;
                    Log.d("ImagePicker", "Image selected: " + pickedUri.toString());
                } catch (SecurityException e) {
                    Log.e("ImagePicker", "Security exception: " + e.getMessage());
                    Toast.makeText(this, "Cannot access the selected image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied. You can't select image without permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveProfile() {
        String first = etFirst.getText().toString().trim();
        String last = etLast.getText().toString().trim();
        String about = etAbout.getText().toString().trim();

        if (first.isEmpty() || last.isEmpty()) {
            Toast.makeText(this, "First name and last name are required", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Saving profile...");
        progress.setCancelable(false);
        progress.show();

        updateProfileData(first, last, about, progress);
    }

    private void updateProfileData(String first, String last, String about, ProgressDialog progress) {
        UpdateProfileRequest request = new UpdateProfileRequest(first, last, about);
        String token = "Bearer " + session.getToken();
        int userId = session.getUser().getUser_id();

        Log.d("ProfileUpdate", "Updating profile for user: " + userId);

        apiService.updateProfile(userId, token, request).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                if (response.isSuccessful()) {
                    Log.d("ProfileUpdate", "Profile update successful");

                    if (avatarChanged && pickedUri != null) {
                        Log.d("ProfileUpdate", "Starting avatar upload");
                        uploadAvatar(first, last, about, progress);
                    } else {
                        updateLocalUser(first, last, about,
                                session.getUser().getAvatar(),
                                session.getUser().getAvatar_public_id());
                        progress.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Profile saved!", Toast.LENGTH_SHORT).show();

                        setResult(RESULT_OK);
                        finish();
                    }
                } else {
                    progress.dismiss();
                    Log.e("ProfileUpdate", "Profile update failed: " + response.code());
                    handleProfileUpdateError(first, last, about, response);
                }
            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                progress.dismiss();
                Log.e("ProfileUpdate", "Profile update network error: " + t.getMessage(), t);
                handleProfileUpdateError(first, last, about, null);
            }
        });
    }

    private void handleProfileUpdateError(String first, String last, String about, Response<UpdateProfileResponse> response) {
        updateLocalUser(first, last, about,
                session.getUser().getAvatar(),
                session.getUser().getAvatar_public_id());

        if (response != null) {
            Toast.makeText(EditProfileActivity.this,
                    "Saved locally. Server error: " + response.code(),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(EditProfileActivity.this,
                    "Saved locally. Network error",
                    Toast.LENGTH_SHORT).show();
        }

        setResult(RESULT_OK);
        finish();
    }

    private void uploadAvatar(String first, String last, String about, ProgressDialog progress) {
        progress.setMessage("Uploading avatar...");

        try {
            if (pickedUri == null) {
                progress.dismiss();
                updateLocalUser(first, last, about, session.getUser().getAvatar(), session.getUser().getAvatar_public_id());
                Toast.makeText(this, "Profile saved, but no avatar selected", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
                return;
            }

            Log.d("AvatarUpload", "File URI: " + pickedUri.toString());

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedUri);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
            byte[] compressedBytes = byteArrayOutputStream.toByteArray();

            Log.d("AvatarUpload", "Compressed image size: " + compressedBytes.length + " bytes");

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "avatar_" + timeStamp + ".jpg";
            File tempFile = new File(getCacheDir(), fileName);

            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(compressedBytes);
            fos.flush();
            fos.close();

            Log.d("AvatarUpload", "Temp file created: " + tempFile.getAbsolutePath());

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", fileName, requestFile);

            String token = "Bearer " + session.getToken();
            int userId = session.getUser().getUser_id();

            Log.d("AvatarUpload", "Uploading compressed avatar for user: " + userId);

            apiService.updateAvatar(userId, token, avatarPart).enqueue(new Callback<SimpleResponse>() {
                @Override
                public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                    progress.dismiss();

                    if (response.isSuccessful() && response.body() != null) {
                        SimpleResponse avatarResponse = response.body();
                        String serverAvatarUrl = avatarResponse.getAvatar();

                        Log.d("AvatarUpload", "SUCCESS: Avatar uploaded: " + serverAvatarUrl);
                        updateLocalUser(first, last, about, serverAvatarUrl, session.getUser().getAvatar_public_id());
                        Toast.makeText(EditProfileActivity.this, "Profile and avatar saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("AvatarUpload", "Server error " + response.code() + ": " + response.message());
                        updateLocalUser(first, last, about, session.getUser().getAvatar(), session.getUser().getAvatar_public_id());
                        Toast.makeText(EditProfileActivity.this, "Profile saved, avatar failed: Server error " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(Call<SimpleResponse> call, Throwable t) {
                    progress.dismiss();
                    Log.e("AvatarUpload", "Network failure: " + t.getMessage());
                    updateLocalUser(first, last, about, session.getUser().getAvatar(), session.getUser().getAvatar_public_id());
                    Toast.makeText(EditProfileActivity.this, "Profile saved locally: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            });

        } catch (Exception e) {
            progress.dismiss();
            Log.e("AvatarUpload", "Upload exception: " + e.getMessage(), e);
            updateLocalUser(first, last, about, session.getUser().getAvatar(), session.getUser().getAvatar_public_id());
            Toast.makeText(this, "Error uploading avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    private void updateLocalUser(String first, String last, String about, String avatarUrl, String avatarPublicId) {
        User u = session.getUser();
        u.setFirst_name(first);
        u.setLast_name(last);
        u.setAbout_user(about);

        if (avatarUrl != null) {
            u.setAvatar(avatarUrl);
        }

        u.setAvatar_public_id(avatarPublicId);
        session.updateUser(u);

        Log.d("LocalUpdate", "User data updated locally");
    }
}