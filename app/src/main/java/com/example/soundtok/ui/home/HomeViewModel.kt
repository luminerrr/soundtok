package com.example.soundtok.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soundtok.data.DatabaseHelper

class HomeViewModel(private val databaseHelper: DatabaseHelper) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text

    private val _sounds = MutableLiveData<List<SoundList>>()
    val sounds = _sounds

    init {
        loadSounds()
    }

    private fun loadSounds() {
        _sounds.value = databaseHelper.getAllSounds()
    }
}