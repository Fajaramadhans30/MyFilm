package com.pttelectg.androidtest.callback;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pttelectg.androidtest.model.Film;
import com.pttelectg.androidtest.helper.Constant;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FilmService {
    int TYPE_GSON = 1;
    int TYPE_STRING = 2;

    String BASE_URL = Constant.DOMAIN;


    @GET("?")
    Call<Film> getFilmData(@Query("t") String title, @Query("apikey") String apikey);


    class Factory {
        static FilmService filmService;



        static Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        public static FilmService getFilm(int type) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            if (filmService == null) {
                Retrofit retrofit = null;
                if (type == TYPE_GSON) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(client)
                            .build();
                } else if (type == TYPE_STRING) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build();

                }
                assert retrofit != null;
                filmService = retrofit.create(FilmService.class);
            }


            return filmService;
        }
    }
}
