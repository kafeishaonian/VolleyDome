package com.volley.client.volley.url;


public class Domains {

    // api地址
    public static final String FORMAL_API_HOST = "http://api.tudou.com";

    private static String API_QILIN_DOMAIN = "";

    static {
        setQilinApiDomain(FORMAL_API_HOST);
    }

    public static void setQilinApiDomain(String api) {
        API_QILIN_DOMAIN = api;
    }

    public static String getQilinApiomain() {
        return API_QILIN_DOMAIN;
    }

}
