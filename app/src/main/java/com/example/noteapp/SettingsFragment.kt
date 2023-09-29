package com.example.noteapp

import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.noteapp.SettingsFragment.ThemeChangingInstance.themeChangeListener

class SettingsFragment : PreferenceFragmentCompat() {
    var previousTheme:String?="init"
    var previousFont:String?="init"
    companion object {
        var fontScaleChangedListener: FontScaleChangedListener? = null
    }
    object ThemeChangingInstance{
        var themeChangeListener: OnThemeChangeListener? = null
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        previousTheme=sp.getString("color_theme","Light")
        previousFont=sp.getString("text_size","Medium")
        val backButtonPreference: Preference? = findPreference("backButton")

        backButtonPreference?.setOnPreferenceClickListener {
            Log.i("note","back button clicked")
           findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sp =PreferenceManager.getDefaultSharedPreferences(requireContext())
        val selectedTextSizePreference = sp.getString("text_size", "Medium")
        val selectedTheme:String?=sp.getString("color_theme","Light")

        if (selectedTextSizePreference != null&&selectedTextSizePreference!=previousFont) {
            onTextSizePreferenceChanged(selectedTextSizePreference)
        }
        if(selectedTheme!=null&&selectedTheme!=previousTheme){
            themeChangeListener?.onThemeChanged(selectedTheme)
            Log.i("remote","theme is cahnged and changed theme is =$selectedTheme")
        }
    }
    fun onTextSizePreferenceChanged(selectedTextSizePreference: String) {
        // Save selectedTextSizePreference to SharedPreferences
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        sp.edit().putString("text_size", selectedTextSizePreference).apply()

        Log.i("remote","passed data = $selectedTextSizePreference")
        // Notify MainActivity to update font scale
        fontScaleChangedListener?.onFontScaleChanged(selectedTextSizePreference)
    }
}

interface FontScaleChangedListener {
    fun onFontScaleChanged(selectedTextSizePreference: String)
}
interface OnThemeChangeListener {
    fun onThemeChanged(selectedThemeString:String?)
}