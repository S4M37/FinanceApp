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
import com.vayetek.financeapp.models.PerformanceStateModel;

import java.util.List;

import static com.vayetek.financeapp.utils.Utils.round;

public class PerformanceRecyclerAdapter extends RecyclerView.Adapter<PerformanceRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final List<PerformanceStateModel> performanceStateModels;

    public PerformanceRecyclerAdapter(Context context, List<PerformanceStateModel> list) {
        this.context = context;
        this.performanceStateModels = list;
    }

    @Override
    public PerformanceRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_performance, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PerformanceStateModel performanceStateModel = performanceStateModels.get(position);
        if (position % 2 != 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#09000000"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.name.setText(performanceStateModel.getCname() + " (" + performanceStateModel.getMname() + ")");
        holder.cour.setText(performanceStateModel.getLast());
        holder.variation.setText(round(Float.parseFloat(performanceStateModel.getVariation()),2)+"%");
        if (Float.valueOf(performanceStateModel.getVariation()) >= 0) {
            holder.icon.setImageResource(R.drawable.ic_action_up);
        } else {
            holder.icon.setImageResource(R.drawable.ic_action_down);
        }
    }


    @Override
    public int getItemCount() {
        return performanceStateModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView cour;
        TextView variation;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            cour = (TextView) itemView.findViewById(R.id.cour);
            variation = (TextView) itemView.findViewById(R.id.variation);
            icon = (ImageView) itemView.findViewById(R.id.variation_icon);
        }
    }
}
