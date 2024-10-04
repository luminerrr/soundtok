package com.example.soundtok.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.soundtok.ui.add.AddSound
import com.example.soundtok.ui.home.SoundList

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "soundtok.db"
        private const val DATABASE_VERSION = 2 // Incremented version
        const val TABLE_NAME = "sounds"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_TIME = "time" // Store as timestamp
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_AGE_RESTRICTION = "age_restriction"
        const val COLUMN_RATING = "rating"
        const val COLUMN_FILE_URL = "file_url" // New column for file URL
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_TIME INTEGER NOT NULL,
                $COLUMN_LATITUDE TEXT NOT NULL,
                $COLUMN_LONGITUDE TEXT NOT NULL,
                $COLUMN_DURATION TEXT NOT NULL,
                $COLUMN_AGE_RESTRICTION TEXT NOT NULL,
                $COLUMN_RATING TEXT NOT NULL,
                $COLUMN_FILE_URL TEXT NOT NULL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Insert sound
    fun insertSound(sound: AddSound): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, sound.title)
            put(COLUMN_DESCRIPTION, sound.description)
            put(COLUMN_TIME, sound.time.time) // Convert Date to Long
            put(COLUMN_LATITUDE, sound.latitude)
            put(COLUMN_LONGITUDE, sound.longitude)
            put(COLUMN_DURATION, sound.duration)
            put(COLUMN_AGE_RESTRICTION, sound.ageRestriction)
            put(COLUMN_RATING, sound.rating)
            put(COLUMN_FILE_URL, sound.fileUrl) // Store file URL
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    // Get all sounds
    @SuppressLint("Range")
    fun getAllSounds(): List<SoundList> {
        val sounds = mutableListOf<SoundList>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val sound = SoundList(
                        id = it.getInt(it.getColumnIndex(COLUMN_ID)),
                        title = it.getString(it.getColumnIndex(COLUMN_TITLE)),
                        duration = it.getString(it.getColumnIndex(COLUMN_DURATION)),
                        rating = it.getString(it.getColumnIndex(COLUMN_RATING)),
                        description = it.getString(it.getColumnIndex(COLUMN_DESCRIPTION)),
                        fileUrl = it.getString(it.getColumnIndex(COLUMN_FILE_URL)),
                        restriction = it.getString(it.getColumnIndex(COLUMN_AGE_RESTRICTION))
                    )
                    sounds.add(sound)
                } while (it.moveToNext())
            }
        }
        db.close()
        return sounds
    }

    // Get sound by ID
    @SuppressLint("Range")
    fun getSoundById(id: Int): SoundList? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var sound: SoundList? = null
        cursor.use {
            if (it.moveToFirst()) {
                sound = SoundList(
                    id = it.getInt(it.getColumnIndex(COLUMN_ID)),
                    title = it.getString(it.getColumnIndex(COLUMN_TITLE)),
                    duration = it.getString(it.getColumnIndex(COLUMN_DURATION)),
                    rating = it.getString(it.getColumnIndex(COLUMN_RATING)),
                    description = it.getString(it.getColumnIndex(COLUMN_DESCRIPTION)),
                    fileUrl = it.getString(it.getColumnIndex(COLUMN_FILE_URL)),
                    restriction = it.getString(it.getColumnIndex(COLUMN_AGE_RESTRICTION))
                )
            }
        }
        db.close()
        return sound
    }

}
