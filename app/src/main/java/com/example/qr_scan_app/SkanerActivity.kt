package com.example.qr_scan_app

import android.Manifest
import android.content.ContentValues
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
import com.example.qr_scan_app.databinding.ActivityMainBinding
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
class SkanerActivity : ComponentActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private var TAG = "SkanerActivity";
    private var imageCapture: ImageCapture? = null
    private lateinit var  labelka:TextView
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
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun sendSkan() {
        cameraExecutor.execute {
            val imageCapture = imageCapture ?: return@execute
            val skan = labelka.text.toString()
            if (skan.length!=10||skan.toBigDecimalOrNull()==null) {
                runOnUiThread {
                    Toast.makeText(baseContext, "Wprowadzono nieprawidłowy format \n$skan", Toast.LENGTH_SHORT).show()
                    labelka.text ="";
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
