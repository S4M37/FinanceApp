package com.vayetek.financeapp.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vayetek.financeapp.R;
import com.vayetek.financeapp.adapters.NewsRecyclerViewAdapter;
import com.vayetek.financeapp.services.RSSParser.RSSItem;
import com.vayetek.financeapp.services.RSSParser.RSSParser;

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
            "http://www.jawharafm.net/fr/rss/showRss/88/1/2", "http://rss.nytimes.com/services/xml/rss/nyt/Economy.xml"};

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
        initializeRecyclerView(rootView);
        new RetrieveRSSFeeds().execute();
        return rootView;
    }

    private void initializeRecyclerView(View rootView) {
        listNews = (RecyclerView) rootView.findViewById(R.id.list_news);
        listNews.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public class RetrieveRSSFeeds extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            retrieveRSSFeed(NEWS_ENDPOINT[2], itemlist);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {

            progress = ProgressDialog.show(getContext(), null, "Loading ...");

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            NewsRecyclerViewAdapter newsRecyclerViewAdapter = new NewsRecyclerViewAdapter(getContext(), itemlist);
            listNews.setAdapter(newsRecyclerViewAdapter);
            progress.dismiss();
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

}
