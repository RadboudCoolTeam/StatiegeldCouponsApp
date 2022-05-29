package io.github.textrecognisionsample.model.statistics;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {StatisticsData.class}, exportSchema = false, version = 1)
public abstract class StatisticsDatabase extends RoomDatabase {
    private static final String DB_NAME = "statistics";
    private static StatisticsDatabase instance;

    public static synchronized StatisticsDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), StatisticsDatabase.class, DB_NAME)
                    .build();
        }
        return instance;
    }

    public abstract StatisticsDao statisticsDao();
}
