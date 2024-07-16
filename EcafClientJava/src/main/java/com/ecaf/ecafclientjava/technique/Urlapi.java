package com.ecaf.ecafclientjava.technique;

public enum Urlapi {
    BASE_URL("https://pa-api-0tcm.onrender.com/");

    private final String url;


    Urlapi(String url) {
        this.url = url;
    }

    // Method to retrieve the URL value
    public String getUrl() {
        return url;
    }

}
