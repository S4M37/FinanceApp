package com.vayetek.financeapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.vayetek.financeapp.R;
import com.vayetek.financeapp.adapters.MainViewPagerAdapter;
import com.vayetek.financeapp.fragments.MarketFragment;
import com.vayetek.financeapp.fragments.NewsFragment;
import com.vayetek.financeapp.fragments.ToolsFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MaterialSearchView materialSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeToolbar();
        initializeSearchView();

        initiliazeTabView();
    }

    private void initiliazeTabView() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) ((LinearLayout) tabLayout.getTabAt(0).getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
                ((TextView) ((LinearLayout) tabLayout.getTabAt(1).getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
                ((TextView) ((LinearLayout) tabLayout.getTabAt(2).getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
                switch (tab.getPosition()) {
                    case 0:
                        ((TextView) ((LinearLayout) tab.getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case 1:
                        ((TextView) ((LinearLayout) tab.getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
                        break;
                    case 2:
                        ((TextView) ((LinearLayout) tab.getCustomView()).getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupTabIcons(tabLayout);
    }

    private void setupTabIcons(TabLayout tabLayout) {
        LinearLayout tabOne = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabOne.getChildAt(0)).setText("MARKET");
        ((TextView) tabOne.getChildAt(0)).setTextColor(Color.parseColor("#ffffff"));

        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);
        LinearLayout tabTow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabTow.getChildAt(0)).setText("NEWS");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTow);
        LinearLayout tabThree = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.custom_tab_view, null);
        ((TextView) tabThree.getChildAt(0)).setText("TOOLS");
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_favourite, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
        ((TextView) tabTow.getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
        ((TextView) tabThree.getChildAt(0)).setTextColor(Color.parseColor("#50ffffff"));
    }


    private void setupViewPager(ViewPager viewPager) {
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MarketFragment(), "MARKET");
        adapter.addFragment(new NewsFragment(), "NEWS");
        adapter.addFragment(new ToolsFragment(), "TOOLS");
        viewPager.setAdapter(adapter);
    }

    private void initializeSearchView() {
        materialSearchView = (MaterialSearchView) findViewById(R.id.material_search);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
        materialSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        materialSearchView.setVoiceSearch(true); //or false

    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        toolbar.setSubtitleTextColor(Color.parseColor("#FFFFFF"));
    }

    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen()) {
            materialSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            materialSearchView.showSearch();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MaterialSearchView.REQUEST_VOICE:
                    ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (matches != null && matches.size() > 0) {
                        String searchWrd = matches.get(0);
                        if (!TextUtils.isEmpty(searchWrd)) {
                            materialSearchView.setQuery(searchWrd, false);
                        }
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}