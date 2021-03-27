package com.gradproject.hospi;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResultSearchCategory {
    @SerializedName("documents")
    @Expose
    private List<Document> documents = null;
    @SerializedName("meta")
    @Expose
    private Meta meta;

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
