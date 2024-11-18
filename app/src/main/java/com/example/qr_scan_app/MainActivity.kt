package com.example.qr_scan_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)
        val button_login = findViewById<Button>(R.id.login)
        val login_text = findViewById<EditText>(R.id.username).text.toString()
        val password_text = findViewById<EditText>(R.id.password).text.toString()
        val e_login = findViewById<TextView>(R.id.error_login)
        val e_password = findViewById<TextView>(R.id.error_password)
        button_login.setOnClickListener(View.OnClickListener {
            if(login_text.length<=5){
                e_login.setTextColor(getColor(R.color.czerwony_blad))
            }else if(password_text.length<=5) e_password.setTextColor(getColor(R.color.czerwony_blad))
            else{

            }
        })




    }
}