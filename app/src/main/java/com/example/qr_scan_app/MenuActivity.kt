package com.example.qr_scan_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val usr = ActiveUser
        supportActionBar?.title = "Zalogowano: ${usr.getName()} ${usr.getSurname()}"
        val btn_zaladSkan = findViewById<Button>(R.id.zaladunek)
        val btn_rozladSkan = findViewById<Button>(R.id.rozladunke)
        val btn_uszkodzon = findViewById<Button>(R.id.stanPaczki)
        val btn_weryfi = findViewById<Button>(R.id.wer)
        val btn_wylog = findViewById<Button>(R.id.wylog)
        val btn_podglad = findViewById<Button>(R.id.podglad)
        var inte = Intent(this,SkanerActivity::class.java)
        btn_zaladSkan.setOnClickListener{
            inte = Intent(this,SkanerActivity::class.java)
            inte.putExtra("sqlInputMode",0)//skanowanie
            startActivity(inte)
        }
        btn_rozladSkan.setOnClickListener{
            inte = Intent(this,SkanerActivity::class.java)
            inte.putExtra("sqlInputMode",1)//rozladnowanie
            startActivity(inte)
        }
        btn_podglad.setOnClickListener{
            inte = Intent(this,TablePreviewActivity::class.java)//podglad
            startActivity(inte)
        }
        btn_uszkodzon.setOnClickListener{
            inte = Intent(this,SkanerActivity::class.java)
            inte.putExtra("sqlInputMode",-1)//uszkodzeni paczek
            startActivity(inte)
        }
        btn_wylog.setOnClickListener{
            DBconnectionExposed("","")
             inte = Intent(this,MainActivity::class.java)
            startActivity(inte)

        }
        btn_weryfi.setOnClickListener{

        }
    }
}