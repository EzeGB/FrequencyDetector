package com.example.frequencydetector

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.frequencydetector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    private var recording = false

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

        handler = HandlerCompat.createAsync(mainLooper)
        runnable = Runnable {
            if (binding.textFrequency.text == "Recording..."){
                binding.textFrequency.text = "Recording"
            } else {
                binding.textFrequency.apply {
                    text = "$text."
                }
            }
            handler.postDelayed(runnable,1000L)
        }

        binding.initialController.setOnClickListener(View.OnClickListener {
            if (recording){
                binding.initialController.setBackgroundResource(R.drawable.icon_controller_off)
                handler.removeCallbacks(runnable)
                binding.textFrequency.text = "Stopped"
                recording = false

            } else {
                binding.initialController.setBackgroundResource(R.drawable.icon_controller_on)
                binding.textFrequency.text = "Recording"
                handler.postDelayed(runnable,1000L)
                recording = true
            }
        })
    }
}