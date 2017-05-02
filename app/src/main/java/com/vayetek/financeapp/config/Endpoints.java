package com.vayetek.financeapp.config;

public class Endpoints {

    public static final String SERVER_IP = "http://192.168.1.4/DOMparserCurrency/";
    public static final String BOURSE_TUNIS_BASE_URL = "http://www.mobile.boursedetunis.com/";
    public static final String GOOGLE_API_BASE_URL = "https://maps.googleapis.com/";
    public static final String BOURSE_TUNIS_API = "http://41.225.8.72/";


    public static final String INDICE_SECTORIEL_API = "brs/cometd/";
    public static final String HANDSHAKE = "brs/cometd/handshake";

    //http://www.google.com/finance/converter
    public static final String CURRENCY_CONVERTER_API = "currency.php";

    //http://www.poste.tn/change.php
    public static final String COURT_EXCHANGE_API = "dom-parsing-court-exchange.php";

    //http://www.ilboursa.com/marches/aaz.aspx
    public static final String COURT_DATA_API = "dom-parsing-court-data.php";

    /*
        POST http://www.mobile.boursedetunis.com/market/ws/performance
        {"Authorization":"Basic YW5kcm9pZDpBbjEwZFBhc3M="}
    */
    public static final String BOURSE_PERFORMANCE = "market/ws/performance";
    public static final String GOOGLE_NEARBYSEARCH = "maps/api/place/nearbysearch/json";
}

//http://www.mobile.boursedetunis.com/indice-app
//http://www.mobile.boursedetunis.com/graph-app
