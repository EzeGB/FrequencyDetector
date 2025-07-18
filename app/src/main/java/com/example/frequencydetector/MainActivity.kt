package com.example.frequencydetector

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.HandlerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.frequencydetector.databinding.ActivityMainBinding
import java.io.IOException
import java.lang.Exception

const val REQUEST_CODE = 200
class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    private var recordAudioPermissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private var recordAudioPermissionsGranted = false

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var mediaRecorder: MediaRecorder

    private var recording = false
    private lateinit var dirPath : String
    private var filename = ""

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

        handler = HandlerCompat.createAsync(mainLooper)
        runnable = Runnable { runCoreLoop() }
        dirPath = "${externalCacheDir?.absolutePath}/"

        binding.initialController.setOnClickListener(View.OnClickListener {
            if (checkForPermissions()){
                if (recording){
                    stopRecording()
                } else {
                    startRecording()
                }
            }
        })
    }

    private fun startRecording(){
        binding.initialController.setBackgroundResource(R.drawable.icon_controller_on)
        binding.textFrequency.text = filename
        handler.postDelayed(runnable,0L)
    }

    private fun stopRecording(){
        binding.initialController.setBackgroundResource(R.drawable.icon_controller_off)
        handler.removeCallbacks(runnable)
        tryToStopMediaRecorder()
        recording = false
        binding.textFrequency.text = "Stopped"
    }

    private fun runCoreLoop (){
        if (recording){
            tryToStopMediaRecorder()
            recording = false
        }
        setUpMediaRecorder()
        mediaRecorder.apply {
            setOutputFile("$dirPath${renameFilename()}.mp3")
            try {
                prepare()
            }catch (e:IOException){}
            start()
            recording = true
        }
        binding.textFrequency.text = "Recording: ".plus(filename)
        handler.postDelayed(runnable,1000L)
    }

    private fun setUpMediaRecorder(){
        mediaRecorder = MediaRecorder()
        mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(320000)
        }
    }

    private fun renameFilename() : String{
        if (filename==""||filename=="TempXXXX"){
            filename = "Temp"
        }else{
            filename = filename.plus("X")
        }
        return filename
    }

    private fun tryToStopMediaRecorder(){
        try {
            mediaRecorder.stop()
            mediaRecorder.release()
        }catch (e:Exception){
            Log.d("Debug4","already stopped")
        }
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