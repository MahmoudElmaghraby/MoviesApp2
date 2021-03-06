package com.elmaghraby.android.moviesapp2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.elmaghraby.android.moviesapp2.adapter.MoviesAdapter;
import com.elmaghraby.android.moviesapp2.adapter.ReviewAdapter;
import com.elmaghraby.android.moviesapp2.adapter.TrailerAdapter;
import com.elmaghraby.android.moviesapp2.api.Client;
import com.elmaghraby.android.moviesapp2.api.Service;
import com.elmaghraby.android.moviesapp2.data.FavoriteDBHelper;
import com.elmaghraby.android.moviesapp2.livedata.AppDatabase;
import com.elmaghraby.android.moviesapp2.livedata.FavoriteEntry;
import com.elmaghraby.android.moviesapp2.model.Movie;
import com.elmaghraby.android.moviesapp2.model.Result;
import com.elmaghraby.android.moviesapp2.model.ResultReview;
import com.elmaghraby.android.moviesapp2.model.ReviewsResponse;
import com.elmaghraby.android.moviesapp2.model.TrailerResponse;
import com.elmaghraby.android.moviesapp2.viewModel.AppExcutor;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.m_movie_title) TextView mMovieName;
    @BindView(R.id.movie_plotsynopsis) TextView mPolotSynopsis;
    @BindView(R.id.movies_userratings) TextView mUserRating;
    @BindView(R.id.movie_releasedata) TextView mReleaseData;
    @BindView(R.id.imgeviewmovie)ImageView imageView;
    private List<Result> mResultTrailer;
    @BindView(R.id.recycler_view_trailer)RecyclerView mRecycler;
    private TrailerAdapter mAdpater;
    private FavoriteDBHelper helper;
    private Movie movie;
    private AppDatabase database;
    private MoviesAdapter adapter;
    private final AppCompatActivity activity=DetailActivity.this;
    private String thumbnial,movieName,synopsis,userRating,releaseDate;
    private int movie_id;
    private List<FavoriteEntry>entiry=new ArrayList<>();
    @BindView(R.id.recycler_reviews) RecyclerView recyclerViewReviews;
    private List<ResultReview>mlistReview;
    private ReviewAdapter reviewAdapter;



    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_deails_layout);
        ButterKnife.bind(this);
        database=AppDatabase.getData(getApplicationContext());
        Intent intentStartActivity=getIntent();
        if (intentStartActivity.hasExtra("original_title")){

             thumbnial=intentStartActivity.getStringExtra("poster_path");
             movieName=intentStartActivity.getStringExtra("original_title");
             synopsis=intentStartActivity.getStringExtra("overview");
             userRating=intentStartActivity.getStringExtra("vote_average");
            releaseDate=intentStartActivity.getStringExtra("release_date");

            movie_id=intentStartActivity.getIntExtra("id",10);

            Picasso.with(this)
                    .load(thumbnial)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imageView);
            mMovieName.setText(movieName);
            mPolotSynopsis.setText(synopsis);
            mUserRating.setText(userRating);
            mReleaseData.setText(releaseDate);

        }else {
            Toast.makeText(this, "No API Data ", Toast.LENGTH_SHORT).show();
        }

        intialView();
        checkState(movieName);
        intailReviews();


    }
    @SuppressLint("StaticFieldLeak")
    private void checkState(final String movieName){
        final MaterialFavoriteButton mBTfavorite = findViewById(R.id.favoriteButtom);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                entiry.clear();
                entiry=database.favoriteDao().loadAll(movieName);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (entiry.size()>0){
                    mBTfavorite.setFavorite(true);
                    mBTfavorite.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite==true){
                                saveLifeData();
                                Snackbar.make(buttonView,"Add Favorite"
                                        ,Snackbar.LENGTH_SHORT).show();
                            }else {
                                deleteFavoirte(movie_id);
                                Snackbar.make(buttonView,"Delete Favorite"
                                        ,Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    mBTfavorite.setOnFavoriteChangeListener(new MaterialFavoriteButton.OnFavoriteChangeListener() {
                        @Override
                        public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                            if (favorite==true){
                                saveLifeData();
                                Snackbar.make(buttonView,"Add Favorite"
                                        ,Snackbar.LENGTH_SHORT).show();
                            }else {
                                int mId=getIntent().getExtras().getInt("id");
                                deleteFavoirte(mId);
                                Snackbar.make(buttonView,"Delete Favorite"
                                        ,Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }.execute();

    }

    private  void  intialView(){

        mResultTrailer=new ArrayList<>();
        mAdpater=new TrailerAdapter(this ,mResultTrailer);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(getApplicationContext());
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(mAdpater);
        mAdpater.notifyDataSetChanged();
        loadJson();
    }
    private void intailReviews(){
        mlistReview=new ArrayList<>();
        reviewAdapter=new ReviewAdapter(this,mlistReview );
        RecyclerView.LayoutManager manager=new LinearLayoutManager(this);
        recyclerViewReviews.setLayoutManager(manager);
        recyclerViewReviews.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();
        loadJson2();

    }

    private void loadJson(){
        int mId=getIntent().getExtras().getInt("id");
        try {

            if (MainActivity.API_KEY.isEmpty()){
                Toast.makeText(getApplicationContext(), "This is empty API"
                        , Toast.LENGTH_SHORT).show();
                return;
            }

            Client client = new Client();
            Service service = client.getClient().create(Service.class);
            Call<TrailerResponse> call = service.getVidoes(mId, MainActivity.API_KEY);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Result> results = response.body().getResults();

                    mRecycler.setAdapter(new TrailerAdapter(getApplicationContext(),results));

                    mRecycler.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Toast.makeText(DetailActivity.this, "Error fetching data"
                            , Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){

        }

    }
    private void loadJson2(){
        int id=getIntent().getExtras().getInt("id");
        try {
            if (MainActivity.API_KEY.isEmpty()){
                Toast.makeText(activity, "No API Key", Toast.LENGTH_SHORT).show();
                return;
            }
            Client client=new Client();
            Service service=client.getClient().create(Service.class);
            Call<ReviewsResponse> responseCall=service.getReviews(id,MainActivity.API_KEY);
            responseCall.enqueue(new Callback<ReviewsResponse>() {
                @Override
                public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                    List<ResultReview> list=response.body().getResultsrewies();
                    recyclerViewReviews.setAdapter(new ReviewAdapter(getApplicationContext(),list));
                    recyclerViewReviews.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<ReviewsResponse> call, Throwable t) {
                    Toast.makeText(activity, "Error Fetch data ", Toast.LENGTH_SHORT).show();

                }
            });
        }catch (Exception e){

        }
    }
    public void saveFavorite(){

        helper=new  FavoriteDBHelper(activity);
        movie=new Movie();
        int mId=getIntent().getExtras().getInt("id");
        String thumbnial=getIntent().getExtras().getString("poster_path");
        String userRating=getIntent().getExtras().getString("vote_average");

        movie.setId(mId);
        movie.setOriginalTitle(mMovieName.getText().toString().trim());
        movie.setVoteAverage(Double.parseDouble(userRating));
        movie.setPosterPath(thumbnial);
        movie.setOverview(mPolotSynopsis.getText().toString().trim());
        helper.appFavorite(movie);
        Toast.makeText(activity, "data saved", Toast.LENGTH_SHORT).show();
    }
    private void saveLifeData(){
        int mId=getIntent().getExtras().getInt("id");
        String thumbnial=getIntent().getExtras().getString("poster_path");
        String movieName=getIntent().getExtras().getString("original_title");
        String synopsis=getIntent().getExtras().getString("overview");
        String userRating=getIntent().getExtras().getString("vote_average");

        final FavoriteEntry favoriteEntry=new FavoriteEntry(mId,movieName,Double.parseDouble(userRating),
                synopsis,thumbnial);
        AppExcutor.getExecutor().getDeskIo().execute(new Runnable() {
            @Override
            public void run() {
                database.favoriteDao().insetFavorite(favoriteEntry);
            }
        });
    }
    private void deleteFavoirte(final int movie_id){
        AppExcutor.getExecutor().getDeskIo().execute(new Runnable() {
            @Override
            public void run() {
                database.favoriteDao().deleteWithId(movie_id);
            }
        });
    }
}
