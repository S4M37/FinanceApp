package com.vayetek.financeapp.fragments.picker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.adapters.picker.CurrencyRecyclerAdapter;
import com.vayetek.financeapp.models.Country;
import com.vayetek.financeapp.utils.WidgetUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class CountryPickerFragment extends Fragment {
    View rootView;
    private RecyclerView list;
    private CurrencyRecyclerAdapter currencyRecyclerAdapter;
    private ArrayList<Country> countries;
    private EditText search;

    public static CountryPickerFragment newInstance(int action) {

        Bundle args = new Bundle();
        args.putInt("action", action);
        CountryPickerFragment fragment = new CountryPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.country_fragment, container, false);
        initializeCountries();
        initializeView();
        currencyRecyclerAdapter = new CurrencyRecyclerAdapter(getContext(), countries, CountryPickerFragment.this);
        list.setAdapter(currencyRecyclerAdapter);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        return rootView;
    }

    private void initializeCountries() {
        countries = new ArrayList<>();
        Country country = new Country();
        country.setName("Tunisian Dinar");
        country.setCode("TND");
        country.setId(1);
        countries.add(country);
        country = new Country();
        country.setName("EURO");
        country.setCode("EUR");
        country.setId(2);
        countries.add(country);
        country = new Country();
        country.setName("United States Dollar");
        country.setCode("USD");
        country.setId(3);
        countries.add(country);
        country = new Country();
        country.setName("Australian Dollar");
        country.setCode("AUD");
        country.setId(4);
        countries.add(country);
        country = new Country();
        country.setName("British Pound Sterling");
        country.setCode("GBP");
        country.setId(5);
        countries.add(country);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    private void initializeView() {
        Toolbar mainToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mainToolbar.setContentInsetsAbsolute(0, 0);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mainToolbar);
        mainToolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Toolbar mainSecond = (Toolbar) rootView.findViewById(R.id.toolbar_second);
        mainSecond.setContentInsetsAbsolute(0, 0);
        ImageView iconToolbar = (ImageView) rootView.findViewById(R.id.iconeToolbar);
        iconToolbar.setImageResource(R.drawable.icon_category);
        //textHint = (TextView) rootView.findViewById(R.id.region);
        //textHint.setTypeface(fontLato.getRegular());
        //textactualRegion = (TextView) rootView.findViewById(R.id.region_actuel);
        //textactualRegion.setTypeface(fontLato.getLight());
        search = (EditText) rootView.findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    String ch = Normalizer.normalize(s.toString().toLowerCase(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                    List<Country> tmp = new ArrayList();
                    for (int i = 0; i < countries.size(); i++) {
                        Country country = countries.get(i);
                        String countryName = Normalizer.normalize(country.getName(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        if (countryName.toLowerCase().contains(ch)) {
                            tmp.add(country);
                        }
                    }
                    list.setAdapter(currencyRecyclerAdapter);
                    currencyRecyclerAdapter.swap(tmp);
                } else {
                    list.setAdapter(currencyRecyclerAdapter);
                }
            }
        });

        list = (RecyclerView) rootView.findViewById(R.id.list);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(mLayoutManager);
        list.setHasFixedSize(true);
    }

    public void handleCountry(Country country) {
        Intent intent = new Intent();
        intent.putExtra("country", country);
        WidgetUtils.hideKeyboard(search);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }
}

