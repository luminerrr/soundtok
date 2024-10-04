package com.example.soundtok

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.soundtok.data.DatabaseHelper

class DetailActivity : AppCompatActivity() {
    private val databaseHelper: DatabaseHelper = DatabaseHelper(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val soundId = intent.getIntExtra("soundId", 0)
        val selectedSound = databaseHelper.getSoundById(soundId)
        findViewById<TextView>(R.id.titleDetailTv).text = selectedSound?.title
        findViewById<TextView>(R.id.durationDetailTv).text = selectedSound?.duration
        findViewById<TextView>(R.id.locationDetailTv).text = selectedSound?.rating

    }
}