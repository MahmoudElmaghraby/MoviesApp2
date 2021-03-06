package com.elmaghraby.android.moviesapp2.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.elmaghraby.android.moviesapp2.livedata.AppDatabase;
import com.elmaghraby.android.moviesapp2.livedata.FavoriteEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAGLOG=MainViewModel.class.getSimpleName();

    LiveData<List<FavoriteEntry>>liveData;
    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase=AppDatabase.getData(getApplication());
        Log.d(TAGLOG,"Activity retrieving favorite from database");
        liveData=appDatabase.favoriteDao().loadAllFavoirte();
    }

    public LiveData<List<FavoriteEntry>> getLiveData() {
        return liveData;
    }
}
