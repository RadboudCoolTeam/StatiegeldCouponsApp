package io.github.textrecognisionsample.model.web;


import java.util.Arrays;

public class WebImage {

    private long id;

    private long width;

    private long height;

    private byte[] data;

    public WebImage(long id, long width, long height, byte[] data) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.data = Arrays.copyOf(data, data.length);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
