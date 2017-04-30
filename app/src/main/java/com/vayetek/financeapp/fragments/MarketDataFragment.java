package com.vayetek.financeapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.adapters.MarketDataRecyclerAdapter;
import com.vayetek.financeapp.models.MarketDataModel;

import java.io.Serializable;
import java.util.List;


public class MarketDataFragment extends Fragment {

    public static MarketDataFragment newInstance(List<MarketDataModel> list) {

        Bundle args = new Bundle();
        args.putSerializable("marketDataList", (Serializable) list);
        MarketDataFragment fragment = new MarketDataFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marketdata, container, false);
        initializeToolbar(rootView);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        RecyclerView marketDataList = (RecyclerView) rootView.findViewById(R.id.market_data_list);
        marketDataList.setLayoutManager(new LinearLayoutManager(getContext()));

        List<MarketDataModel> marketDataModels = (List<MarketDataModel>) getArguments().getSerializable("marketDataList");
        MarketDataRecyclerAdapter marketDataRecyclerAdapter = new MarketDataRecyclerAdapter(getContext(), marketDataModels);
        marketDataList.setAdapter(marketDataRecyclerAdapter);
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
}
