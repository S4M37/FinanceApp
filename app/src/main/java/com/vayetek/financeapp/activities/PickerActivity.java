package com.vayetek.financeapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.fragments.picker.CountryPickerFragment;


public class PickerActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        fragmentManager = getSupportFragmentManager();
        int action = getIntent().getIntExtra("action", 0);
        int fragmentRequest = getIntent().getIntExtra("fragmentRequest", 0);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentRequest) {
            case 1:
                fragmentTransaction.replace(R.id.container, CountryPickerFragment.newInstance(action));
                break;
            default:
                finish();
                break;
        }
        fragmentTransaction.commit();
    }

    public void showFragment(final Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragment != null) {
            fragmentTransaction.replace(R.id.container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }
}
