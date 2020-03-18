package com.misterymatch.app.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.R;
import com.misterymatch.app.activity.ProfileDetailedActivity;
import com.misterymatch.app.model.User;
import com.misterymatch.app.twilio_video.TwilloVideoActivity;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.ApiClient;
import com.misterymatch.app.webservice.ApiInterface;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class ChatViewActivity extends AppCompatActivity {


    static final int REQUEST_VIDEO_CAPTURE = 3;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 101;
    ChatMessageAdapter mAdapter;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.chat_lv)
    ListView mChatView;
    @BindView(R.id.et_message)
    EditText etMessage;
    @BindView(R.id.send)
    ImageView send;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String TAG = "FireBase";
    Integer userId, sender = -1;
    String senderName, senderAvatar, chatPath = "", deviceToken, senderMobile;

    StorageReference storageRef;
    ChildEventListener childEventListener;
    List<String> childKeys = new ArrayList<>();

    public static ApiInterface FcmAPI = ApiClient.getFcmRetrofit().create(ApiInterface.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        ButterKnife.bind(this);
        userId = Integer.parseInt(SharedHelper.getKey(this, "user_id", "-1"));

        Bundle extras = getIntent().getExtras();
        if (extras == null || userId == -1) {
            return;
        }

        sender = extras.getInt("sender");
        senderName = extras.getString("sender_name");
        senderMobile = extras.getString("sender_mobile");
        senderAvatar = extras.getString("sender_avatar");
        deviceToken = extras.getString("device_token");

        System.out.println("SENDER " + sender + " USER" + userId);

        if (userId < sender) {
            chatPath = userId + "_chats_" + sender;
        } else {
            chatPath = sender + "_chats_" + userId;
        }

        System.out.println(chatPath);
        title.setText(senderName);
        storageRef = FirebaseStorage.getInstance().getReference();

        mAdapter = new ChatMessageAdapter(this, new ArrayList<Chat>(), senderAvatar);
        mChatView.setAdapter(mAdapter);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child(chatPath);
        childEventListener = myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                childKeys.add(dataSnapshot.getKey());

                Chat chat = null;
                try {
                    chat = dataSnapshot.getValue(Chat.class);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (chat == null) {
                    return;
                }

                mAdapter.add(chat);
                if (chat.getSender() != null && chat.getRead() != null) {
                    if (chat.getSender() == userId && chat.getRead() == 0) {
                        chat.setRead(1);
                        dataSnapshot.getRef().setValue(chat);
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                int pos = childKeys.indexOf(dataSnapshot.getKey());
                Chat chat = dataSnapshot.getValue(Chat.class);
                if (chat.getUser() == userId && chat.getRead() == 1) {
                    mAdapter.getItem(pos).setRead(1);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    @OnClick({R.id.back, R.id.send, R.id.camera, R.id.video, R.id.gallery, R.id.smiley, R.id.title, R.id.twilio_voice_call, R.id.twilio_video_call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.send:
                String myText = etMessage.getText().toString().trim();
                if (myText.length() > 0) {
                    sendMessage(myText);
                }
                break;
            case R.id.camera:
                cameraIntent();
                break;
            case R.id.video:
                dispatchTakeVideoIntent();
                break;
            case R.id.gallery:
                galleryIntent();
                break;
            case R.id.smiley:
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                etMessage.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                keyboard.showSoftInput(etMessage, 0);
                break;
            case R.id.title:
                getSingleUser(sender);
                break;
            case R.id.twilio_voice_call:
                if(senderMobile != null){
                    /*Intent callIntent = new Intent(getApplicationContext(), CallActivity.class);
                    callIntent.putExtra("phone", senderMobile);
                    callIntent.putExtra("caller_name", senderName);
                    startActivity(callIntent);*/
                }else {
                    Toast.makeText(this, "Invalid phone number", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.twilio_video_call:
                Intent callIntent = new Intent(getApplicationContext(), TwilloVideoActivity.class);
                callIntent.putExtra("chat_path", chatPath);
                callIntent.putExtra("sender", sender);
                startActivity(callIntent);
                break;
        }
    }

    private void sendMessage(String message) {

        if (myRef == null) {
            return;
        }

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setTimestamp(new Date().getTime());
        chat.setType("text");
        chat.setText(message);
        chat.setUser(userId);
        chat.setUrl("");
        chat.setRead(0);

        myRef.push().setValue(chat);

        etMessage.setText("");

        FcmData fcmData = new FcmData();
        fcmData.setBody(message);
        fcmData.setTitle(senderName);
        FcmNotification fcmNotification = new FcmNotification();
        fcmNotification.setBody(message);
        fcmNotification.setTitle(senderName);
        SendFCM(fcmData, fcmNotification);

    }

    private void sendFile(File file, final String type) {

        if (myRef == null) {
            return;
        }

        StorageReference chatStorageRef = storageRef.child(chatPath + "/" + file.getName());
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, getString(R.string.uploading), Toast.LENGTH_SHORT).show();
        UploadTask uploadTask = chatStorageRef.putStream(stream);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri downloadUrl = uri.getResult();
                //Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                Chat chat = new Chat();
                chat.setSender(sender);
                chat.setTimestamp(new Date().getTime());
                chat.setType(type);
                chat.setUrl(String.valueOf(downloadUrl));
                chat.setUser(userId);
                chat.setRead(0);
                myRef.push().setValue(chat);
            }
        });

    }

    private void cameraIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openCamera(ChatViewActivity.this, 0);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        } else {
            EasyImage.openCamera(ChatViewActivity.this, 0);
        }
    }

    private void galleryIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                EasyImage.openGallery(ChatViewActivity.this, 0);
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
            }
        } else {
            EasyImage.openGallery(ChatViewActivity.this, 0);
        }
    }

    private void dispatchTakeVideoIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            String path = getRealPathFromURI(videoUri);
            File videoFile = new File(path);
            if (videoFile.exists()) {
                sendFile(videoFile, "video");
            }
            //mVideoView.setVideoURI(videoUri);
        }
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                e.printStackTrace();
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                sendFile(imageFile, "image");
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {

            }
        });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean permission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permission2 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (permission1 && permission2) {
                        cameraIntent();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please give permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void getSingleUser(Integer userId) {

        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<User> call = api.getSingleUser(accessToken, userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    GlobalData.USER = response.body();
                    Intent intent = new Intent(ChatViewActivity.this, ProfileDetailedActivity.class);
                    intent.putExtra("isBottomEnabled", false);
                    startActivity(intent);
                    //startActivity(new Intent(getApplicationContext(), ProfileDetailedActivity.class));
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myRef != null) {
            myRef.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    private void SendFCM(FcmData fcmData, FcmNotification fcmNotification) {
        Fcm fcm = new Fcm();

        fcm.setTo(deviceToken);
        fcm.setFcmData(fcmData);
        fcm.setFcmNotification(fcmNotification);

        Call<Object> call = FcmAPI.sendFcm(fcm);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
            }
        });
    }
}
