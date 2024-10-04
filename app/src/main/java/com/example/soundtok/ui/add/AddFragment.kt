package com.example.soundtok.ui.add

import android.content.ContentResolver
import android.content.pm.PackageManager
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
import android.widget.Toast
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        pickAudioLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {
                    println(uri)
                    Log.d("should log", it.toString())
                    saveAudioFile(it)
                    submitButton.isEnabled = true
                    // Handle the picked audio file URI
                    // For example, process the audio URI here
                }
            }

        checkPermissions()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dbHelper = DatabaseHelper(requireContext())
        // Inflate the layout for this fragment
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
        view.findViewById<Button>(R.id.recordButton).setOnClickListener {
            openFilePicker()
        }
        submitButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            val lastSavedFileName =
                "filename_you_saved.mp3" // Placeholder for the actual file name you saved
            val dateNow = Date()

            val sound =
                AddSound(title, description, dateNow, "1.5", "1.2", "4:53", "20", "5", fileUrl)
            dbHelper.insertSound(sound)
            Log.d("AddFragment", "Sound submitted: $sound")
        }

        recordBtn.setOnClickListener {
            startRecording()
            recordBtn.isEnabled = false
            stopBtn.isEnabled = true
        }

        stopBtn.setOnClickListener {
            stopRecording()
            recordBtn.isEnabled = true
            stopBtn.isEnabled = false
        }
    }

//    private fun openFilePicker() {
//        pickAudioLauncher.launch("audio/*")
//    }

    private fun openFilePicker() {
        pickAudioLauncher.launch("audio/*")
    }

    private fun saveAudioFile(uri: Uri) {
        val contentResolver: ContentResolver = requireContext().contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)

        // Specify the location where you want to save the file
        val timestamp = System.currentTimeMillis()
        val outputFileName = "$timestamp.mp3" // Change the extension based on your needs
        val outputDir = requireContext().filesDir // Use internal storage for saving files
        val outputFile = File(outputDir, outputFileName)

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
                fileUrl = outputFile.absolutePath.toString()
                Log.d("File Saved", "Audio file saved to: ${outputFile.absolutePath}")
            }
        } ?: Log.e("File Save Error", "Unable to open input stream for URI: $uri")
    }

    private val REQUEST_PERMISSION_CODE = 100

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
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
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
            Log.d("Recording", "Recording stopped")
        }
        mediaRecorder = null
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with recording
            } else {
                // Permission denied, show a message to the user
            }
        }
    }





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}