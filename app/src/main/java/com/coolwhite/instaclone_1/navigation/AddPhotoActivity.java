package com.coolwhite.instaclone_1.navigation;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.CursorLoader;


import com.coolwhite.instaclone_1.DTO.ContentDTO;
import com.coolwhite.instaclone_1.R;
import com.coolwhite.instaclone_1.databinding.ActivityAddPhotoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.coolwhite.instaclone_1.util.StatusCode.PICK_IMAGE_FROM_ALBUM;

public class AddPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    // Data Binding
    private ActivityAddPhotoBinding binding;

    private String photoUrl;

    // Firebase Storage, Database, Auth
    private FirebaseStorage firebaseStorage;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_photo);

        // ImageVIew Button EditText 버튼
        binding.addphotoBtnUpload.setOnClickListener(this);

        // 권한 요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        // 앨범 오픈
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);

        // Firebase Storage
        firebaseStorage = FirebaseStorage.getInstance();

        // Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();

        // Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        binding.addphotoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 앨범에서 사진 선택할 경우
        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == RESULT_OK) {

            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader cursorLoader = new CursorLoader(this, data.getData(), proj, null, null, null);
            Cursor cursor = cursorLoader.loadInBackground();

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            // 이미지 경로
            photoUrl = cursor.getString(column_index);

            // 이미지뷰에 이미지 세팅
            binding.addphotoImage.setImageURI(data.getData());
        }
    }

    public void contentUpload() {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.addphoto_btn_upload && photoUrl != null) {
            binding.progressBar.setVisibility(View.VISIBLE);

            File file = new File(photoUrl);
            Uri contentUri = Uri.fromFile(file);

            final StorageReference storageReference = firebaseStorage.getReferenceFromUrl("gs://instaclone-568ef.appspot.com").child("images").child(contentUri.getLastPathSegment());

            final UploadTask uploadTask = storageReference.putFile(contentUri);

//            FirebaseStorage.getInstance().getReference().child("images").child("uid").putFile(contentUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            FirebaseStorage.getInstance().getReference().child("images").child("uid").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//
//                                    DatabaseReference images = firebaseDatabase.getReference().child("images").child("uid").push();
//
//                                    Date date = new Date();
//                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                    ContentDTO contentDTO = new ContentDTO();
//
//                                    // 이미지 주소
//                                    contentDTO.imageUrl = uri.toString();
//
//                                    // 유저의 UID
//                                    contentDTO.uid = firebaseAuth.getCurrentUser().getUid();
//
//                                    // 게시물 설명
//                                    contentDTO.explain = binding.addphotoEditExplain.getText().toString();
//
//                                    // 유저 아이디
//                                    contentDTO.userId = firebaseAuth.getCurrentUser().getEmail();
//
//                                    // 게시물 업로드 시간
//                                    contentDTO.timestamp = simpleDateFormat.format(date);
//
//                                    // 게시물 데이터 생성 및 액티비티 종료
//                                    images.setValue(contentDTO);
//
//                                    setResult(RESULT_OK);
//                                    finish();
//                                }
//                            });
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    binding.progressBar.setVisibility(View.GONE);
//
//                    Toast.makeText(AddPhotoActivity.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
//                }
//            });

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(AddPhotoActivity.this, "업로드에 성공하였습니다.", Toast.LENGTH_SHORT).show();

//                    @SuppressWarnings("VisibleForTests")
//                    // deprecated
//                  Uri uri = taskSnapshot.getDownloadUrl();





                    DatabaseReference images = firebaseDatabase.getReference().child("images").push();

                    Date date = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    ContentDTO contentDTO = new ContentDTO();

                    // 이미지 주소
                    contentDTO.imageUrl = storageReference.getDownloadUrl().toString();

                    // 유저의 UID
                    contentDTO.uid = firebaseAuth.getCurrentUser().getUid();

                    // 게시물 설명
                    contentDTO.explain = binding.addphotoEditExplain.getText().toString();

                    // 유저 아이디
                    contentDTO.userId = firebaseAuth.getCurrentUser().getEmail();

                    // 게시물 업로드 시간
                    contentDTO.timestamp = simpleDateFormat.format(date);

                    // 게시물 데이터 생성 및 액티비티 종료
                    images.setValue(contentDTO);

                    setResult(RESULT_OK);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(AddPhotoActivity.this, "업로드에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
