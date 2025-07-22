package com.example.frequencydetector

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import be.hogent.tarsos.dsp.MicrophoneAudioDispatcher
import be.hogent.tarsos.dsp.pitch.PitchDetectionHandler
import be.hogent.tarsos.dsp.pitch.PitchProcessor
import com.example.frequencydetector.databinding.ActivityMainBinding

const val REQUEST_CODE = 200
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private var recordAudioPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var recordAudioPermissionsGranted = false

    private val audioThread = setUpPitchDetectionAudioThread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recordAudioPermissionsGranted = ActivityCompat.checkSelfPermission(this,recordAudioPermissions[0])==PackageManager.PERMISSION_GRANTED
        checkForPermissions()

        audioThread.start()
    }

    private fun setUpPitchDetectionAudioThread () : Thread {
        val dispatcher = MicrophoneAudioDispatcher(22050, 1024, 0)

        val pdHandler = PitchDetectionHandler { pitchDetectionResult, audioEvent ->
            val pitchInHz = pitchDetectionResult.pitch
            runOnUiThread{
                updatePitchText(pitchInHz)
            }
        }

        val pitchProcessor = PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
            22050F,1024,pdHandler)

        dispatcher.addAudioProcessor(pitchProcessor)

        return Thread(dispatcher,"Audio Thread")
    }

    private fun updatePitchText(pitchInHz: Float){
        if (pitchInHz.toString()  == "-1.0")
            binding.textFrequency.text = ". . ."
        else
            binding.textFrequency.text = String.format("%.2f",pitchInHz)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE)
            recordAudioPermissionsGranted = grantResults[0]==PackageManager.PERMISSION_GRANTED
    }

    private fun checkForPermissions(): Boolean {
        if (!recordAudioPermissionsGranted)
            ActivityCompat.requestPermissions(this,recordAudioPermissions, REQUEST_CODE)
        return recordAudioPermissionsGranted
    }
}