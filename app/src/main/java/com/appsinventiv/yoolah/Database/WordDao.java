package com.appsinventiv.yoolah.Database;


import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WordDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Word word);

    @Query("DELETE FROM word_table")
    void deleteAll();

    @Query("SELECT * FROM word_table where roomId=:room_id")
    LiveData<List<Word>> getAllWords(int room_id);

    @Query("select * from word_table where `id` in(select max(`id`) from word_table GROUP by `roomId`) order by time desc ")
    LiveData<List<Word>> getUserWords();

    @Query("SELECT * FROM word_table LIMIT 1")
    Word[] getAnyWord();

    @Delete
    void deleteWord(Word word);

    @Update
    void update(Word... word);
}
