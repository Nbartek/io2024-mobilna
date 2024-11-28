package com.example.qr_scan_app
import android.graphics.ImageFormat.*
import android.os.Build
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader

class QrCodeAnalizer:ImageAnalysis.Analyzer {
    //Image Reader używa formatu YuV
    private val yuvFormats = mutableListOf(YUV_420_888)

    init {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            yuvFormats.addAll(listOf(YUV_422_888, YUV_444_888))
        }
    }
    override fun analyze(image: ImageProxy) {
        // We are using YUV format because, ImageProxy internally uses ImageReader to get the image
        // by default ImageReader uses YUV format unless changed.
        //Z tutoriala, może kiedyś się pryzdać
        // https://developer.android.com/reference/androidx/camera/core/ImageProxy.html#getImage()
        // https://developer.android.com/reference/android/media/Image.html#getFormat()
        if (image.format !in yuvFormats) {
            Log.e("QRCodeAnalyzer", "Expected YUV, now = ${image.format}")
            return
        }
    }
    //Dekodowanie
    private val reader = MultiFormatReader().apply {
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
        )
        setHints(map)
    }
}