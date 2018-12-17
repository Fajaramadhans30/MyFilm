package com.pttelectg.androidtest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pttelectg.androidtest.callback.FilmFetchListener;
import com.pttelectg.androidtest.helper.Constant;
import com.pttelectg.androidtest.helper.Utils;
import com.pttelectg.androidtest.model.Film;

import java.util.ArrayList;


public class FilmDatabase extends SQLiteOpenHelper {

    private static final String TAG = FilmDatabase.class.getSimpleName();

    public FilmDatabase(Context context) {
        super(context, Constant.DATABASE.DB_NAME, null, Constant.DATABASE.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Constant.DATABASE.CREATE_TABLE_QUERY);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Constant.DATABASE.DROP_QUERY);
        this.onCreate(db);
    }

    public long addFilm(Film film) {

        Log.d(TAG, "Values Got " + film.getTitle());
        Log.d(TAG, "Values GENRE " + film.getGenre());
        Log.d(TAG, "Values POSTER " + film.getPoster());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
//        values.put(Constant.DATABASE.IMDB_ID, film.getImdbID());
        values.put(Constant.DATABASE.TITLE, film.getTitle());
        values.put(Constant.DATABASE.YEAR, film.getYear());
        values.put(Constant.DATABASE.IMDB_RATING, film.getImdbRating());
        values.put(Constant.DATABASE.GENRE, film.getGenre());
        values.put(Constant.DATABASE.POSTER_URL, film.getPoster());
        values.put(Constant.DATABASE.PHOTO, Utils.getPictureByteOfArray(film.getPicture()));

//        try {
            long id =  db.insert(Constant.DATABASE.TABLE_NAME, null, values);
//        } catch (Exception e) {

//        }
        db.close();
        return id;
    }

    public void fetchFilm(FilmFetchListener listener) {
        FilmFetcher fetcher = new FilmFetcher(listener, this.getWritableDatabase());
        fetcher.start();
    }

    public class FilmFetcher extends Thread {

        private final FilmFetchListener mListener;
        private final SQLiteDatabase mDb;

        public FilmFetcher(FilmFetchListener listener, SQLiteDatabase db) {
            mListener = listener;
            mDb = db;
        }

        @Override
        public void run() {
            Cursor cursor = mDb.rawQuery(Constant.DATABASE.GET_FILMS_QUERY, null);

            final ArrayList<Film> filmList = new ArrayList<>();

            if (cursor.getCount() > 0) {

                if (cursor.moveToFirst()) {
                    do {
                        Film film = new Film();
                        film.setFromDatabase(true);
                        film.setTitle(cursor.getString(cursor.getColumnIndex(Constant.DATABASE.TITLE)));
                        film.setYear(cursor.getString(cursor.getColumnIndex(Constant.DATABASE.YEAR)));
                        film.setImdbRating(cursor.getString(cursor.getColumnIndex(Constant.DATABASE.IMDB_RATING)));
                        film.setGenre(cursor.getString(cursor.getColumnIndex(Constant.DATABASE.GENRE)));
                        film.setPicture(Utils.getBitmapFromByte(cursor.getBlob(cursor.getColumnIndex(Constant.DATABASE.PHOTO))));
//                        film.setImdbID(cursor.getString(cursor.getColumnIndex(Constant.DATABASE.IMDB_ID)));
                        film.setPoster(cursor.getString(cursor.getColumnIndex(Constant.DATABASE.POSTER_URL)));

                        filmList.add(film);
                        publishFilm(film);

                    } while (cursor.moveToNext());
                }
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverAllFilms(filmList);
                    mListener.onHideDialog();
                }
            });
        }

        public void publishFilm(final Film film) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onDeliverFilm(film);
                }
            });
        }
    }
}
