package com.maria.gallery.mvp.model.entity;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Image {

    @NonNull
    @SerializedName("sha256")
    private String id = "";

    @SerializedName("path")
    private String path;

    @SerializedName("size")
    private long size;

    @SerializedName("preview")
    private String previewDownloadLink;

    @SerializedName("name")
    private String name;

    @SerializedName("file")
    private String fileDownloadLink;

    @SerializedName("created")
    private String date;

    @NonNull
    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public String getPreviewDownloadLink() {
        return previewDownloadLink;
    }

    public String getName() {
        return name;
    }

    public String getFileDownloadLink() {
        return fileDownloadLink;
    }

    public String getDate() {
        return date;
    }

    public Image(@NonNull String id, String path, long size, String previewDownloadLink, String name, String fileDownloadLink, String date) {
        this.id = id;
        this.path = path;
        this.size = size;
        this.previewDownloadLink = previewDownloadLink;
        this.name = name;
        this.fileDownloadLink = fileDownloadLink;
        this.date = date;
    }
}
