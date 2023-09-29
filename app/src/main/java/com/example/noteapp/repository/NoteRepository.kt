package com.example.noteapp.repository

import androidx.room.Dao
import com.example.noteapp.room.Note
import com.example.noteapp.room.NoteDAO


class NoteRepository ( val dao: NoteDAO){
    suspend fun insertNote(note: Note) =
        dao.insertNote(note)

    suspend fun deleteNote(note: Note)=
        dao.deleteNote(note)

    suspend fun updateNote(note: Note)=
        dao.updateNote(note)

     fun selectALlNotes() = dao.getAllNotes()

     fun searchNote(query:String?)=
        dao.searchNote(query)

}