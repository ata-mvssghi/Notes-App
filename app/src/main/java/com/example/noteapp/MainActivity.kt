package com.example.noteapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.noteapp.repository.NoteRepository
import com.example.noteapp.room.NoteDatabase
import com.example.noteapp.viewModel.NoteViewModel
import com.example.noteapp.viewModel.NoteViewModelFactory


class MainActivity : AppCompatActivity(), FontScaleChangedListener, OnThemeChangeListener {
    lateinit var noteViewModel: NoteViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themePreference = preferences.getString("color_theme","Light") // Default to light theme
        val fontPreference=preferences.getString("text_size","Medium")//Medium as def
        themeSetting(themePreference)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SettingsFragment.fontScaleChangedListener = this
        SettingsFragment.ThemeChangingInstance.themeChangeListener=this
        if (fontPreference != null) {
            applyFontScaleConfiguration(this ,fontPreference)
       }
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
    fun applyFontScaleConfiguration(context: Context, selectedTextSizePreference: String) {
        val configuration = context.resources.configuration
        configuration.fontScale = when (selectedTextSizePreference) {
            "Very small"->0.5f
            "Small" -> 0.7f // Adjust as needed
            "Medium" -> 1f // Default font scale
            "Large" -> 1.1f // Adjust as needed
            "Very large"->1.5f
            else -> 1.0f // Default font scale
        }
        Log.i("remote","dddddddddddddddddddddddddddddddd")

        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }
    fun themeSetting(selectedTheme:String?){
        when (selectedTheme){
            "Light"->{
                setTheme(R.style.LightMode)
            }
            "Dark"->{
                setTheme(R.style.DarkMode)
            }
            "SkyBlue"->{
                setTheme(R.style.SkyBlue)
            }
            "Colorful"->{
                setTheme(R.style.Colorful)
            }
            else->{
                setTheme(R.style.LightMode)
            }
        }
    }

    override fun onThemeChanged(selectedThemeString: String?) {
        recreate()
        Log.i("remote","theme change called in main")
    }

    override fun onFontScaleChanged(selectedTextSizePreference: String) {
        recreate()
    }

}