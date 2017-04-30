package com.vayetek.financeapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.models.MarketDataModel;

import java.util.List;

public class MarketDataRecyclerAdapter extends RecyclerView.Adapter<MarketDataRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final List<MarketDataModel> marketDataModels;

    public MarketDataRecyclerAdapter(Context context, List<MarketDataModel> list) {
        this.context = context;
        this.marketDataModels = list;
    }

    @Override
    public MarketDataRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_market_data, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MarketDataModel marketDataModel = marketDataModels.get(position);
        if (position % 2 != 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#09000000"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.name.setText(marketDataModel.getName());
        holder.ouverture.setText(marketDataModel.getOpenedWith());
        holder.last.setText(marketDataModel.getClosedWith());
        holder.volTitre.setText(marketDataModel.getVolumeTitle());
        holder.volQuantity.setText(marketDataModel.getVolumeTnd());
        holder.variation.setText(marketDataModel.getVariation());
        if (Float.valueOf(marketDataModel.getVariation().substring(0, marketDataModel.getVariation().length() - 1)) >= 0) {
            holder.icon.setImageResource(R.drawable.ic_action_up);
        } else {
            holder.icon.setImageResource(R.drawable.ic_action_down);
        }
    }


    @Override
    public int getItemCount() {
        return marketDataModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView ouverture;
        TextView volTitre;
        TextView volQuantity;
        TextView last;
        TextView variation;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            ouverture = (TextView) itemView.findViewById(R.id.ouverture);
            volTitre = (TextView) itemView.findViewById(R.id.vol_titre);
            volQuantity = (TextView) itemView.findViewById(R.id.vol_quantity);
            last = (TextView) itemView.findViewById(R.id.last);
            variation = (TextView) itemView.findViewById(R.id.variation);
            icon = (ImageView) itemView.findViewById(R.id.variation_icon);
        }
    }
}
