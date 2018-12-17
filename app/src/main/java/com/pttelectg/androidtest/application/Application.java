package com.pttelectg.androidtest.application;

import android.content.Context;

import com.pttelectg.androidtest.callback.FilmService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class Application {
    private FilmService filmService;

    private static Context mContext;
    private static Application application;
    private Scheduler defaultSubscribeScheduler;



    public synchronized static Application get(Context context) {
        mContext = context;
        if (application == null) {
            application = new Application();
        }
        return application;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    public FilmService getFilmService() {
        if (filmService == null) {
            return null;
        }
        return filmService;
    }

    public void setFilmService(FilmService filmService) {
        this.filmService = filmService;
    }

}
