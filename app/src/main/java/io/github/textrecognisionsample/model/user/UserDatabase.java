package io.github.textrecognisionsample.model.user;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import io.github.textrecognisionsample.model.statistics.StatisticsDao;
import io.github.textrecognisionsample.model.statistics.StatisticsData;

@Database(entities = {UserData.class}, exportSchema = false, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    private static final String DB_NAME = "user";
    private static UserDatabase instance;

    public static synchronized UserDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, DB_NAME)
                    .build();
        }
        return instance;
    }

    public abstract UserDao userDao();
}
