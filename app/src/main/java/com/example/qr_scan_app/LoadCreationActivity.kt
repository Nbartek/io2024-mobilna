package com.example.qr_scan_app

import DatabaseHelper
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class LoadCreationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_load_creation)
        val btn__accpt = findViewById<Button>(R.id.akceptacja)
        val btn_odmowa = findViewById<Button>(R.id.odmowa)
        val tekst_warning = findViewById<TextView>(R.id.ostrzerzenie)
        val tekst_pytanie = findViewById<TextView>(R.id.pytanie)
        var mode = intent.getIntExtra("tworzenie",1)
        //var code = intent.getIntExtra("skan",-1)
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
        btn_odmowa.setOnClickListener{
            finish()
        }
        btn__accpt.setOnClickListener{
            val code = intent.getIntExtra("skan",-1)
            val db = DatabaseHelper()
            val query ="INSERT INTO Ladunek (Id_Samochod,[Status])VALUES (1,'Stworzono');"
            lifecycleScope.launch {
                if(db.executeUpdate(query)==1){
                    if(code!=-1){
                        val query1 = "Select Id_Ladunek from dbo.Ladunek order by Id_Ladunek"
                        val res = db.executeQuery(query1);
                        CurrentLoad.addScan(code)
                        val ade = res?.last()?.get("Id_Ladunek").toString().toInt()
                        CurrentLoad.setCurrentLoad(ade)
                    }
                    finish()
                }else{
                    runOnUiThread{
                        Toast.makeText(baseContext, "Nie powiodło się, spróbuj później", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}