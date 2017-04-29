package com.vayetek.financeapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.adapters.CurrencyExchangeRecyclerAdapter;
import com.vayetek.financeapp.models.ItemCurrencyExchange;
import com.vayetek.financeapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyExchangeFragment extends Fragment {

    private FloatingActionButton currencyChange;
    private View loadingCard;
    private RecyclerView currencyRecyclerView;

    public static CurrencyExchangeFragment newInstance() {
        Bundle args = new Bundle();
        CurrencyExchangeFragment fragment = new CurrencyExchangeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.currency_exchange_layout, container, false);

        initializeView(rootView);
        initializeToolbar(rootView);

        getCurrencyExchange();

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void getCurrencyExchange() {
        setLoading(true);
        Call<ResponseBody> call = Utils.getCurrencyApiRetrofitServices().getCourtExchange();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("code", "onResponse: " + response.code());
                Log.d("message", "onResponse: " + response.message());
                if (response.code() != 200) {
                    return;
                }
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());

                    ArrayList<ItemCurrencyExchange> itemCurrencyExchanges = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ItemCurrencyExchange itemCurrencyExchange = Utils.getGson().fromJson(jsonArray.getString(i), ItemCurrencyExchange.class);
                        itemCurrencyExchanges.add(itemCurrencyExchange);
                    }
                    CurrencyExchangeRecyclerAdapter currencyExchangeRecyclerAdapter = new CurrencyExchangeRecyclerAdapter(getContext(), itemCurrencyExchanges);
                    currencyRecyclerView.setAdapter(currencyExchangeRecyclerAdapter);
                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                setLoading(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                setLoading(false);
                t.printStackTrace();
            }
        });
    }

    private void initializeToolbar(View rootView) {
        Toolbar mainToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        mainToolbar.setContentInsetsAbsolute(0, 0);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mainToolbar);
        mainToolbar.setNavigationIcon(R.drawable.ic_action_navigation_arrow_back_white);
        mainToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void initializeView(View rootView) {
        loadingCard = rootView.findViewById(R.id.loading);
        currencyChange = (FloatingActionButton) rootView.findViewById(R.id.currency_change);
        currencyChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrencyExchange();
            }
        });

        currencyRecyclerView = (RecyclerView) rootView.findViewById(R.id.currency_exchange_list);
        currencyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        currencyRecyclerView.setHasFixedSize(false);
        currencyRecyclerView.setNestedScrollingEnabled(false);
    }

    void setLoading(boolean isLoading) {
        currencyChange.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        loadingCard.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
}
