package io.github.textrecognisionsample.model.statistics;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

import io.github.textrecognisionsample.model.SupermarketChain;

@Entity(tableName = "statistics")
public class StatisticsData {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private UUID uid;

    @ColumnInfo(name = "chain")
    public SupermarketChain supermarketChain;

    @ColumnInfo(name = "value")
    public Double value;


    public StatisticsData() {
        this.uid = UUID.randomUUID();
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }
}
