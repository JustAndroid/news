package com.allNews.data.NewApp;


import com.google.gson.annotations.Expose;

import java.util.List;

public class FieldImage {

    @Expose(serialize = true)
    private List<Und> und;

    public FieldImage() {
    }

    public List<Und> getUnd() {
        return und;
    }

    public void setUnd(List<Und> und) {
        this.und = und;
    }
}
