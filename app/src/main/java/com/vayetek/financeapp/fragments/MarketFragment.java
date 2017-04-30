package com.vayetek.financeapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.vayetek.financeapp.R;
import com.vayetek.financeapp.activities.ToolsActivity;
import com.vayetek.financeapp.adapters.MarketDataRecyclerAdapter;
import com.vayetek.financeapp.adapters.PerformanceRecyclerAdapter;
import com.vayetek.financeapp.config.Const;
import com.vayetek.financeapp.models.MarketDataModel;
import com.vayetek.financeapp.models.PerformanceStateModel;
import com.vayetek.financeapp.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vayetek.financeapp.utils.Utils.round;


public class MarketFragment extends Fragment {
    //barEntries
    private BarChart haussesChart;
    private BarChart baissesChart;
    private BarChart quantitesChart;
    private BarChart volumesChart;
    private ArrayList<PerformanceStateModel> haussesEntriesStateObject;
    private ArrayList<PerformanceStateModel> baissesEntriesStateObject;
    private ArrayList<PerformanceStateModel> quantitesEntriesStateObject;
    private ArrayList<PerformanceStateModel> volumesEntriesStateObject;
    private RecyclerView quantityRecyclerView;
    private RecyclerView volumesRecyclerView;
    private View loading;
    private View marketData;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView marketDataRecyclerView;
    private ArrayList<MarketDataModel> marketDataModels;
    private TextView marketPerformance;

    public static MarketFragment newInstance() {

        Bundle args = new Bundle();

        MarketFragment fragment = new MarketFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_market, container, false);

        initializeCharts(rootView);
        initializeView(rootView);
        initializeRecyclerView(rootView);
        getBoursePerformanceIndices();
        getMarketData();
        return rootView;
    }

    private void initializeRecyclerView(View rootView) {
        volumesRecyclerView = (RecyclerView) rootView.findViewById(R.id.volumes_list);
        volumesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        volumesRecyclerView.setHasFixedSize(false);
        volumesRecyclerView.setNestedScrollingEnabled(false);

        quantityRecyclerView = (RecyclerView) rootView.findViewById(R.id.quantity_list);
        quantityRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quantityRecyclerView.setHasFixedSize(false);
        quantityRecyclerView.setNestedScrollingEnabled(false);

        marketDataRecyclerView = (RecyclerView) rootView.findViewById(R.id.market_data_list);
        marketDataRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        marketDataRecyclerView.setHasFixedSize(false);
        marketDataRecyclerView.setNestedScrollingEnabled(false);
    }

    private void initializeView(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pull_to_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getBoursePerformanceIndices();
                getMarketData();
            }
        });
        marketPerformance = (TextView) rootView.findViewById(R.id.market_performance);
        FloatingActionButton voirAllCotation = (FloatingActionButton) rootView.findViewById(R.id.voir_all);
        voirAllCotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ToolsActivity.class);
                intent.putExtra("toolsRequest", 3);
                intent.putExtra("marketDataList", marketDataModels);
                startActivity(intent);
            }
        });
    }

    private void initializeCharts(View rootView) {
        loading = rootView.findViewById(R.id.loading);
        marketData = rootView.findViewById(R.id.market_data);
        haussesChart = (BarChart) rootView.findViewById(R.id.chart_hausses);

        haussesChart.getDescription().setEnabled(false);
        haussesChart.setDrawBarShadow(false);
        haussesChart.setDrawValueAboveBar(true);
        haussesChart.getDescription().setEnabled(false);
        haussesChart.getLegend().setEnabled(false);
        haussesChart.setHighlightFullBarEnabled(false);
        haussesChart.setHighlightPerTapEnabled(false);
        haussesChart.setHighlightPerDragEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        haussesChart.setPinchZoom(false);
        haussesChart.setDoubleTapToZoomEnabled(false);
        haussesChart.setDrawGridBackground(false);
        XAxis xAxis = haussesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(8f);
        //xAxis.setLabelRotationAngle(-20f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return haussesEntriesStateObject.get((int) value).getMname();
            }
        });


        YAxis yAxis = haussesChart.getAxisLeft();
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "+" + round(value, 1) + "%";
            }
        });
        //yAxis.setEnabled(false);
        haussesChart.getAxisRight().setEnabled(false);


        baissesChart = (BarChart) rootView.findViewById(R.id.chart_baisses);

        baissesChart.getDescription().setEnabled(false);
        baissesChart.setDrawBarShadow(false);
        baissesChart.setDrawValueAboveBar(true);
        baissesChart.getDescription().setEnabled(false);
        baissesChart.getLegend().setEnabled(false);

        baissesChart.setHighlightFullBarEnabled(false);
        baissesChart.setHighlightPerTapEnabled(false);
        baissesChart.setHighlightPerDragEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        baissesChart.setPinchZoom(false);
        baissesChart.setDoubleTapToZoomEnabled(false);
        baissesChart.setDrawGridBackground(false);
        xAxis = baissesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(8f);
        //xAxis.setLabelRotationAngle(-20f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return baissesEntriesStateObject.get((int) value).getMname();
            }
        });


        yAxis = baissesChart.getAxisLeft();
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "-" + value + "%";
            }
        });
        baissesChart.getAxisRight().setEnabled(false);


        quantitesChart = (BarChart) rootView.findViewById(R.id.chart_quantity);
        quantitesChart.getDescription().setEnabled(false);
        quantitesChart.setDrawBarShadow(false);
        quantitesChart.setDrawValueAboveBar(true);
        quantitesChart.getDescription().setEnabled(false);
        quantitesChart.setHighlightFullBarEnabled(false);
        quantitesChart.setHighlightPerTapEnabled(false);
        quantitesChart.setHighlightPerDragEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        quantitesChart.setPinchZoom(false);
        quantitesChart.setDoubleTapToZoomEnabled(false);
        quantitesChart.setDrawGridBackground(false);
        quantitesChart.setHighlightPerTapEnabled(true);
        xAxis = quantitesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setTextSize(8f);
        //xAxis.setLabelRotationAngle(-20f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quantitesEntriesStateObject.get((int) value).getMname();
            }
        });


        yAxis = quantitesChart.getAxisLeft();
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value + "";
            }
        });
        quantitesChart.getAxisRight().setEnabled(false);
        quantitesChart.getLegend().setEnabled(false);

        //volumes chart
        volumesChart = (BarChart) rootView.findViewById(R.id.chart_volumes);

        volumesChart.getDescription().setEnabled(false);
        volumesChart.setDrawBarShadow(false);
        volumesChart.setDrawValueAboveBar(true);
        volumesChart.getDescription().setEnabled(false);
        volumesChart.getLegend().setEnabled(false);
        volumesChart.setHighlightFullBarEnabled(false);
        volumesChart.setHighlightPerTapEnabled(false);
        volumesChart.setHighlightPerDragEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        volumesChart.setPinchZoom(false);
        volumesChart.setDoubleTapToZoomEnabled(false);
        volumesChart.setDrawGridBackground(false);
        xAxis = volumesChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(8f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return volumesEntriesStateObject.get((int) value).getMname();
            }
        });


        yAxis = volumesChart.getAxisLeft();
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return (int) value / 1000 + "M TND";
            }
        });
        //yAxis.setEnabled(false);
        volumesChart.getAxisRight().setEnabled(false);

    }

    private void getMarketData() {
        Call<ResponseBody> call = Utils.getCurrencyApiRetrofitServices().getBourseData();
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
                    Log.d("getMarketData", "onResponse: " + jsonArray.toString());
                    Gson gson = Utils.getGson();
                    marketDataModels = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        MarketDataModel marketDataModel = gson.fromJson(jsonArray.getString(i), MarketDataModel.class);
                        marketDataModels.add(marketDataModel);
                    }

                    List<MarketDataModel> marketDataModelSorted = new ArrayList<>(marketDataModels);
                    Collections.sort(marketDataModelSorted, new Comparator<MarketDataModel>() {
                        @Override
                        public int compare(MarketDataModel o1, MarketDataModel o2) {
                            float f1 = Math.abs(Float.parseFloat(o1.getVariation().substring(0, o1.getVariation().length() - 1)));
                            float f2 = Math.abs(Float.parseFloat(o2.getVariation().substring(0, o2.getVariation().length() - 1)));
                            return f1 < f2 ? 1 : -1;
                        }
                    });
                    marketDataModelSorted = marketDataModelSorted.subList(0, 5);
                    MarketDataRecyclerAdapter marketDataRecyclerAdapter = new MarketDataRecyclerAdapter(getContext(), marketDataModelSorted);
                    marketDataRecyclerView.setAdapter(marketDataRecyclerAdapter);
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

    private void getBoursePerformanceIndices() {
        setLoading(true);
        Call<ResponseBody> call = Utils.getBourseTunisieApiRetrofitServices().getPerformanceIndices(Const.AUTHORIZATION_BOURSE_API);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("code", "onResponse: " + response.code());
                Log.d("message", "onResponse: " + response.message());
                if (response.code() != 200) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    jsonObject = jsonObject.getJSONObject("performances");
                    Gson gson = Utils.getGson();
                    List<BarEntry> haussesEntries = new ArrayList<>();
                    haussesEntriesStateObject = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("hausses");
                    PerformanceStateModel performanceStateModel = new PerformanceStateModel();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        performanceStateModel = gson.fromJson(String.valueOf(jsonArray.get(i)), PerformanceStateModel.class);
                        haussesEntriesStateObject.add(performanceStateModel);
                        haussesEntries.add(new BarEntry(i, Float.parseFloat(performanceStateModel.getVariation())));
                    }
                    baissesEntriesStateObject = new ArrayList<>();
                    List<BarEntry> baissesEntries = new ArrayList<>();
                    jsonArray = jsonObject.getJSONArray("baisses");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        performanceStateModel = gson.fromJson(String.valueOf(jsonArray.get(i)), PerformanceStateModel.class);
                        baissesEntriesStateObject.add(performanceStateModel);
                        baissesEntries.add(new BarEntry(i, Float.parseFloat(performanceStateModel.getVariation().substring(1))));
                    }
                    quantitesEntriesStateObject = new ArrayList<>();
                    List<BarEntry> quantitesEntries = new ArrayList<>();
                    jsonArray = jsonObject.getJSONArray("quantites");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        performanceStateModel = gson.fromJson(String.valueOf(jsonArray.get(i)), PerformanceStateModel.class);
                        quantitesEntriesStateObject.add(performanceStateModel);
                        quantitesEntries.add(new BarEntry(i, Float.parseFloat(performanceStateModel.getQuantity())));
                    }
                    volumesEntriesStateObject = new ArrayList<>();
                    List<BarEntry> volumesEntries = new ArrayList<>();
                    jsonArray = jsonObject.getJSONArray("volumes");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        performanceStateModel = gson.fromJson(String.valueOf(jsonArray.get(i)), PerformanceStateModel.class);
                        volumesEntriesStateObject.add(performanceStateModel);
                        volumesEntries.add(new BarEntry(i, Float.parseFloat(performanceStateModel.getVolume())));
                    }
                    String date = performanceStateModel.getSessionDate();
                    date = date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6);
                    marketPerformance.setText("Market Performance for " + date);
                    setHaussesData(haussesEntries);
                    setBaissesData(baissesEntries);
                    setQuantitiesData(quantitesEntries);
                    setVolumesData(volumesEntries);

                    PerformanceRecyclerAdapter quatityRecyclerAdapter = new PerformanceRecyclerAdapter(getContext(), quantitesEntriesStateObject);
                    quantityRecyclerView.setAdapter(quatityRecyclerAdapter);
                    PerformanceRecyclerAdapter volumesRecyclerAdapter = new PerformanceRecyclerAdapter(getContext(), volumesEntriesStateObject);
                    volumesRecyclerView.setAdapter(volumesRecyclerAdapter);
                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                //setLoading(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                //setLoading(false);
            }
        });

    }

    private void setHaussesData(List<BarEntry> haussesEntries) {
        BarData barData = new BarData();
        BarDataSet set = new BarDataSet(haussesEntries, "Hausses");
        set.setColor(Color.parseColor("#4CAF50"));
        set.setValueTextColor(Color.parseColor("#4CAF50"));

        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        barData.addDataSet(set);
        //BarData data = new CombinedData();
        //data.setData(barData);
        barData.setDrawValues(true);
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return round(value, 2) + " %";
            }
        });
        haussesChart.setData(barData);
        haussesChart.invalidate();
        // add a nice and smooth animation
        haussesChart.animateY(500);
        haussesChart.setVisibility(View.VISIBLE);
    }

    private void setBaissesData(List<BarEntry> baissesEntries) {
        BarData barData = new BarData();
        BarDataSet set = new BarDataSet(baissesEntries, "Baisses");
        set.setColor(Color.parseColor("#F44336"));
        set.setValueTextColor(Color.parseColor("#F44336"));
        barData.addDataSet(set);
        barData.setValueTextSize(10f);

        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "-" + round(value, 2) + "%";
            }
        });

        baissesChart.setData(barData);
        baissesChart.invalidate();
        // add a nice and smooth animation
        baissesChart.animateY(500);
        baissesChart.setVisibility(View.VISIBLE);

    }

    private void setQuantitiesData(List<BarEntry> dataList) {
        /*
        List<Integer> colors = new ArrayList<Integer>();
        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);
        for (int i = 0; i < dataList.size(); i++) {
            BarEntry d = dataList.get(i);
            // specific colors
            if (d.getY() >= 0)
                colors.add(green);
            else
                colors.add(red);
        }

*/
        BarData barData = new BarData();
        BarDataSet set = new BarDataSet(dataList, "quantites");
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextColor(Color.parseColor("#FF9800"));
        set.setColors(Color.parseColor("#FF9800"));
        barData.addDataSet(set);
        //BarData data = new CombinedData();
        //data.setData(barData);
        barData.setDrawValues(true);
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return (int) value + "";
            }
        });
        quantitesChart.setData(barData);
        quantitesChart.invalidate();
        // add a nice and smooth animation
        quantitesChart.animateY(1000);
        quantitesChart.setVisibility(View.VISIBLE);
    }

    private void setVolumesData(List<BarEntry> volumesEntries) {
        BarData barData = new BarData();
        BarDataSet set = new BarDataSet(volumesEntries, "Hausses");
        set.setColor(Color.parseColor("#03A9F4"));

        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(0, 0, 0));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        barData.addDataSet(set);
        //BarData data = new CombinedData();
        //data.setData(barData);
        barData.setDrawValues(true);
        barData.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return (int) value / 1000 + "M TND";
            }
        });
        volumesChart.setData(barData);
        volumesChart.invalidate();
        // add a nice and smooth animation
        volumesChart.animateY(500);
        volumesChart.setVisibility(View.VISIBLE);
    }

    public void setLoading(boolean isLoading) {
        if (!swipeRefreshLayout.isRefreshing()) {
            loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            marketData.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
        if (!isLoading) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
