package com.example.qr_scan_app

import DatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TablePreviewActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: RecycleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_table_preview)
        recyclerView = findViewById(R.id.recycler)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = DatabaseHelper()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Fetch data from the database
                val mode  = intent.getIntExtra("ladunek",0)
                var result: List<Map<String, Any>>?
                if(mode==0){
                 result = db.executeQuery("SELECT Id_Paczki, Status, czyZniszczona FROM dbo.Paczki")
                }else{
                    result = db.executeQuery("SELECT \n" +
                            "    p.Id_Paczki, \n" +
                            "    p.Status, \n" +
                            "    p.czyZniszczona\n" +
                            "FROM \n" +
                            "    dbo.Paczki p\n" +
                            "JOIN \n" +
                            "    dbo.Paczka_Ladunek l\n" +
                            "ON \n" +
                            "    p.Id_Paczki = l.Id_Paczki\n" +
                            "WHERE \n" +
                            "    l.Id_Ladunku = ${CurrentLoad.getID()};")
                }

                // Switch to Main thread to update UI
                withContext(Dispatchers.Main) {
                    // Set up RecyclerView adapter with fetched data
                    userAdapter = RecycleAdapter(result ?: emptyList())
                    recyclerView.layoutManager = LinearLayoutManager(this@TablePreviewActivity)
                    recyclerView.adapter = userAdapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Handle any errors here, e.g., show a Toast
                    showError("Failed to load data")
                }
            }
        }

        // Setting up the adapter and layout manager
    }
    private fun showError(message: String) {
        // Show an error message (e.g., a Toast or Snackbar)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        finish()
    }


}