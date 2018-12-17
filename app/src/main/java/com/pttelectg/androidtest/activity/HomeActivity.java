package com.pttelectg.androidtest.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.pttelectg.androidtest.R;
import com.pttelectg.androidtest.adapter.FilmAdapter;
import com.pttelectg.androidtest.application.Application;
import com.pttelectg.androidtest.callback.FilmFetchListener;
import com.pttelectg.androidtest.database.FilmDatabase;
import com.pttelectg.androidtest.helper.Constant;
import com.pttelectg.androidtest.helper.Utils;
import com.pttelectg.androidtest.model.Film;
import com.pttelectg.androidtest.callback.FilmService;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rd.utils.DensityUtils.dpToPx;

public class HomeActivity extends AppCompatActivity implements FilmAdapter.FilmClickListener, FilmFetchListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = HomeActivity.class.getSimpleName();
    RecyclerView recyclerView;
    LinearLayoutManager mLayoutManager;
    int currentItems, totalItems, scrollOutItems;
    Boolean isScrolling = false;
    FilmAdapter adapter;
    long id;
    SwipeRefreshLayout mSwipe;

    private FilmDatabase mDatabase;
    private ProgressDialog mDialog;
    private Handler mHandler;

    boolean isListView = false;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbars = findViewById(R.id.toolbar);


        initView();
        setSupportActionBar(toolbars);

        mDatabase = new FilmDatabase(this);
        loadFilms();

        mHandler = new Handler();

        try {
            Glide.with(this).load(R.drawable.covers).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mSwipe.setOnRefreshListener(this);

    }

    private void loadFilms() {
        mDialog = new ProgressDialog(HomeActivity.this);
        mDialog.setMessage("Loading Film Data...");
        mDialog.setCancelable(true);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(true);

        adapter.reset();

        mDialog.show();

        if (getNetworkAvailability()) {
//            mSwipe.setRefreshing(true);
//            mSwipe.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mSwipe.setRefreshing(true);
//
//                                getFeed();
//                            }
//                        }
//            );

            getFeed();
        } else {
            getFeedFromDatabase();
        }
    }

    private void getFeedFromDatabase() {
        Log.d(TAG, "getFeedFromDatabase: " + mDatabase);

        mDatabase.fetchFilm(this);
    }

    private void initView() {
        mSwipe = findViewById(R.id.swipe);
        mSwipe.setOnRefreshListener(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = mLayoutManager.getChildCount();
                totalItems = mLayoutManager.getItemCount();
                scrollOutItems = mLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    loadFilms();
                }
            }
        });

//        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(this);
//        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        if (recyclerView != null) {
//            recyclerView.setAdapter(adapter);
//            recyclerView.setNestedScrollingEnabled(false);
//
//        }
//        recyclerView.setLayoutManager(MyLayoutManager);
//
//        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        adapter = new FilmAdapter(this);

        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void tooggle() {
        if (isListView) {
            showGridView();
        } else {
            showListView();
        }
    }

    private void showListView() {
        staggeredGridLayoutManager.setSpanCount(1);
        isListView = true;
    }

    private void showGridView() {
        staggeredGridLayoutManager.setSpanCount(2);
        isListView = false;
    }

    //added code start here
    Runnable mAutoRefreshRunnable = new Runnable() {
        @Override
        public void run() {
            getFeed();
            mHandler.postDelayed(mAutoRefreshRunnable, 1000);
        }
    };


    private void getFeed() {
//        mSwipe.setRefreshing(true);
        Application application = Application.get(this);
        application.setFilmService(FilmService.Factory.getFilm(FilmService.TYPE_GSON));
        FilmService filmService = application.getFilmService();
        Call<Film> call = filmService.getFilmData("b", "e403a017");
        call.enqueue(new Callback<Film>() {
            @Override
            public void onResponse(@NonNull Call<Film> call, @NonNull Response<Film> response) {
//                pd.dismiss();

                    if (response.isSuccessful()) {

                        try {
                            Film films = response.body();
                            Log.d(TAG, "onResponse: " + films);

                            SaveIntoDatabase task = new SaveIntoDatabase();
                            task.execute(films);

                            adapter.addFilm(films);
                        }catch (Exception e) {
                            Log.d(TAG, "onResponse: " + e);

                        }
                    } else {
                        int sc = response.code();
                        switch (sc) {
                            case 400:
                                Log.e("Error 400", "Bad Request");
                                break;
                            case 404:
                                Log.e("Error 404", "Not Found");
                                break;
                            default:
                                Log.e("Error", "Generic Error");
                        }
                    }
                    mDialog.dismiss();

                }
            @Override
            public void onFailure(@NonNull Call<Film> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void getSearch(String title) {
//        mSwipe.setRefreshing(true);
        Application application = Application.get(this);
        application.setFilmService(FilmService.Factory.getFilm(FilmService.TYPE_GSON));
        FilmService filmService = application.getFilmService();
        Call<Film> call = filmService.getFilmData(title, "e403a017");
        call.enqueue(new Callback<Film>() {
            @Override
            public void onResponse(@NonNull Call<Film> call, @NonNull Response<Film> response) {
//                pd.dismiss();

                if (response.isSuccessful()) {

                    try {
                        Film films = response.body();
                        Log.d(TAG, "onResponse: " + films);

                        SaveIntoDatabase task = new SaveIntoDatabase();
                        task.execute(films);

                        adapter.addFilm(films);
                    }catch (Exception e) {
                        Log.d(TAG, "onResponse: " + e);

                    }
                } else {
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");
                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");
                            break;
                        default:
                            Log.e("Error", "Generic Error");
                    }
                }
                mDialog.dismiss();

            }
            @Override
            public void onFailure(@NonNull Call<Film> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t);
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem mSearch = menu.findItem(R.id.action_search);
//
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mSearch);

        mSearchView.setQueryHint("Search");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                getSearch(newText);
                return true;
            }
        });
        initCollapsingToolbar(mSearchView);

        return super.onCreateOptionsMenu(menu);
    }

    private void initCollapsingToolbar(final SearchView mSearchViews) {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                    mSearchViews.setVisibility(View.GONE);
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                    mSearchViews.setVisibility(View.VISIBLE);
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                    mSearchViews.setVisibility(View.GONE);
                }
            }
        });
    }

    public boolean getNetworkAvailability() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }

    @Override
    public void onClick(int position) {
        Film selectedFilm = adapter.getSelectedFilm(position);
        Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
        intent.putExtra(Constant.REFERENCE.FILM, selectedFilm);
        startActivity(intent);
    }

    @Override
    public void onDeliverAllFilms(ArrayList<Film> films) {

    }

    @Override
    public void onDeliverFilm(Film film) {
       adapter.addFilm(film);
    }

    @Override
    public void onHideDialog() {
        mDialog.dismiss();

    }

    @Override
    public void onRefresh() {
//        getFeed();
    }

    @SuppressLint("StaticFieldLeak")
    public class SaveIntoDatabase extends AsyncTask<Film, Void, Void> {


        private final String TAG = SaveIntoDatabase.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Film... params) {

            Film film = params[0];

            try {
                InputStream stream = new URL(film.getPoster()).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                film.setPicture(bitmap);
               id = mDatabase.addFilm(film);
                Log.d(TAG, "doInBackground: " + id);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
