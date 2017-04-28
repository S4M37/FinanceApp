package com.vayetek.financeapp.adapters.picker;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.fragments.picker.CountryPickerFragment;
import com.vayetek.financeapp.models.Country;
import com.vayetek.financeapp.widgets.NetworkImageView;

import java.util.List;

public class CurrencyRecyclerAdapter extends RecyclerView.Adapter<CurrencyRecyclerAdapter.ViewHolder> {
    //private static final String ARG_COUNTRY_SEARCH = "COUNTRY";
    private Context context;
    private CountryPickerFragment countryPickerFragment;
    private List<Country> countries;

    public CurrencyRecyclerAdapter(Context context, List<Country> list, CountryPickerFragment countryPickerFragment) {
        this.context = context;
        this.countries = list;
        this.countryPickerFragment = countryPickerFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.name.setTextColor(Color.parseColor("#363636"));
        holder.img.setVisibility(View.GONE);
        final Country country = countries.get(position);
        final int resourceId = context.getResources().getIdentifier("country_" + country.getId(), "drawable",
                context.getPackageName());
        holder.img.setImageResource(resourceId);
        holder.name.setText(country.getName());
        holder.img.setVisibility(View.VISIBLE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPickerFragment.handleCountry(country);
            }
        });

    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView img;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.delegation_name);
            img = (NetworkImageView) itemView.findViewById(R.id.img);
        }
    }

    public void swap(List<Country> countries) {
        this.countries = countries;
        notifyDataSetChanged();
    }

    interface OnSubCategoryFiltreClickListener {
        void onSubCategoryFiltreClickListener(Country country);
    }

    private OnSubCategoryFiltreClickListener onSubCategoryFiltreClickListener;

    public void setOnSubCategoryFiltreClickListener(OnSubCategoryFiltreClickListener onSubCategoryFiltreClickListener) {
        this.onSubCategoryFiltreClickListener = onSubCategoryFiltreClickListener;
    }
}
