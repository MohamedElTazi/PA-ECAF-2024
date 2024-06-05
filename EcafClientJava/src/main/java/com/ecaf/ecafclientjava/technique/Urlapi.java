package com.ecaf.ecafclientjava.technique;

public enum Urlapi {
    BASE_URL("http://localhost:3000/");

    private final String url;


    Urlapi(String url) {
        this.url = url;
    }

    // Method to retrieve the URL value
    public String getUrl() {
        return url;
    }

}
