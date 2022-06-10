package io.github.textrecognisionsample.model.web;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.util.UUID;

import io.github.textrecognisionsample.model.SupermarketChain;

public class WebStatisticsData {

    private UUID uid;

    public SupermarketChain supermarketChain;

    public Double value;

    private long userId;

    private long id;

    public WebStatisticsData(UUID uid, SupermarketChain supermarketChain, Double value, long userId,
                          long id) {
        this.uid = uid;
        this.supermarketChain = supermarketChain;
        this.value = value;
        this.userId = userId;
        this.id = id;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }
}
