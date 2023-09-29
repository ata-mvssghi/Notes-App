package com.example.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.room.NoteDatabase
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.viewModel.NoteViewModelFactory


class MainActivity : AppCompatActivity() {
    lateinit var noteViewModel: NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }
    fun setup(){
        val db=NoteDatabase(this)
        val repository= NoteRepository(db.dao)
        val factory= NoteViewModelFactory(application,repository)
        noteViewModel=ViewModelProvider(
            this,factory
        ).get(NoteViewModel::class.java)
    }
}