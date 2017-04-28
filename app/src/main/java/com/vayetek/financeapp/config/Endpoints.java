package com.vayetek.financeapp.config;

public class Endpoints {

    /*  CouchBase Endpoint
     *  Run CouchBase as Docker container:
     *  docker run -d --name db -p 8091-8094:8091-8094 -p 11210:11210 couchbase
     *  Container runs under http://localhost:8091
     */
    public static final String SERVER_IP = "http://192.168.1.4";

    public static final String CURRENCY_CONVERTER_API = "/currency.php";
    public static final String COURT_EXCHANGE_API = "/DOMparser/dom-parsing-court.php";
}
