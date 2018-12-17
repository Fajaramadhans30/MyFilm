package com.pttelectg.androidtest.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pttelectg.androidtest.R;
import com.pttelectg.androidtest.model.Film;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.Holder> {

    private static final String TAG = FilmAdapter.class.getSimpleName();
    private final FilmClickListener mListener;
    private ArrayList<Film> mFilm;

    public FilmAdapter(FilmClickListener listener) {
        mFilm = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_film, null, false);
        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        Film currFilm = mFilm.get(position);

        holder.mTitle.setText(currFilm.getTitle());
        holder.mGenre.setText(currFilm.getGenre());

        if (currFilm.isFromDatabase()) {
            holder.mPoster.setImageBitmap(currFilm.getPicture());
        } else {
            Picasso.with(holder.itemView.getContext()).load(currFilm.getPoster()).into(holder.mPoster);
        }
    }

    @Override
    public int getItemCount() {
        return mFilm.size();
    }

    public long addFilm(Film film) {
        mFilm.add(film);
        notifyDataSetChanged();
        return 0;
    }

    /**
     * @param position
     * @return
     */
    public Film getSelectedFilm(int position) {
        return mFilm.get(position);
    }

    public void reset() {
        mFilm.clear();
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mPoster;
        private TextView mTitle, mGenre;

        public Holder(View itemView) {
            super(itemView);
            mPoster = itemView.findViewById(R.id.thumbnail);
            mTitle = itemView.findViewById(R.id.title);
            mGenre = itemView.findViewById(R.id.genre);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(getLayoutPosition());
        }
    }

    public interface FilmClickListener {

        void onClick(int position);
    }
}
