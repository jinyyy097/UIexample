package com.example.uiexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.uiexample.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private ActivityMainBinding mainBinding;
    private static ArrayList<Fragment> fragments = new ArrayList<Fragment>();


    public static int getFragmentListSize(){
        return fragments.size();
    }

    /**
     * 리스트에서 프래그먼트 추가
     * @param fragment
     */
    public static void addFragment(Fragment fragment) {
        getFragmentList().add(fragment);
        Log.e("MainFragmentActivity", "addFragment current Stack == " + getFragmentListSize());
    }

    /**
     * 리스트에서 프래그먼트 제거
     * @param fragment
     */
    public static void removeFragment(Fragment fragment) {
        getFragmentList().remove(fragment);
        Log.e("MainFragmentActivity", "removeFragment current Stack == " + getFragmentListSize());
    }

    public static ArrayList<Fragment> getFragmentList() {
        if (fragments == null) {
            fragments = new ArrayList<Fragment>();
        }
        return fragments;
    }
    public void goToFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameview,fragment)
                .commitAllowingStateLoss();
        this.addFragment(fragment);
    }
    public void backToFragment(){
        if(getFragmentListSize()>1) {
            final Fragment currentFragment = getFragmentList().get(getFragmentListSize() - 1);
            final Fragment showFragment = getFragmentList().get(getFragmentListSize() - 2);
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(currentFragment)
                    .show(showFragment)
                    .commitAllowingStateLoss();
            removeFragment(currentFragment);
        }
    }
    @Override
    public void onBackPressed() {
        if (getFragmentList().size() == 0) {
            finish();
            return;
        } else {
            if (getFragmentList().size() > 1) {

                final Fragment currentFragment = getFragmentList().get(getFragmentListSize() - 1);
                final Fragment showFragment = getFragmentList().get(getFragmentListSize() - 2);

                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(getResources().getIdentifier("hold", "anim", getPackageName()), getResources().getIdentifier("anim_slide_out_right", "anim", getPackageName()))
                        .remove(currentFragment)
                        .show(showFragment)
                        .commitAllowingStateLoss();
                removeFragment(currentFragment);


            } else {
                finish();
                removeFragment(getFragmentList().get(getFragmentListSize() - 1));
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

//        Intent intent = getIntent();
//
//        if(intent.getStringExtra("modifydata")!=null)
//        {
//
//            MainFragment mainFragment = new MainFragment();
//            Bundle bundle = new Bundle();
//            bundle.putString("modifydata", intent.getStringExtra("modifydata"));
//            mainFragment.setArguments(bundle);
//            goToFragment(mainFragment);
//
//        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        LoginFragment loginFragment = new LoginFragment();
        goToFragment(loginFragment);




    }

}