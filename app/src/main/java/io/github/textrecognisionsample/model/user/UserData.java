package io.github.textrecognisionsample.model.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "user")
public class UserData {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private UUID uid;

    @ColumnInfo(name = "data")
    private String data;

    public UserData(String data) {
        this.uid = UUID.randomUUID();
        this.data = data;
    }

    @NonNull
    public UUID getUid() {
        return uid;
    }

    public void setUid(@NonNull UUID uid) {
        this.uid = uid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
