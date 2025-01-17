package com.example.qr_scan_app

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
        val userList = listOf(
            Pair("Jan","Kronos"),
            Pair("Foran","nos"),
            Pair("Koran","ja"),
            Pair("Gaz","uuuu"),
            Pair("Franz","fsdgsgf"),
            Pair("Fortran","sfdsf")
        )
        // Setting up the adapter and layout manager
        userAdapter = RecycleAdapter(userList)
        recyclerView.layoutManager = LinearLayoutManager(this) // Use GridLayoutManager for a grid
        recyclerView.adapter = userAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }


}