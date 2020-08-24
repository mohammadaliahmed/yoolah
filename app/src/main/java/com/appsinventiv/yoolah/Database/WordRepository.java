package com.appsinventiv.yoolah.Database;

import android.app.Application;

import android.os.AsyncTask;

import com.appsinventiv.yoolah.Utils.SharedPrefs;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WordRepository {
    private WordDao mWordDao;
    private LiveData<List<Word>> mAllWords;
    private MutableLiveData<Long> mLastWordInserted;

    public WordRepository(Application application) {
        WordRoomDatabase db = WordRoomDatabase.getDatabase(application);
        mWordDao = db.wordDao();
//        mAllWords = mWordDao.getAllWords();
        mLastWordInserted = new MutableLiveData<Long>();
    }

    public MutableLiveData<Long> getLastWordInserted() {
        return mLastWordInserted;
    }

    public LiveData<List<Word>> getAllWords(Integer roomId) {
        mAllWords = mWordDao.getAllWords(roomId);
        return mAllWords;
    }

    public LiveData<List<Word>> getUserWords() {
        mAllWords = mWordDao.getUserWords();
        return mAllWords;
    }

    public void insert(Word word) {
        new insertAsyncTask(mWordDao, mLastWordInserted).execute(word);
    }

    public void deleteAll() {
        new deleteAllWordsAsyncTask(mWordDao).execute();
    }

    public void deleteWord(Word word) {
        new deleteWordAsyncTask(mWordDao).execute(word);
    }

    public void updateWord(Word word) {
        new updateWordAsyncTask(mWordDao).execute(word);
    }

    // ASYNCTASK PARA INSERTAR UNA PALABRA A LA BD.
    private static class insertAsyncTask extends AsyncTask<Word, Void, Void> {

        private WordDao mAsyncTaskDao;
        private MutableLiveData<Long> mAsyncLastWordInserted;

        insertAsyncTask(WordDao dao, MutableLiveData<Long> mLastWordInserted) {
            mAsyncTaskDao = dao;
            mAsyncLastWordInserted = mLastWordInserted;
        }

        @Override
        protected Void doInBackground(final Word... params) {
            long id = mAsyncTaskDao.insert(params[0]);
            mAsyncLastWordInserted.postValue(id);
            return null;
        }
    }

    // ASYNCTASK PARA BORRAR TODAS LAS PALABRAS EN LA BD.
    private static class deleteAllWordsAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao mAsyncTaskDao;

        deleteAllWordsAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    // ASYNCTASK PARA BORRAR UNA PALABRA DE LA BD.
    private static class deleteWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mAsyncTaskDao;

        deleteWordAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Word... params) {
            mAsyncTaskDao.deleteWord(params[0]);
            return null;
        }
    }

    // ASYNCTASK PARA ACTUALIZAR UNA PALABRA DE LA BD.
    private static class updateWordAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao mAsyncTaskDao;

        updateWordAsyncTask(WordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Word... params) {
            mAsyncTaskDao.update(params[0]);
            return null;
        }
    }

}
