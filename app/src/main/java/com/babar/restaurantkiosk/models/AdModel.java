package com.babar.restaurantkiosk.models;

/***********************************
 * Created by Babar on 12/17/2020.  *
 ***********************************/
public class AdModel {
    private String url;
    private String name;
    private String contentType;

    public AdModel() {
    }

    public AdModel(String url, String name, String contentType) {
        this.url = url;
        this.name = name;
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
