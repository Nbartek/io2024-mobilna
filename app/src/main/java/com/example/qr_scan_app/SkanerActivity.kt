package com.example.qr_scan_app

import DatabaseHelper
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import android.util.Log
import android.util.Size
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCaptureException
import androidx.core.content.PermissionChecker
import androidx.lifecycle.lifecycleScope
import com.example.qr_scan_app.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.LinkedList
import java.util.Locale
class SkanerActivity : ComponentActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private var TAG = "SkanerActivity";
    private var imageCapture: ImageCapture? = null
    private lateinit var  labelka:TextView
    private lateinit var zakolejkowane:MutableList<Int>
    private lateinit var zaladowano:TextView
    private lateinit var licznik_kolejka:TextView
    private var ladunek:MutableList<Int> = mutableListOf<Int>()
    private val load = CurrentLoad
    val db = DatabaseHelper()
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Set up the listeners for take photo and video capture buttons
        viewBinding.skanBtn.setOnClickListener { sendSkan() }
        labelka = viewBinding.root.findViewById<TextView>(R.id.skanCode)
         zakolejkowane = LinkedList<Int>()
        zaladowano = viewBinding.root.findViewById<TextView>(R.id.cargo_counter)
        licznik_kolejka = viewBinding.root.findViewById(R.id.queue_counter)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun sendSkan() {
        val modeParam = intent.getIntExtra("sqlInputMode",0)
        cameraExecutor.execute {
            val imageCapture = imageCapture ?: return@execute
            val skan = labelka.text.toString()
            if (skan.length!=10||skan.toBigDecimalOrNull()==null) {
                runOnUiThread {
                    Toast.makeText(baseContext, "Wprowadzono nieprawidłowy format \n$skan", Toast.LENGTH_SHORT).show()
                    labelka.text ="";
                }
            }else{
                    when(modeParam){

                        //Utawianie zniszczonej

                        -1 ->{
                            val query:String = "UPDATE dbo.Paczki SET czyZniszczona=1 WHERE Id_Paczki = ${skan};"
                            lifecycleScope.launch {
                                if(db.executeUpdate(query)==0){
                                    runOnUiThread{
                                        Toast.makeText(baseContext, "Nie powiodło się", Toast.LENGTH_SHORT).show()
                                        labelka.text ="";
                                    }
                                }else{
                                    runOnUiThread{
                                        Toast.makeText(baseContext, "Status pakunku zmieniono dla: \n$skan", Toast.LENGTH_SHORT).show()
                                        labelka.text ="";

                                    }
                                }
                            }

                            finish()
                        }

                        //Rozpoczenie rozladunku
                        1 ->{
                            val query:String = "UPDATE dbo.Paczki SET Status='Na Magazynie' WHERE Id_Paczki = ${skan};"
                            lifecycleScope.launch {
                                if(db.executeUpdate(query)==0){
                                    runOnUiThread{
                                        Toast.makeText(baseContext, "Nie powiodło się", Toast.LENGTH_SHORT).show()
                                        labelka.text ="";
                                    }
                                }else{
                                    runOnUiThread{
                                        Toast.makeText(baseContext, "Status pakunku zmieniono dla: \n$skan", Toast.LENGTH_SHORT).show()
                                        labelka.text ="";

                                    }
                                }
                            }
                            //jak nie ma ladunku otwartego
                            //jak mi się będzie chciało poprawnie zroibć tabele ładunków
//                            if(load.getID()==-1){
//                                lifecycleScope.launch {
//                                    val checkQuery:String ="select Id_Ladunku from dbo.Paczka_Ladunek where Id_Paczki = ${skan}"
//                                    val result = db.executeQuery(checkQuery)
//                                    if(result.isNullOrEmpty()){
//                                        //wyswietlamy ostrzerzenie
//                                        runOnUiThread{
//                                            Toast.makeText(baseContext, "Paczka ta nie należy do żadnego ładunku", Toast.LENGTH_SHORT).show()
//                                            labelka.text ="";
//                                        }
//                                    }else{
//                                        //otwieramy ładunke
//                                        load.setCurrentLoad(result.get(0).get("Id_Ladunku").toString().toInt())
//                                        load.addScan(skan.toInt())
//                                        val temp = zaladowano.text.toString().toInt()
//                                        zaladowano.setText( (temp+1).toString())
//                                        runOnUiThread{
//                                            Toast.makeText(baseContext, "Rozpoczęto rozładunek", Toast.LENGTH_SHORT).show()
//                                            labelka.text ="";
//                                        }
//                                    }
//                                }
//
//                            } else{
////                            val query:String = "UPDATE dbo.Paczki SET Status='Przyjęto na Magazyn' WHERE Id_Paczki = ${skan};"
////                            if(db.executeUpdate(query)==0){
////                                zakolejkowane.add(skan.toInt())
////                                var licznik = licznik_kolejka.text.toString().toInt()+1
////                                licznik_kolejka.setText((licznik))
////                            }else{
////                                zaladowano.setText( zaladowano.text.toString().toInt()+1)
////                                zakolejkowane.forEach { row->
////                                    val query:String = "UPDATE dbo.Paczki SET Status='Przyjęto na Magazyn' WHERE Id_Paczki = ${row};"
////                                    db.executeUpdate(query)
////                                    zaladowano.setText( zaladowano.text.toString().toInt()-1)
////                                }
////                            }
//                                val spisPaczek:List<Int> = load.getParcels()
//                                if(spisPaczek.contains(skan.toInt())&&!load.getSkanned().contains(skan.toInt())){
//
//                                }
//                            }

                        }
                        //Zaladunke
                        0->{
                            //jak nie ma ladunku otwartego
                            if(load.getID()==-1){
                                val inte = Intent(this,LoadCreationActivity::class.java)
                                val queryCheck = "Select Id_Ladunku from dbo.Paczka_Ladunek where Id_Paczki=${skan} "
                                lifecycleScope.launch {
                                    val result = db.executeQuery(queryCheck)
                                    if(result.isNullOrEmpty()){
                                        inte.putExtra("skan",skan.toInt())
                                        inte.putExtra("tworzenie",1)
                                        ladunek.add(skan.toInt())
                                        val temp = zaladowano.text.toString().toInt()
                                        zaladowano.setText( (temp+ladunek.size).toString())
                                        startActivity(inte)
                                    }else{
                                     load.setCurrentLoad(result.get(0).get("Id_Ladunku").toString().toInt())
                                        val temp = zaladowano.text.toString().toInt()
                                        ladunek.add(skan.toInt())
                                        ladunek.addAll(load.getParcels())
                                        zaladowano.setText( (temp+ladunek.size).toString())
                                        runOnUiThread {
                                            labelka.text ="";
                                        }
                                    }
                                }

                            }else {
                                licznik_kolejka.text = load.getID().toString()
                                if(ladunek.contains(skan.toInt())){
                                    runOnUiThread {
                                        Toast.makeText(baseContext, "Już skanowane", Toast.LENGTH_SHORT).show()
                                        labelka.text ="";
                                    }
                                }else{

                                lifecycleScope.launch {

                                val query: String =
                                    "UPDATE dbo.Paczki SET Status='Załadowano' WHERE Id_Paczki = ${skan};"
                                if (db.executeUpdate(query) == 0) {
                                    runOnUiThread {
                                        Toast.makeText(baseContext, "Paczka została usunięta", Toast.LENGTH_SHORT).show()
                                        labelka.text ="";
                                    }
//                                    val query0: String = "INSERT into Paczka_Ladunek (Id_Ladunku,Id_Paczki)values(${load.getID()},${skan})"
//                                    val re = db.executeUpdate(query0)
//                                    zakolejkowane.add(skan.toInt())
//                                    val temp = zaladowano.text.toString().toInt()
//                                    zaladowano.setText( (temp+1).toString())
                                } else {
                                    val temp = zaladowano.text.toString().toInt()
                                    zaladowano.setText( (temp+1).toString())
                                    val query0: String = "INSERT into Paczka_Ladunek (Id_Ladunku,Id_Paczki)values(${load.getID()},${skan})"
                                    val re = db.executeUpdate(query0)
                                    ladunek.add(skan.toInt())
                                    zakolejkowane.forEach { row ->
                                        val query2: String = "UPDATE dbo.Paczki SET Status='Załadowano' WHERE Id_Paczki = ${row};"
                                        val query3: String = "INSERT into Paczka_Ladunek (Id_Ladunku,Id_Paczki)values(${load.getID()},${row})"
                                        db.executeUpdate(query3)
                                        db.executeUpdate(query2)
                                        val temp = zaladowano.text.toString().toInt()
                                        zaladowano.setText( (temp-1).toString())
                                    }
                                }
                                    runOnUiThread {
                                        labelka.text ="";
                                    }
                                }
                                }
                            }
                        }
                    }

            }

        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Powiąż cykl życiowy(lifecycle) kamery z cyklem użytkownika
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            //kod qr

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QrCodeAnalizer { qrResult ->
                        viewBinding.root.post {
                            if(labelka.text != qrResult.text){
                            Log.i(TAG+"Analiza kodu qr", "Kod zeskanowany: ${qrResult.text}")
                                labelka.text = qrResult.text
                            }
                            //finish()
                        }
                    })
                }
            // Podgląd
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()

            // Której kamery używamy
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // odłączamy kamerę od innych powiążanych procesów
                cameraProvider.unbindAll()

                // Powiąż z aparatem kolejno , cykl życia użytkownika, który aparat, podgląd, analiza_kodów
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageAnalysis)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Activity destroyed.")
        cameraExecutor.shutdown()
    }
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Zarządzanie (nie)przyznanymi pozwoleniami
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Nie dano pozwoleń",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    companion object {
        private const val TAG = "Skaner Poczty"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
