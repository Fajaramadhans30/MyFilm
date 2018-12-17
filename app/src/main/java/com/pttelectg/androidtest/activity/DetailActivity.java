package com.pttelectg.androidtest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.pttelectg.androidtest.R;
import com.pttelectg.androidtest.helper.Constant;
import com.pttelectg.androidtest.model.Film;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private ImageView mPoster;
    private TextView mTitle, mGenre;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        Film film = (Film) intent.getSerializableExtra(Constant.REFERENCE.FILM);

        configViews();

        mTitle.setText(film.getTitle());
        mGenre.setText(film.getGenre());

        if (film.isFromDatabase()) {
            mPoster.setImageBitmap(film.getPicture());
        } else {
            Picasso.with(getApplicationContext()).load(film.getPoster()).into(mPoster);
        }
    }

    private void configViews() {
        mPoster = findViewById(R.id.thumbnail);
        mTitle = findViewById(R.id.title);
        mGenre = findViewById(R.id.genre);


    }
}
