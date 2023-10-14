package com.example.noteapp.viewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.room.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(
    val app:Application,
    val noteRepository: NoteRepository
): AndroidViewModel(app){
    fun insertNote(note: Note) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                noteRepository.insertNote(note)
            }
        }
    }

    fun updateNote(note: Note){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                noteRepository.updateNote(note)
            }
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                noteRepository.deleteNote(note)
            }
        }
    }

    fun getALlNotes()=noteRepository.selectALlNotes()
    fun deleteAllNotes() = noteRepository.deleteAllNotes()
    fun searchNote(query:String?)=
        noteRepository.searchNote(query)

}