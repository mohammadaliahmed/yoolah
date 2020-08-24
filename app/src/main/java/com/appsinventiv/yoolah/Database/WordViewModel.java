package com.appsinventiv.yoolah.Database;

import android.app.Application;


import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WordViewModel extends AndroidViewModel {
    private WordRepository mRepository;
    private LiveData<List<Word>> mAllWords;
    private MutableLiveData<Long> mLastWordInserted;

    public WordViewModel(@NonNull Application application) {
        super(application);
        mRepository = new WordRepository(application);
//
        mLastWordInserted = mRepository.getLastWordInserted();
    }

    LiveData<Long> getLastWordInserted() {
        return mLastWordInserted;
    }

    public LiveData<List<Word>> getAllWords(Integer roomId) {
        mAllWords = mRepository.getAllWords(roomId);
        return mAllWords;
    }

    public LiveData<List<Word>> getUserWords() {
        mAllWords = mRepository.getUserWords();
        return mAllWords;
    }

    public void insert(Word word) {
        mRepository.insert(word);
    }

    public void deleteAll() {
        mRepository.deleteAll();
    }

    public void deleteWord(Word word) {
        mRepository.deleteWord(word);
    }

    public void updateWord(Word word) {
        mRepository.updateWord(word);
    }
}
