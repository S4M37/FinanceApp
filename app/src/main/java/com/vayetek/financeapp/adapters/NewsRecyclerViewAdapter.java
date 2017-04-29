package com.vayetek.financeapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.services.RSSItem;
import com.vayetek.financeapp.widgets.NetworkImageView;

import java.util.ArrayList;
import java.util.List;


public class NewsRecyclerViewAdapter extends RecyclerView.Adapter<NewsRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private List<RSSItem> itemlist;

    public NewsRecyclerViewAdapter(Context context, ArrayList<RSSItem> itemlist) {
        this.context = context;
        this.itemlist = itemlist;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RSSItem rssItem = itemlist.get(position);
        holder.newsDescription.setText(rssItem.description);
        holder.newsTitle.setText(rssItem.title);
        holder.newsDate.setText(rssItem.date);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(rssItem.link));
                context.startActivity(i);
            }
        });
        holder.newsImg.setImageUrl(rssItem.mediaContent);
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle;
        TextView newsDescription;
        TextView newsDate;
        NetworkImageView newsImg;

        ViewHolder(View itemView) {
            super(itemView);
            newsTitle = (TextView) itemView.findViewById(R.id.news_title);
            newsDescription = (TextView) itemView.findViewById(R.id.news_description);
            newsDate = (TextView) itemView.findViewById(R.id.news_date);
            newsImg = (NetworkImageView) itemView.findViewById(R.id.news_img);
        }
    }

    public void setItemlist(List<RSSItem> itemlist) {
        this.itemlist = itemlist;
        notifyDataSetChanged();
    }
}
