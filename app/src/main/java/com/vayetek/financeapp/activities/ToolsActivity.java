package com.vayetek.financeapp.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.fragments.CurrencyConverterFragment;
import com.vayetek.financeapp.fragments.CurrencyExchangeFragment;

public class ToolsActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        fragmentManager = getSupportFragmentManager();
        int fragmentRequest = getIntent().getIntExtra("toolsRequest", 0);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentRequest) {
            case 1:
                fragmentTransaction.replace(R.id.container, CurrencyConverterFragment.newInstance());
                break;
            case 2:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fragmentTransaction.replace(R.id.container, CurrencyExchangeFragment.newInstance());
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
