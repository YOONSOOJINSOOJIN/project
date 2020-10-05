package com.example.smuocr;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Voca.class}, version = 1)
public abstract class VocaDatabase extends RoomDatabase {

    public final static String DATABASE_NAME = "Voca.db";
    private static VocaDatabase instance;

    public abstract VocaDao vocaDao();

    public static synchronized VocaDatabase getInstance(Context context) {
        if(instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    VocaDatabase.class, "voca_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private VocaDao vocaDao;
        private PopulateDbAsyncTask(VocaDatabase db) {
            vocaDao = db.vocaDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}
