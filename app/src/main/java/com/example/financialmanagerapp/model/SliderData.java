package com.example.financialmanagerapp.model;

public class SliderData {
    // image url is used to
    // store the url of image
    protected String imgUrl;
    protected String title;
    protected String description;

    public SliderData(String imgUrl, String title, String description) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
