package com.example.qr_scan_app

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
        val e_connect = findViewById<TextView>(R.id.login_failed)
        val inte = Intent(this,MenuActivity::class.java)
        button_login.setOnClickListener{
            login_text= findViewById<EditText>(R.id.username).text.toString()
            password_text = findViewById<EditText>(R.id.password).text.toString()
            //Do testów
            val dbt = DatabaseHelper("stebat10","Stebat@10");
            startActivity(inte)
            //
             if(!login_text.contains(Regex("^[a-zA-Z]{6}[0-9]{2}$"))){
                e_login.setTextColor(getColor(R.color.czerwony_blad))
                e_connect.setTextColor(getColor(R.color.jasny_niebieski))
                e_password.setTextColor(getColor(R.color.jasny_niebieski))
            }else if(password_text.length<=6){
                e_password.setTextColor(getColor(R.color.czerwony_blad))
                e_login.setTextColor(getColor(R.color.jasny_niebieski))
                e_connect.setTextColor(getColor(R.color.jasny_niebieski))
            }
            else{
                val db = DatabaseHelper(login_text,password_text)
                lifecycleScope.launch {

                    if(db.isConnected()){

                        startActivity(inte);
                    }else{
                        e_password.setTextColor(getColor(R.color.jasny_niebieski))
                        e_login.setTextColor(getColor(R.color.jasny_niebieski))
                        e_connect.setTextColor(getColor(R.color.czerwony_blad))
                    }
                }

            }
        }




    }
}