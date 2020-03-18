package com.misterymatch.app.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.HomeActivity;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.User;
import com.misterymatch.app.model.UserImage;
import com.misterymatch.app.model.UserInterest;
import com.misterymatch.app.utils.CodeSnippet;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.misterymatch.app.utils.GlobalData.api;

public class EditProfileActivity extends AppCompatActivity {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 101;
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.show_age)
    Switch showAge;
    @BindView(R.id.show_distance)
    Switch showDistance;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.about)
    EditText about;
    @BindView(R.id.male)
    RadioButton male;
    @BindView(R.id.female)
    RadioButton female;
    @BindView(R.id.work)
    EditText work;
    CustomDialog customDialog;
    CodeSnippet codeSnippet;
    @BindView(R.id.picture)
    ImageView picture;
    @BindView(R.id.image1)
    ImageView image1;
    @BindView(R.id.image2)
    ImageView image2;
    @BindView(R.id.image5)
    ImageView image5;
    @BindView(R.id.image4)
    ImageView image4;
    @BindView(R.id.image3)
    ImageView image3;
    @BindView(R.id.bio_video)
    Button bioVideo;
    @BindView(R.id.interests)
    TextView interests;
    File imgFile;
    ImageView UploadImageView;
    @BindView(R.id.video_player)
    JZVideoPlayerStandard videoPlayer;
    private int PICK_IMAGE_REQUEST = 1;
    private int PICK_VIDEO_REQUEST = 2;
    private int CHOOSE_INTEREST_REQUEST = 3;

    public static Uri handleVideoUri(Uri uri) {
        if (uri.getPath().contains("content")) {
            Pattern pattern = Pattern.compile("(content://media/.*\\d)");
            Matcher matcher = pattern.matcher(uri.getPath());
            if (matcher.find())
                return Uri.parse(matcher.group(1));
            else
                throw new IllegalArgumentException("Cannot handle this URI");
        }
        return uri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        HomeActivity.isgetFindMatch = true;
        customDialog = new CustomDialog(this);
        codeSnippet = new CodeSnippet(this);

        title.setText(getString(R.string.edit_profile));
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        if (GlobalData.PROFILE != null) {
            User user = GlobalData.PROFILE.getUser();
            if (user != null) {
                about.setText(user.getAbout());
                work.setText(user.getWork());
                Glide.with(getApplicationContext()).load(user.getPicture()).apply(new RequestOptions().centerCrop().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(picture);
                if (user.getGender() != null) {
                    if (user.getGender().equals("male")) {
                        male.setChecked(true);
                    } else if (user.getGender().equals("female")) {
                        female.setChecked(true);
                    }
                }

                for (UserImage image : user.getUserImages()) {
                    if (image.getStatus().equals(1)) {
                        Glide.with(getApplicationContext()).load(image.getImage()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user)).into(image1);
                    } else if (image.getStatus().equals(2)) {
                        Glide.with(getApplicationContext()).load(image.getImage()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user)).into(image2);
                    } else if (image.getStatus().equals(3)) {
                        Glide.with(getApplicationContext()).load(image.getImage()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user)).into(image3);
                    } else if (image.getStatus().equals(4)) {
                        Glide.with(getApplicationContext()).load(image.getImage()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user)).into(image4);
                    } else if (image.getStatus().equals(5)) {
                        Glide.with(getApplicationContext()).load(image.getImage()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).centerCrop().dontAnimate()).into(image5);
                    }
                }

                if (user.getShowAge().equals(1)) {
                    showAge.setChecked(true);
                }
                if (user.getShowDistance().equals(1)) {
                    showDistance.setChecked(true);
                }

                if (user.getBioVideo() != null) {
                    videoPlayer.setUp(user.getBioVideo(), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL);
                } else {
                    videoPlayer.setVisibility(View.GONE);
                }

                StringBuilder interestStr = new StringBuilder();
                for (UserInterest obj : user.getUserInterest()) {
                    interestStr.append(obj.getInterest().getName()).append(", ");
                }
                interests.setText(interestStr);
            }


        } else {
            getProfile();
        }
    }

    @OnClick({R.id.back, R.id.save, R.id.picture, R.id.image1, R.id.image2, R.id.image5, R.id.image4, R.id.image3, R.id.bio_video, R.id.choose_your_interests})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.save:
                updateProfile();
                break;
            case R.id.picture:
                UploadImageView = picture;
                goToImageIntent();
                break;
            case R.id.image1:
                UploadImageView = image1;
                goToImageIntent();
                break;
            case R.id.image2:
                UploadImageView = image2;
                goToImageIntent();
                break;
            case R.id.image5:
                UploadImageView = image5;
                goToImageIntent();
                break;
            case R.id.image4:
                UploadImageView = image4;
                goToImageIntent();
                break;
            case R.id.image3:
                UploadImageView = image3;
                goToImageIntent();
                break;
            case R.id.bio_video:
                //UploadImageView = bioVideo;
                goToVideoIntent();
                break;
            case R.id.choose_your_interests:
                Intent intent = new Intent(this, ChooseInterestsActivity.class);
                startActivityForResult(intent, CHOOSE_INTEREST_REQUEST);
                break;
        }
    }

    private void updateProfile() {

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("about", RequestBody.create(MediaType.parse("text/plain"), about.getText().toString()));
        map.put("work", RequestBody.create(MediaType.parse("text/plain"), work.getText().toString()));

        map.put("show_age", RequestBody.create(MediaType.parse("text/plain"), showAge.isChecked() ? "on" : "off"));
        map.put("show_distance", RequestBody.create(MediaType.parse("text/plain"), showDistance.isChecked() ? "on" : "off"));


        MultipartBody.Part filePart = null;

        if (imgFile != null) {
            filePart = MultipartBody.Part.createFormData("picture", imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
        }

        if (male.isChecked()) {
            map.put("gender", RequestBody.create(MediaType.parse("text/plain"), "male"));
        } else if (female.isChecked()) {
            map.put("gender", RequestBody.create(MediaType.parse("text/plain"), "female"));
        }

        if (!codeSnippet.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        System.out.println("FF MAP " + map.toString());

        customDialog.show();
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = api.updateProfile(accessToken, map, filePart);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                    initView();
                    finish();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                customDialog.cancel();
            }
        });
    }

    public void goToImageIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    public void goToVideoIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_VIDEO_REQUEST);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_VIDEO_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean permission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permission2 = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (permission1 && permission2) {
                        goToImageIntent();
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                getString(R.string.please_grant_permissions_to_upload),
                                Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.enable),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            Glide.with(getApplicationContext()).load(imgDecodableString).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(UploadImageView);
            imgFile = new File(imgDecodableString);

            uploadImage(imgFile);
            /*if (UploadImageView.getId() != R.id.picture) {
                uploadImage(imgFile);
            }*/

        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            assert cursor != null;
            if(cursor.moveToFirst()){
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                imgFile = new File(imgDecodableString);
                uploadVideo(imgFile);
                videoPlayer.setUp(String.valueOf(selectedImage), JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL);
            }
        } else if (requestCode == CHOOSE_INTEREST_REQUEST) {
            initView();
        }

    }

    private void getProfile() {
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = GlobalData.api.getProfile(accessToken, new HashMap<String, Object>());
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    initView();
                } else {
                    if (response.code() == 401) {
                        SharedHelper.putKey(getApplicationContext(), "logged_in", false);
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
            }
        });
    }

    public String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    private void uploadVideo(File imgFile) {

        HashMap<String, RequestBody> map = new HashMap<>();
        MultipartBody.Part filePart = null;

        if (imgFile != null) {
            String tag = "bio_video";
            System.out.println(tag);
            filePart = MultipartBody.Part.createFormData(tag, imgFile.getName(), RequestBody.create(MediaType.parse("video/*"), imgFile));
        }

        if (!codeSnippet.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.uploading));
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();
        System.out.println("MAP " + map.toString());
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = api.updateProfile(accessToken, map, filePart);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Toast.makeText(getApplicationContext(), getString(R.string.bio_video_uploaded), Toast.LENGTH_SHORT).show();
                    initView();
                    finish();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), "Failed to Upload. Please try again later", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Network error. Please try agin later" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void uploadImage(File imgFile) {

        HashMap<String, RequestBody> map = new HashMap<>();
        MultipartBody.Part filePart = null;

        if (imgFile != null) {
            String tag = UploadImageView.getContentDescription().toString();
            System.out.println(tag);
            filePart = MultipartBody.Part.createFormData(tag, imgFile.getName(), RequestBody.create(MediaType.parse("image/*"), imgFile));
        }

        if (!codeSnippet.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }
        System.out.println("MAP " + map.toString());
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = api.updateProfile(accessToken, map, filePart);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                    initView();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
