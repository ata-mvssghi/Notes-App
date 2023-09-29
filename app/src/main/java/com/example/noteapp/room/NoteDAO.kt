package com.example.noteapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertNote(note: Note):Long

    @Update
     fun updateNote(note: Note)

    @Delete
     fun deleteNote(note: Note)

    @Query("SELECT * FROM NOTES ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM NOTES WHERE noteTitle LIKE '%' || :query || '%' OR noteBody LIKE '%' || :query || '%'")
    fun searchNote(query: String?): LiveData<List<Note>>

}