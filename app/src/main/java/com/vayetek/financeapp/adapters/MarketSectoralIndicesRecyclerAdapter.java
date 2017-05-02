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
import com.vayetek.financeapp.models.IndicesSectoralModel;

import java.util.List;

public class MarketSectoralIndicesRecyclerAdapter extends RecyclerView.Adapter<MarketSectoralIndicesRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final List<IndicesSectoralModel> indicesSectoralModels;

    public MarketSectoralIndicesRecyclerAdapter(Context context, List<IndicesSectoralModel> list) {
        this.context = context;
        this.indicesSectoralModels = list;
    }

    @Override
    public MarketSectoralIndicesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_market_sectoral_indice, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        IndicesSectoralModel indicesSectoralModel = indicesSectoralModels.get(position);
        if (position % 2 != 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#09000000"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.indice.setText(indicesSectoralModel.getName());
        holder.previousClose.setText(indicesSectoralModel.getPreviousClose());
        holder.lastClose.setText(indicesSectoralModel.getValue());
        holder.variation.setText(String.valueOf(indicesSectoralModel.getPercentChange()));
        if (indicesSectoralModel.getVariation().equals("+")) {
            holder.icon.setImageResource(R.drawable.ic_action_up);
        } else {
            holder.icon.setImageResource(R.drawable.ic_action_down);
        }
    }


    @Override
    public int getItemCount() {
        return indicesSectoralModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView indice;
        TextView previousClose;
        TextView lastClose;
        TextView variation;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            indice = (TextView) itemView.findViewById(R.id.indice);
            previousClose = (TextView) itemView.findViewById(R.id.previous_closed);
            lastClose = (TextView) itemView.findViewById(R.id.last_close);
            variation = (TextView) itemView.findViewById(R.id.variation);
            icon = (ImageView) itemView.findViewById(R.id.variation_icon);
        }
    }
}
