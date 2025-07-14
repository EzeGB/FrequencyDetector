package com.example.frequencydetector

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.frequencydetector.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
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

        binding.initialController.setOnClickListener(View.OnClickListener {
            if (recording){
                binding.initialController.setBackgroundResource(R.drawable.icon_controller_off)
                binding.textFrequency.text = "Stopped"
                recording = false

            } else {
                binding.initialController.setBackgroundResource(R.drawable.icon_controller_on)
                binding.textFrequency.text = "Recording"
                recording = true
            }
        })
    }
}