package com.example.soundtok.ui.add

import android.content.ContentResolver
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.soundtok.R
import com.example.soundtok.data.DatabaseHelper
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Date
import android.media.MediaRecorder
import android.widget.CheckBox
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.soundtok.helpers.DurationHelper

class AddFragment : Fragment() {
    private lateinit var fileUrl: String
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var pickAudioLauncher: ActivityResultLauncher<String>
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var recordBtn: Button
    private lateinit var stopBtn: Button
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null

    //    Helper
    private val durationHelper = DurationHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pickAudioLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    saveAudioFile(it)
                    submitButton.isEnabled = true
                }
            }

        checkPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dbHelper = DatabaseHelper(requireContext())
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleEditText = view.findViewById(R.id.editTextText)
        descriptionEditText = view.findViewById(R.id.descriptionAddEt)
        submitButton = view.findViewById(R.id.submitBtn)
        recordBtn = view.findViewById(R.id.recordButton)
        stopBtn = view.findViewById(R.id.stopButton)

        view.findViewById<Button>(R.id.uploadButton).setOnClickListener {
            openFilePicker()
        }

        submitButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val dateNow = Date()
            val durationMillis = durationHelper.getAudioDuration(fileUrl)
            val durationString = durationHelper.formatDuration(durationMillis / 1000)
            val isSafeForKids = view.findViewById<CheckBox>(R.id.isSaveCheckbox).isChecked
            val sound =
                AddSound(
                    title,
                    description,
                    dateNow,
                    "1.5",
                    "1.2",
                    durationString,
                    if (isSafeForKids) "Safe" else "18+",
                    "5",
                    fileUrl
                )
            dbHelper.insertSound(sound)
            Log.d("AddFragment", "Sound submitted: $sound")
            findNavController().navigate(R.id.navigation_home)
        }

        recordBtn.setOnClickListener {
            startRecording()
            recordBtn.isEnabled = false
            stopBtn.isEnabled = true
        }

        stopBtn.setOnClickListener {
            stopRecording()
//            recordBtn.isEnabled = true
            stopBtn.isEnabled = false
            submitButton.isEnabled = true
        }
    }

    private fun openFilePicker() {
        pickAudioLauncher.launch("audio/*")
    }

    private fun saveAudioFile(uri: Uri) {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        val timestamp = System.currentTimeMillis()
        val outputFileName = "$timestamp.mp3"
        val outputDir = requireContext().filesDir
        val outputFile = File(outputDir, outputFileName)

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
                fileUrl = outputFile.absolutePath
                Log.d("File Saved", "Audio file saved to: ${outputFile.absolutePath}")
            }
        } ?: Log.e("File Save Error", "Unable to open input stream for URI: $uri")
    }

    private val REQUEST_PERMISSION_CODE = 100

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    private fun startRecording() {
        audioFile = File(requireContext().externalCacheDir, "${System.currentTimeMillis()}.wav")
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFile?.absolutePath)

            try {
                prepare()
                start()
                Log.d("Recording", "Recording started")
            } catch (e: Exception) {
                Log.e("Recording Error", "Error starting recording: ${e.message}")
                Toast.makeText(
                    requireContext(),
                    "Recording failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                Log.d("Recording", "Recording stopped")
                fileUrl = audioFile?.absolutePath.toString() // Save the file URL for future use
            } catch (e: Exception) {
                Log.e("Recording Error", "Error stopping recording: ${e.message}")
                Toast.makeText(
                    requireContext(),
                    "Stopping recording failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                release()
                mediaRecorder = null
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with recording
            } else {
                // Permission denied, show a message to the user
                Toast.makeText(
                    requireContext(),
                    "Permissions required for recording audio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
