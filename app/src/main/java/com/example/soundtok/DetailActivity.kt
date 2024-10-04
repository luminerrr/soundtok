package com.example.soundtok

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.soundtok.data.DatabaseHelper
import com.example.soundtok.ui.home.SoundList

class DetailActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var playButton: Button
    private lateinit var stopButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var restriction: TextView
    private lateinit var rating: TextView
    private lateinit var duration: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        playButton = findViewById(R.id.playButton)
        stopButton = findViewById(R.id.stopButton)
        seekBar = findViewById(R.id.seekBar)
        titleTextView = findViewById(R.id.titleDetailTv)
        descriptionTextView = findViewById(R.id.descriptionTv)
        locationTextView = findViewById(R.id.locationDetailTv)
        restriction = findViewById(R.id.restrictionTv)
        rating = findViewById(R.id.ratingTv)
        duration = findViewById(R.id.durationTv)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(this)

        // Get sound ID from Intent
        val soundId = intent.getIntExtra("soundId", -1)
        val selectedSound = databaseHelper.getSoundById(soundId)

        // Set text views
        titleTextView.text = selectedSound?.title
        descriptionTextView.text = selectedSound?.description
        locationTextView.text = selectedSound?.rating
        restriction.text = selectedSound?.restriction
        rating.text = selectedSound?.rating
        duration.text = selectedSound?.duration

        // Setup MediaPlayer
        setupMediaPlayer(selectedSound?.fileUrl)

        // Set up button listeners
        playButton.setOnClickListener {
            if (!isPlaying) {
                mediaPlayer?.start()
                isPlaying = true
                startSeekBarUpdate()
            }
        }

        stopButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer?.pause()
                isPlaying = false
            }
        }

        // SeekBar listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress * 1000) // Convert to milliseconds
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupMediaPlayer(fileUrl: String?) {
        mediaPlayer = MediaPlayer().apply {
            fileUrl?.let {
                setDataSource(it)
                prepare()
                setOnCompletionListener {
                    this@DetailActivity.isPlaying = false
                    seekBar.progress = 0
                    handler.removeCallbacksAndMessages(null) // Stop updating SeekBar
                }
            }
        }

        // Set SeekBar max duration
        seekBar.max = mediaPlayer?.duration?.div(1000) ?: 0 // Convert milliseconds to seconds
    }

    private fun startSeekBarUpdate() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                mediaPlayer?.let {
                    if (isPlaying) {
                        seekBar.progress = it.currentPosition / 1000 // Convert milliseconds to seconds
                        handler.postDelayed(this, 1000) // Update every second
                    }
                }
            }
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacksAndMessages(null) // Clean up handlers
    }
}
