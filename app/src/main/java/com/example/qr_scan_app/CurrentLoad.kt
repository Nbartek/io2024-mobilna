package com.example.qr_scan_app

import DatabaseHelper
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.LinkedList

object CurrentLoad {
    private lateinit var spisPaczek:MutableList<Int>
    private var zeskanowane:MutableList<Int> = mutableListOf<Int>()
    private var id:Int = -1
    suspend fun setCurrentLoad(id:Int){
        this.id =id
        val db = DatabaseHelper()
        val query = "select Id_Paczki from dbo.Paczka_Ladunek where Id_Ladunku = ${id}"
        val result = db.executeQuery(query)
        spisPaczek = mutableListOf<Int>()


        // Check if result is not null, then iterate over it
        if (!result.isNullOrEmpty()) {
            result.forEach { row ->
                // Extract the value for "Id_Paczki" and add it to spisPaczek
                val idPaczki = row["Id_Paczki"] as? Int
                if (idPaczki != null) {
                    spisPaczek.add(idPaczki)
                }
            }
        }else{
            val query2 = "INSERT into Paczka_Ladunek (Id_Ladunku,Id_Paczki)values(${id},${zeskanowane.get(0)})"
            val query3 = "UPDATE dbo.Paczki SET Status='Załadowano' WHERE Id_Paczki = ${zeskanowane.get(0)};"
            db.executeUpdate(query2);
            db.executeQuery(query3)
        }

    }
    fun addScan(skan:Int){
        zeskanowane.add(skan)
    }
    fun getID():Int {
        return id
    }
    fun getParcels():List<Int>{
        return spisPaczek
    }
    fun getSkanned():MutableList<Int>{
        return zeskanowane
    }
    fun clear(){
        this.id = -1
    }
}