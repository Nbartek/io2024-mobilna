package com.example.qr_scan_app
import android.graphics.ImageFormat.*
import android.os.Build
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

private fun ByteBuffer.toByteArray():ByteArray{
    rewind()
    val data = ByteArray(remaining())
    get(data)
    return data
}
class QrCodeAnalizer(
    private val onQrCodesDetected: (qrCode: com.google.zxing.Result) -> Unit
):ImageAnalysis.Analyzer {
    //Image Reader używa formatu YuV
    private val yuvFormats = mutableListOf(YUV_420_888)

    init {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            yuvFormats.addAll(listOf(YUV_422_888, YUV_444_888))
        }
    }

    //Dekodowanie
    private val reader = MultiFormatReader().apply {
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE) //specyfikacja dekodowania kodu qr
        )
        setHints(map)
    }
    // We are using YUV format because, ImageProxy internally uses ImageReader to get the image
    // by default ImageReader uses YUV format unless changed.
    //Z tutoriala, może kiedyś się pryzdać
    // https://developer.android.com/reference/androidx/camera/core/ImageProxy.html#getImage()
    // https://developer.android.com/reference/android/media/Image.html#getFormat()
    //funkcja do analizy
    override fun analyze(image: ImageProxy) {
        if (image.format !in yuvFormats) {
            Log.e("QRCodeAnalyzer", "Expected YUV, now = ${image.format}")
            return
        }
        val data = image.planes[0].buffer.toByteArray()
        //podświetla dla komputera, nie w preview
        val source = PlanarYUVLuminanceSource(
            data,
            image.width,
            image.height,
            0,
            0,
            image.width,
            image.height,
            false
        )
        //z source towrzymy binarną mapę by Reader zdekodował qr
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        try{
            //jak nie odczyta kodu, wywala NoFoundExeption(dobrze)
            val result =reader.decode(binaryBitmap)
            onQrCodesDetected(result)
            Log.d("Analiza qr",result.text)

        }catch(e: NotFoundException){
            e.printStackTrace()
        }
        //image.close()
    }
}