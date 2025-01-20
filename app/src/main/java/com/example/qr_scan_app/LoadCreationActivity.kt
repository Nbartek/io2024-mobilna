package com.example.qr_scan_app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoadCreationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_load_creation)
        val btn__accpt = findViewById<Button>(R.id.akceptacja)
        val btn_odmowa = findViewById<Button>(R.id.odmowa)
        val tekst_warning = findViewById<TextView>(R.id.ostrzerzenie)
        val tekst_pytanie = findViewById<TextView>(R.id.pytanie)
        val mode = intent.getIntExtra("tworzenie",1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if(mode!=1){
            tekst_warning.setVisibility(View.INVISIBLE)
            tekst_pytanie.text = getString(R.string.pytanie_Otw)
            btn__accpt.text = getString(R.string.akcep_Otw)
        }
    }
}