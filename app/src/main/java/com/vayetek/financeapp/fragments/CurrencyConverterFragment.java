package com.vayetek.financeapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.activities.PickerActivity;
import com.vayetek.financeapp.models.Country;
import com.vayetek.financeapp.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrencyConverterFragment extends Fragment {

    Country currencyFrom;
    Country currencyTo;

    TextView currencyNameFrom;
    TextView currencyNameTo;
    ImageView currencyImgTo;
    ImageView currencyImgFrom;
    EditText currencyAmountFrom;
    EditText currencyAmountTo;
    private static final int CURRENCY_FROM_PICKER_REQUEST = 124;
    private static final int CURRENCY_TO_PICKER_REQUEST = 126;
    private FloatingActionButton currencyChange;
    private View loadingCard;

    public static CurrencyConverterFragment newInstance() {
        Bundle args = new Bundle();
        CurrencyConverterFragment fragment = new CurrencyConverterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.currency_convertor_layout, container, false);

        currencyFrom = new Country();
        currencyFrom.setName("Tunisian Dinar");
        currencyFrom.setCode("TND");
        currencyFrom.setId(1);

        currencyTo = new Country();
        currencyTo.setName("EURO");
        currencyTo.setCode("EUR");
        currencyTo.setId(2);

        initializeView(rootView);
        initializeToolbar(rootView);
        setUpCurrencyFrom();
        setUpCurrencyTo();
        return rootView;
    }

    private void setUpCurrencyFrom() {
        final int resourceId = getResources().getIdentifier("country_" + currencyFrom.getId(), "drawable", getContext().getPackageName());
        currencyImgFrom.setImageResource(resourceId);
        currencyNameFrom.setText(currencyFrom.getName());
    }

    private void setUpCurrencyTo() {
        final int resourceId = getResources().getIdentifier("country_" + currencyTo.getId(), "drawable", getContext().getPackageName());
        currencyImgTo.setImageResource(resourceId);
        currencyNameTo.setText(currencyTo.getName());
    }

    private void initializeToolbar(View rootView) {
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
    }

    private void initializeView(View rootView) {
        final View currencyFromLayout = rootView.findViewById(R.id.currency_layout_from);
        currencyFromLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickerActivity.class);
                intent.putExtra("fragmentRequest", 1);
                startActivityForResult(intent, CURRENCY_FROM_PICKER_REQUEST);
            }
        });
        final View currencyToLayout = rootView.findViewById(R.id.currency_layout_to);
        currencyToLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickerActivity.class);
                intent.putExtra("fragmentRequest", 1);
                startActivityForResult(intent, CURRENCY_TO_PICKER_REQUEST);
            }
        });
        currencyNameFrom = (TextView) rootView.findViewById(R.id.currency_name_from);
        currencyImgFrom = (ImageView) rootView.findViewById(R.id.currency_img_from);
        currencyAmountFrom = (EditText) rootView.findViewById(R.id.currency_amount_from);
        currencyNameTo = (TextView) rootView.findViewById(R.id.currency_name_to);
        currencyImgTo = (ImageView) rootView.findViewById(R.id.currency_img_to);
        currencyAmountTo = (EditText) rootView.findViewById(R.id.currency_amount_to);

        loadingCard = rootView.findViewById(R.id.loading);
        currencyChange = (FloatingActionButton) rootView.findViewById(R.id.currency_change);
        currencyChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Country tmp = currencyFrom;
                currencyFrom = currencyTo;
                currencyTo = tmp;
                setUpCurrencyFrom();
                setUpCurrencyTo();
                convertCurrency();
            }
        });

        currencyAmountFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    convertCurrency();
                } else {
                    currencyAmountTo.setText("");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CURRENCY_FROM_PICKER_REQUEST:
                    currencyFrom = (Country) data.getSerializableExtra("country");
                    setUpCurrencyFrom();
                    convertCurrency();
                    break;
                case CURRENCY_TO_PICKER_REQUEST:
                    currencyTo = (Country) data.getSerializableExtra("country");
                    setUpCurrencyTo();
                    convertCurrency();
                    break;
            }
        }
    }


    void convertCurrency() {
        setLoading(true);
        Call<ResponseBody> call = Utils.getCurrencyApiRetrofitServices().
                convertCurrency(currencyFrom.getCode(), currencyTo.getCode(), Double.parseDouble(currencyAmountFrom.getEditableText().toString()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("message", "onResponse: " + response.message());
                Log.d("code", "onResponse: " + response.code());
                if (response.code() != 200) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    currencyAmountTo.setText(jsonObject.getString("currency"));
                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                setLoading(false);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                setLoading(false);
            }
        });
    }

    void setLoading(boolean isLoading) {
        currencyChange.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        loadingCard.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }


}
