package com.example.qr_scan_app

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
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
        val infoId = findViewById<TextView>(R.id.ladunekId)
        val reset = findViewById<Button>(R.id.button)
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
            CurrentLoad.clear()
             inte = Intent(this,MainActivity::class.java)
            DatabaseHelper().closeConnection()
            startActivity(inte)

        }
        btn_weryfi.setOnClickListener{
            inte = Intent(this,TablePreviewActivity::class.java)//podglad
            inte.putExtra("ladunek",1)
            startActivity(inte)
        }
        reset.setOnClickListener {
            CurrentLoad.clear()
        }
        val timer = object : CountDownTimer(3600000, 1000) { // 100000 seconds, tick every 1 second
            override fun onTick(millisUntilFinished: Long) {
                // Update the UI with the remaining time
                if(CurrentLoad.getID()!=-1){
                    reset.visibility =View.VISIBLE
                    infoId.text = "Ładunek numer: ${CurrentLoad.getID()}"
                }else{
                    reset.visibility = View.GONE
                    infoId.text = getString(R.string.infoLadunek)
                }
            }

            override fun onFinish() {
                // Handle when the timer finishes
                btn_wylog.performClick()
            }
        }
        timer.start()
    }

}