package com.vayetek.financeapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.activities.ToolsActivity;


public class ToolsFragment extends Fragment {

    public static ToolsFragment newInstance() {

        Bundle args = new Bundle();

        ToolsFragment fragment = new ToolsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tools, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        View currencyConverterCard = rootView.findViewById(R.id.currency_converter_card);
        currencyConverterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ToolsActivity.class);
                intent.putExtra("toolsRequest", 1);
                startActivity(intent);
            }
        });
        View wealthEstimatorCard = rootView.findViewById(R.id.wealth_estimator_card);
        wealthEstimatorCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
