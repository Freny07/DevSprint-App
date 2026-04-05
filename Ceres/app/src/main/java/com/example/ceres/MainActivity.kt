package com.example.ceres

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mainLayout = findViewById<ConstraintLayout>(R.id.main)
        val firstScreen = findViewById<LinearLayout>(R.id.firstScreen)
        val secondScreen = findViewById<LinearLayout>(R.id.secondScreen)
        val btnGenerate = findViewById<Button>(R.id.btnGenerate)

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnGenerate.setOnClickListener {
            // Change background to bg2
            mainLayout.setBackgroundResource(R.drawable.bg2)
            
            // Hide the first screen content
            firstScreen.visibility = View.GONE
            
            // Show the second screen content
            secondScreen.visibility = View.VISIBLE
        }
    }
}