package com.pttelectg.androidtest.helper;

public class Constant {
    private static final String URL_API= "http://www.omdbapi.com/";

    public static final String DOMAIN = URL_API;

    public static final class HTTP {
        public static final String BASE_URL = "http://www.omdbapi.com/";
    }

    public static final class DATABASE {

        public static final String DB_NAME = "films";
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME = "film";

        public static final String DROP_QUERY = "DROP TABLE IF EXIST " + TABLE_NAME;

        public static final String GET_FILMS_QUERY = "SELECT * FROM " + TABLE_NAME;

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String YEAR = "year";
        public static final String IMDB_RATING = "imbd_rating";
        public static final String GENRE = "genre";
        public static final String POSTER_URL = "poster_url";
        public static final String PHOTO = "photo";


        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "" +
                "(" + ID + "STRING PRIMARY KEY," + TITLE + " TEXT NOT NULL,"
                + YEAR + " TEXT,"  + IMDB_RATING + " TEXT," + GENRE + " TEXT," + POSTER_URL + " TEXT not null,"
                + PHOTO + " blob not null)";
    }

//    public static final String CREATE_TABLE_QUERY = "CREATE TABLE" + TABLE_NAME + "" +
//            "(" + IMDB_ID + " INTEGER PRIMARY KEY," + TITLE + " TEXT NOT NULL,"
//            + YEAR + " TEXT,"  + IMDB_RATING + " TEXT," + GENRE + " TEXT"+ ")";

    public static final class REFERENCE {
        public static final String FILM = Config.PACKAGE_NAME + "film";
    }

    public static final class Config {
        public static final String PACKAGE_NAME = "com.pttelectg.androidtest.";
    }
}
