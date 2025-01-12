package com.example.qr_scan_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.jetbrains.exposed.sql.*


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)
        val button_login = findViewById<Button>(R.id.login)
        var login_text:String;
        var password_text:String
        val e_login = findViewById<TextView>(R.id.error_login)
        val e_password = findViewById<TextView>(R.id.error_password)
        button_login.setOnClickListener{
            login_text= findViewById<EditText>(R.id.username).text.toString()
            password_text = findViewById<EditText>(R.id.password).text.toString()
            if(!login_text.toString().contains(Regex("[A-z]{6}[0-9]{3}"))){
                e_login.setTextColor(getColor(R.color.czerwony_blad))
                e_password.setTextColor(getColor(R.color.jasny_niebieski))
            }else if(password_text.length<=6){
                e_password.setTextColor(getColor(R.color.czerwony_blad))
                e_login.setTextColor(getColor(R.color.jasny_niebieski))
            }
            else{
                Database.connect("io-poczta.database.windows.net")
                ///TODO implementacja loginu i połączenia z bazą danych
                val inte = Intent(this,SkanerActivity::class.java)
                //val inte = Intent(this,CameraTest::class.java)
            startActivity(inte)
            }
        }




    }
}