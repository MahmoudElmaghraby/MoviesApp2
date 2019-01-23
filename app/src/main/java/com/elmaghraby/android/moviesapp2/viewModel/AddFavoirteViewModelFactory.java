package com.elmaghraby.android.moviesapp2.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.elmaghraby.android.moviesapp2.livedata.AppDatabase;

public class AddFavoirteViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase appDatabase;
    private final int id;

    public AddFavoirteViewModelFactory(AppDatabase appDatabase, int id) {
        this.appDatabase = appDatabase;
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T)new AddFavoriteViewModel(appDatabase,id);
    }
}
