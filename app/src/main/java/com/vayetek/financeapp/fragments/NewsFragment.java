package com.vayetek.financeapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.adapters.NewsRecyclerViewAdapter;
import com.vayetek.financeapp.services.RSSItem;
import com.vayetek.financeapp.services.RSSParser;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class NewsFragment extends Fragment {

    private ArrayList<RSSItem> itemlist;
    private RecyclerView listNews;

    public static final String[] NEWS_ENDPOINT = {"http://www.lemonde.fr/crise-financiere/rss_full.xml",
            "http://rss.nytimes.com/services/xml/rss/nyt/Economy.xml"};
    private SwipeRefreshLayout swipeRefreshLayout;
    private NewsRecyclerViewAdapter newsRecyclerViewAdapter;
    private int randomChannel = 0;
    private View loading;

    public static NewsFragment newInstance() {

        Bundle args = new Bundle();

        NewsFragment fragment = new NewsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_news, container, false);
        itemlist = new ArrayList<>();
        initializeView(rootView);
        initializeRecyclerView(rootView);

        new RetrieveRSSFeeds().execute(randomChannel);
        return rootView;
    }

    private void initializeView(View rootView) {
        loading = rootView.findViewById(R.id.loading);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pull_to_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int random = (int) (Math.random() * NEWS_ENDPOINT.length);
                while (randomChannel == random) {
                    random = (int) (Math.random() * NEWS_ENDPOINT.length);
                }
                randomChannel = random;
                new RetrieveRSSFeeds().execute(randomChannel);
            }
        });
    }

    private void initializeRecyclerView(View rootView) {
        listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
        listNews.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private class RetrieveRSSFeeds extends AsyncTask<Integer, Void, Void> {


        @Override
        protected Void doInBackground(Integer... params) {
            Log.d("Endpoint", "doInBackground: " + NEWS_ENDPOINT[params[0]]);
            itemlist = new ArrayList<>();
            retrieveRSSFeed(NEWS_ENDPOINT[params[0]], itemlist);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            if (!swipeRefreshLayout.isRefreshing()) {
                setLoading(true);
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            if (newsRecyclerViewAdapter == null) {
                newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(getContext(), itemlist);
                listNews.setAdapter(newsRecyclerViewAdapter);
            } else {
                newsRecyclerViewAdapter.setItemlist(itemlist);
            }
            setLoading(false);
            swipeRefreshLayout.setRefreshing(false);
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private void retrieveRSSFeed(String urlToRssFeed, ArrayList<RSSItem> list) {
        try {
            URL url = new URL(urlToRssFeed);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader xmlreader = parser.getXMLReader();
            RSSParser theRssHandler = new RSSParser(list);

            xmlreader.setContentHandler(theRssHandler);

            InputSource is = new InputSource(url.openStream());

            xmlreader.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLoading(boolean isLoading) {
        loading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

}
