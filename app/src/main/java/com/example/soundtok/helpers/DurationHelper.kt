package com.example.soundtok.helpers

import android.media.MediaMetadataRetriever
import android.util.Log

class DurationHelper {
    public fun getAudioDuration(filePath: String): Long {
        val mmr = MediaMetadataRetriever()
        return try {
            mmr.setDataSource(filePath)
            val durationString = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationString?.toLong() ?: 0L // Return duration in milliseconds
        } catch (e: Exception) {
            Log.e("Audio Duration Error", "Error retrieving duration: ${e.message}")
            0L // Return 0 if there's an error
        } finally {
            mmr.release()
        }
    }

    public fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, secs) // hh:mm:ss format
        } else {
            String.format("%02d:%02d", minutes, secs) // mm:ss format
        }
    }
}