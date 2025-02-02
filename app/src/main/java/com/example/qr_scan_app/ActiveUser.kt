package com.example.qr_scan_app

//Singielton obiekt dla zalogowanego urzytkownika
object ActiveUser {
    private lateinit var imie:String
    private lateinit var nazwisko:String
    private lateinit var login:String
    private lateinit var password:String
    fun setUser(name:String,surname:String,login:String,pass:String){
        this.nazwisko = surname
        this.imie = name
        this.password =pass
        this.login = login
    }
    fun getName():String {
        return imie
    }
    fun getSurname():String{
        return nazwisko
    }
    fun getLogin():String{
        return login
    }
    fun getPassword():String{
        return password
    }
}