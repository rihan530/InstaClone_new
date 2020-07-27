package com.coolwhite.instaclone_1;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.coolwhite.instaclone_1.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;


import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    // 구글 로그인
    private static final int RC_SIGN_IN = 9001; // Intent Request ID
    private FirebaseAuth auth;
    private GoogleApiClient mGoogleApiClient;
    // 바인딩
    private ActivityLoginBinding binding;
    // 페이스북 로그인
    private CallbackManager callbackManager;
    // 로그인 리스너
    FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);

        // Firebase 로그인 통합 관리하는 Object 생성
        auth = FirebaseAuth.getInstance();

        // 이메일 로그인 버튼
        binding.emailLoginButton.setOnClickListener(this);

        // 구글 로그인 옵션
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // 구글 로그인
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        // 구글 로그인 버튼 가져오기
        binding.googleSignInButton.setOnClickListener(this);

        // Facebook 로그인 버튼
        binding.facebookLoginButton.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();

        // Firebase 인증 상태를 체크해서 인증에 성공하면 로그인하고 메인으로 넘어감
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                // 로그인
                if(user != null) {
                    // 로그인 성공 토스트 출력
                    Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                    // 로그인에서 메인 액티비티로 넘어가는 인텐트 생성
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    // 인텐트 실행
                    startActivity(intent);

                    finish();

                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        };

//        getHashKey();
    }

    // Facebook Key Hash 가져오는 메소드
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    // 이메일 회원가입 및 로그인
    private void createAndLoginEmail() {
        auth.createUserWithEmailAndPassword(binding.emailEdittext.getText().toString(), binding.passwordEdittext.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                        // 비밀번호를 8자리 이상 입력하지 않았을 경우
                        else if(binding.passwordEdittext.getText().toString().length() < 8) {
                            binding.progressBar.setVisibility(View.GONE);

                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // 아이디가 있을 경우엔 바로 로그인
                        else {
                            signinEmail();
                        }
                    }
                });
    }

    // 이메일 로그인
    private void signinEmail() {
        auth.signInWithEmailAndPassword(binding.emailEdittext.getText().toString(), binding.passwordEdittext.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // 비밀번호 불일치
                        if(!task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);

                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 구글 로그인
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(authCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            binding.progressBar.setVisibility((View.GONE));
                        }
                    }
                });
    }

    // Facebook 로그인
    // Facebook 토큰을 Firebase로 넘겨줌
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    // 로그인 결과
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Facebook SDK 로 값 전달
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // 구글에서 승인된 정보 가져옴
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            // 로그인에 성공했을 경우
            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 구글 로그인 버튼
            case R.id.google_sign_in_button:

                binding.progressBar.setVisibility(View.VISIBLE);

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

            // Facebook 로그인 버튼
            case R.id.facebook_login_button:

                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {

                        binding.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(FacebookException error) {

                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
                break;

            // 이메일 로그인 버튼
            case R.id.email_login_button:
                if(TextUtils.isEmpty(binding.emailEdittext.getText().toString() ) || TextUtils.isEmpty(binding.passwordEdittext.getText().toString() ) ) {
                    Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    createAndLoginEmail();
                }
                break;
        }
    }

    // Firebase 접속 실패
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}