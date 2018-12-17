package com.pttelectg.androidtest.callback;

import com.pttelectg.androidtest.model.Film;

import java.util.ArrayList;
import java.util.List;

public interface FilmFetchListener {
    void onDeliverAllFilms(ArrayList<Film> films);

    void onDeliverFilm(Film film);

    void onHideDialog();
}
