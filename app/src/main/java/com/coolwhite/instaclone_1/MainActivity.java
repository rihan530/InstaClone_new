package com.coolwhite.instaclone_1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.coolwhite.instaclone_1.databinding.ActivityMainBinding;
import com.coolwhite.instaclone_1.navigation.AddPhotoActivity;
import com.coolwhite.instaclone_1.navigation.AlarmFragment;
import com.coolwhite.instaclone_1.navigation.DetailViewFragment;
import com.coolwhite.instaclone_1.navigation.GridFragment;
import com.coolwhite.instaclone_1.navigation.UserFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import static com.coolwhite.instaclone_1.util.StatusCode.FRAGMENT_ARG;
import static com.coolwhite.instaclone_1.util.StatusCode.PICK_IMAGE_FROM_ALBUM;

public class MainActivity extends AppCompatActivity implements BottomNavigationViewEx.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.progressBar.setVisibility(View.VISIBLE);

        // Bottom Navigation View
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this);
        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_home:

                setToolbarDefault();

                Fragment detailViewFragment = new DetailViewFragment();

                Bundle bundle_0 = new Bundle();
                bundle_0.putInt(FRAGMENT_ARG, 0);

                detailViewFragment.setArguments(bundle_0);

                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, detailViewFragment).commit();

                return true;

            case R.id.action_search:

                setToolbarDefault();

                Fragment gridFragment = new GridFragment();

                Bundle bundle_1 = new Bundle();
                bundle_1.putInt(FRAGMENT_ARG, 1);

                gridFragment.setArguments(bundle_1);

                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, gridFragment).commit();

                return true;

            case R.id.action_add_photo:

                setToolbarDefault();

                startActivityForResult(new Intent(MainActivity.this, AddPhotoActivity.class),PICK_IMAGE_FROM_ALBUM);

                return true;

            case R.id.action_favorite_alarm:

                setToolbarDefault();

                Fragment alarmFragment = new AlarmFragment();

                Bundle bundle_3 = new Bundle();
                bundle_3.putInt(FRAGMENT_ARG, 3);

                alarmFragment.setArguments(bundle_3);

                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, alarmFragment).commit();

                return true;

            case R.id.action_account:

                setToolbarDefault();

                Fragment userFragment = new UserFragment();

                Bundle bundle_4 = new Bundle();
                bundle_4.putInt(FRAGMENT_ARG, 4);

                userFragment.setArguments(bundle_4);

                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, userFragment).commit();

                return true;
        }

        return false;
    }

    public void setToolbarDefault() {

        binding.toolbarTitleImage.setVisibility(View.VISIBLE);
        binding.toolbarBtnBack.setVisibility(View.GONE);
        binding.toolbarUsername.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}