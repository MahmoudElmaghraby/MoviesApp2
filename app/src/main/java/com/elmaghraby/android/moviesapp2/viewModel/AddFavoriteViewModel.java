package com.elmaghraby.android.moviesapp2.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.elmaghraby.android.moviesapp2.livedata.AppDatabase;
import com.elmaghraby.android.moviesapp2.livedata.FavoriteEntry;

public class AddFavoriteViewModel extends ViewModel {

    private LiveData<FavoriteEntry> listLiveData;

    public AddFavoriteViewModel(AppDatabase database ,int id) {
        listLiveData=database.favoriteDao().loadFavoriteById(id);
    }

    public LiveData<FavoriteEntry> getListLiveData() {
        return listLiveData;
    }
}
