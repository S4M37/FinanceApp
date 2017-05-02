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
import com.vayetek.financeapp.fragments.MarketDataFragment;
import com.vayetek.financeapp.fragments.MarketSectoralIndicesFragment;
import com.vayetek.financeapp.models.IndicesSectoralModel;
import com.vayetek.financeapp.models.MarketDataModel;

import java.util.List;

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
            case 3:
                List<MarketDataModel> marketDataModelList = (List<MarketDataModel>) getIntent().getSerializableExtra("marketDataList");
                if (marketDataModelList == null) {
                    finish();
                }
                fragmentTransaction.replace(R.id.container, MarketDataFragment.newInstance(marketDataModelList));
                break;
            case 4:
                List<IndicesSectoralModel> indicesSectoralModels = (List<IndicesSectoralModel>) getIntent().getSerializableExtra("marketSectoralIndicesList");
                if (indicesSectoralModels == null) {
                    finish();
                }
                fragmentTransaction.replace(R.id.container, MarketSectoralIndicesFragment.newInstance(indicesSectoralModels));
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
