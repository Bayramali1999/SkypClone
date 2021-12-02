package com.example.skypclone.model;

public class Contacts {

    private String name, status, ui, image;

    public Contacts(String name, String status, String ui, String image) {
        this.name = name;
        this.status = status;
        this.ui = ui;
        this.image = image;
    }

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUi() {
        return ui;
    }

    public void setUi(String ui) {
        this.ui = ui;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
