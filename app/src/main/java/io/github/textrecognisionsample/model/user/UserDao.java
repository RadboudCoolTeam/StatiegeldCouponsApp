package io.github.textrecognisionsample.model.user;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.textrecognisionsample.model.SupermarketChain;
import io.github.textrecognisionsample.model.statistics.StatisticsData;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<UserData> getAll();

    @Update
    void update(UserData userData);

    @Query("DELETE FROM user")
    public void nukeTable();

    @Insert
    void insert(UserData userData);
}
