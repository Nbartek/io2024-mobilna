package com.example.qr_scan_app

import org.jetbrains.exposed.sql.Database

class dbConnection constructor(login:String, password:String/*nie wiem czy w tym się to przechowywuje*/) {

    init {

    }
    val db by lazy{
        Database.connect("@string/dbConnect","@string/dbDriver", user = login, password = password)
    }
}