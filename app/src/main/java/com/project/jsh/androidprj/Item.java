package com.project.jsh.androidprj;

/**
 * Created by jjh on 2017-12-03.
 */

public class Item {
    //    int image;
    private String title;
    private String information;

    //    int getImage() {
//        return this.image;
//    }
    public String getTitle() {
        return this.title;
    }
    public String getInformation() {
        return this.information;
    }

//    Item(int image, String title) {
//        this.image = image;
//        this.title = title;
//    }

    Item(String title, String information) {
//        this.image = image;
        this.title = title;
        this.information = information;
    }

    Item(String title) {
//        this.image = image;
        this.title = title;
    }

}
