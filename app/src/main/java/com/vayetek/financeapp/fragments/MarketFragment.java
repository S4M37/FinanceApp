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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vayetek.financeapp.R;
import com.vayetek.financeapp.activities.ToolsActivity;
import com.vayetek.financeapp.adapters.MarketDataRecyclerAdapter;
import com.vayetek.financeapp.adapters.MarketSectoralIndicesRecyclerAdapter;
import com.vayetek.financeapp.adapters.PerformanceRecyclerAdapter;
import com.vayetek.financeapp.config.Const;
import com.vayetek.financeapp.models.IndicesSectoralModel;
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
    private RecyclerView marketSectoralIndicesRecyclerView;
    private ArrayList<IndicesSectoralModel> listSectoralIndices;
    private String clientId;
    private LineChart tunindexChart;
    private ArrayList<String> tunidexHours;
    private double previousClose;

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

        getIndiceSectoriel();
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

        marketSectoralIndicesRecyclerView = (RecyclerView) rootView.findViewById(R.id.market_sectoral_indices_list);
        marketSectoralIndicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        marketSectoralIndicesRecyclerView.setHasFixedSize(false);
        marketSectoralIndicesRecyclerView.setNestedScrollingEnabled(false);
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
        FloatingActionButton voirAllSectoralIndices = (FloatingActionButton) rootView.findViewById(R.id.voir_all_sectoral_indices);
        voirAllSectoralIndices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ToolsActivity.class);
                intent.putExtra("toolsRequest", 4);
                intent.putExtra("marketSectoralIndicesList", listSectoralIndices);
                startActivity(intent);
            }
        });
    }

    private void getIndiceSectoriel() {
        JsonObject ext = new JsonObject();
        ext.addProperty("userId", "0000");

        JsonArray supportedConnectionTypes = new JsonArray();
        supportedConnectionTypes.add("long-polling");
        supportedConnectionTypes.add("callback-polling");

        JsonObject advice = new JsonObject();
        advice.addProperty("timeout", 60000);
        advice.addProperty("interval", 0);

        JsonObject rootObject = new JsonObject();
        rootObject.addProperty("version", "1.0");
        rootObject.addProperty("minimumVersion", "0.9");
        rootObject.addProperty("channel", "/meta/handshake");
        rootObject.addProperty("id", 1);
        rootObject.add("ext", ext);
        rootObject.add("supportedConnectionTypes", supportedConnectionTypes);
        rootObject.add("advice", advice);
        JsonArray handshakeObject = new JsonArray();
        handshakeObject.add(rootObject);
        Call<ResponseBody> handshake = Utils.getBourseTunisWebApiRetrofitServices().handshake(handshakeObject);
        handshake.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("code", "onResponse: " + response.code());
                Log.d("message", "onResponse: " + response.message());
                if (response.code() != 200) {
                    return;
                }
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(response.body().string());
                    clientId = jsonArray.getJSONObject(0).getString("clientId");

                } catch (JSONException | IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                if (clientId == null) {
                    return;
                }
                JsonObject value = new JsonObject();
                value.addProperty("group", 11);
                value.addProperty("isIndexSectorial", true);
                JsonObject data = new JsonObject();
                data.addProperty("userId", "0000");
                data.addProperty("command", "onInitAction");
                data.add("value", value);
                JsonObject root = new JsonObject();
                root.addProperty("channel", "/bvmt/process");
                root.add("data", data);
                root.addProperty("id", "5");
                root.addProperty("clientId", clientId);
                final JsonArray indiceSectorielBody = new JsonArray();
                indiceSectorielBody.add(root);

                call = Utils.getBourseTunisWebApiRetrofitServices().getPerformanceIndices(indiceSectorielBody);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }

                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("code", "onResponse: " + response.code());
                        Log.d("message", "onResponse: " + response.message());
                        if (response.code() != 200) {
                            return;
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            JSONArray responseObject = new JSONArray(jsonArray.getJSONObject(0).getJSONObject("data").getJSONObject("value").getString("indexSectorial"));
                            listSectoralIndices = new ArrayList<>();
                            for (int i = 0; i < responseObject.length(); i++) {
                                JSONObject tmp = responseObject.getJSONObject(i);
                                IndicesSectoralModel indicesSectoralModel = new IndicesSectoralModel();
                                indicesSectoralModel.setPreviousClose(tmp.getString("previousClose"));
                                indicesSectoralModel.setName(tmp.getString("indice"));
                                indicesSectoralModel.setValue(tmp.getString("value"));
                                indicesSectoralModel.setVariation(tmp.getString("variation"));
                                indicesSectoralModel.setPercentChange(tmp.getDouble("percentChange"));
                                listSectoralIndices.add(indicesSectoralModel);
                            }

                            List<IndicesSectoralModel> indicesSectoralModelsSorted = new ArrayList<>(listSectoralIndices);
                            Collections.sort(indicesSectoralModelsSorted, new Comparator<IndicesSectoralModel>() {
                                @Override
                                public int compare(IndicesSectoralModel o1, IndicesSectoralModel o2) {
                                    return Math.abs(o1.getPercentChange()) < Math.abs(o2.getPercentChange()) ? 1 : -1;
                                }
                            });
                            indicesSectoralModelsSorted = indicesSectoralModelsSorted.subList(0, 3);

                            MarketSectoralIndicesRecyclerAdapter marketSectoralIndicesRecyclerAdapter = new MarketSectoralIndicesRecyclerAdapter(getContext(), indicesSectoralModelsSorted);
                            marketSectoralIndicesRecyclerView.setAdapter(marketSectoralIndicesRecyclerAdapter);

                        } catch (JSONException | IOException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        setLoading(false);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });

                value = new JsonObject();
                value.addProperty("group", 11);
                value.addProperty("isIndexReference", true);
                value.addProperty("isRecap", true);
                data = new JsonObject();
                data.addProperty("userId", "0000");
                data.addProperty("command", "onInitAction");
                data.add("value", value);
                root = new JsonObject();
                root.addProperty("channel", "/bvmt/process");
                root.add("data", data);
                root.addProperty("id", 6);
                root.addProperty("clientId", clientId);
                final JsonArray tuniDexObj = new JsonArray();
                tuniDexObj.add(root);
                Log.d("tuniDexObj", "onResponse: " + tuniDexObj);
                Call<ResponseBody> callTunidex = Utils.getBourseTunisWebApiRetrofitServices().getPerformanceIndices(tuniDexObj);
                callTunidex.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.d("code", "onResponse: " + response.code());
                        Log.d("message", "onResponse: " + response.message());
                        if (response.code() != 200) {
                            return;
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            JSONArray responseObject = new JSONArray(jsonArray.getJSONObject(0).getJSONObject("data").getJSONObject("value").getString("indexReference"));
                            previousClose = responseObject.getJSONObject(0).getDouble("previousClose");
                            responseObject = responseObject.getJSONObject(0).getJSONArray("lastTransaction");
                            YAxis yAxis = tunindexChart.getAxisLeft();
                            LimitLine ll1 = new LimitLine((float) previousClose, "Previous Close");
                            ll1.setLineWidth(4f);
                            ll1.enableDashedLine(10f, 10f, 0f);
                            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                            ll1.setTextSize(10f);
                            yAxis.addLimitLine(ll1);
                            Log.d("responseObject", "onResponse: " + responseObject.toString());
                            List<Entry> entries = new ArrayList<>();
                            tunidexHours = new ArrayList<>();
                            for (int i = 0; i < responseObject.length(); i++) {
                                JSONObject jsonObject = responseObject.getJSONObject(i);
                                entries.add(new Entry(i, (float) jsonObject.getDouble("value")));
                                tunidexHours.add(jsonObject.getString("time").substring(0, 5));
                            }
                            setTunindexData(entries);
                        } catch (JSONException | IOException | NullPointerException e) {
                            e.printStackTrace();
                        }
                        setLoading(false);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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
                return "-" + round(value, 1) + "%";
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
        //tunindex chart
        tunindexChart = (LineChart) rootView.findViewById(R.id.chart_tunindex);

        tunindexChart.getDescription().setEnabled(false);
        tunindexChart.getLegend().setEnabled(false);
        tunindexChart.setHighlightPerTapEnabled(false);
        tunindexChart.setHighlightPerDragEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        tunindexChart.setPinchZoom(false);
        tunindexChart.setDoubleTapToZoomEnabled(false);
        tunindexChart.setDrawGridBackground(false);
        xAxis = tunindexChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setDrawLimitLinesBehindData(true);
        xAxis.setTextSize(8f);


        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return tunidexHours.get((int) value);
            }
        });

        //yAxis.setEnabled(false);
        tunindexChart.getAxisRight().setEnabled(false);
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
                    marketDataModelSorted = marketDataModelSorted.subList(0, 3);
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

    private void setTunindexData(List<Entry> indTunindexEntries) {
        LineData lineData = new LineData();
        LineDataSet set = new LineDataSet(indTunindexEntries, "tunindex");
        set.setColor(Color.parseColor("#03A9F4"));
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(0, 0, 0));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawValues(false);
        lineData.addDataSet(set);
        //BarData data = new CombinedData();
        //data.setData(barData);
        tunindexChart.setData(lineData);
        tunindexChart.invalidate();
        // add a nice and smooth animation
        tunindexChart.animateY(500);
        tunindexChart.setVisibility(View.VISIBLE);
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
