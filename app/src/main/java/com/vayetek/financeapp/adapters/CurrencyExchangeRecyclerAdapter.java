package com.vayetek.financeapp.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.models.ItemCurrencyExchange;

import java.util.List;

public class CurrencyExchangeRecyclerAdapter extends RecyclerView.Adapter<CurrencyExchangeRecyclerAdapter.ViewHolder> {

    private final Context context;
    private final List<ItemCurrencyExchange> itemCurrencyExchanges;

    public CurrencyExchangeRecyclerAdapter(Context context, List<ItemCurrencyExchange> list) {
        this.context = context;
        this.itemCurrencyExchanges = list;
    }

    @Override
    public CurrencyExchangeRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_exchange, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemCurrencyExchange itemCurrencyExchange = itemCurrencyExchanges.get(position);
        if (position % 2 != 0) {
            holder.itemView.setBackgroundColor(Color.parseColor("#09000000"));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        holder.currency.setText(itemCurrencyExchange.currency);
        holder.code.setText(itemCurrencyExchange.code);
        holder.unity.setText(itemCurrencyExchange.unit);
        holder.buy.setText(itemCurrencyExchange.buy);
        holder.sell.setText(itemCurrencyExchange.sell);
    }


    @Override
    public int getItemCount() {
        return itemCurrencyExchanges.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView currency;
        TextView code;
        TextView unity;
        TextView buy;
        TextView sell;

        public ViewHolder(View itemView) {
            super(itemView);
            currency = (TextView) itemView.findViewById(R.id.currency);
            code = (TextView) itemView.findViewById(R.id.code);
            unity = (TextView) itemView.findViewById(R.id.unity);
            buy = (TextView) itemView.findViewById(R.id.buy);
            sell = (TextView) itemView.findViewById(R.id.sell);
        }
    }
}
