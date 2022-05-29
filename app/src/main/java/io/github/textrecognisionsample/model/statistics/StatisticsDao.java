package io.github.textrecognisionsample.model.statistics;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.github.textrecognisionsample.model.SupermarketChain;

@Dao
public interface StatisticsDao {

    @Query("SELECT * FROM statistics")
    List<StatisticsData> getAll();

    @Query("SELECT * FROM statistics WHERE chain LIKE :supermarketChain")
    List<StatisticsData> getDataByChain(SupermarketChain supermarketChain);

    @Query("SELECT value FROM statistics WHERE chain LIKE :supermarketChain")
    List<Double> getValueByChain(SupermarketChain supermarketChain);

    @Insert
    void insertAll(StatisticsData... statisticsData);

    @Delete
    void delete(StatisticsData statisticsData);

    @Update
    void update(StatisticsData statisticsData);
}
