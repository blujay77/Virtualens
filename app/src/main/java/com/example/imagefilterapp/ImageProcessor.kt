package com.example.imagefilterapp

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import kotlin.math.round

val BITMAP_CONFIG = Bitmap.Config.ARGB_8888

class ImageProcessor(
    val contentResolver: ContentResolver
) {

    var mainImageMat: Mat = Mat()
    var secondaryImageMat: Mat = Mat()

    fun processImage(imageUri: Uri, filterList: List<ImageFilter>, cvType: Int, maxResolution: Int) {

        secondaryImageMat.release()

        getMatFromUri(imageUri, mainImageMat, maxResolution)
        mainImageMat.convertTo(mainImageMat, cvType)

        for (imageFilter in filterList) {
            when (imageFilter.effect) {
                filterId.GRAYSCALE -> nGrayscale(mainImageMat.nativeObjAddr, cvType)
                filterId.INVERT -> nInvert(mainImageMat.nativeObjAddr)
                filterId.ADDITION -> nAddition(mainImageMat.nativeObjAddr, (imageFilter as Addition).brightness)
                filterId.MULTIPLICATION -> nMultiplication(mainImageMat.nativeObjAddr, (imageFilter as Multiplication).brightness)
                filterId.EXPONENTIATION -> nExponentiation(mainImageMat.nativeObjAddr, (imageFilter as Exponentiation).exponent)
                filterId.SOBEL -> nSobel(mainImageMat.nativeObjAddr, (imageFilter as Sobel).kernel, Sobel.Parameters.Direction.directionConverter[imageFilter.direction]!!, cvType)
                filterId.SCHARR -> nScharr(mainImageMat.nativeObjAddr, cvType)
                filterId.QUANTIZATION -> nQuantization(mainImageMat.nativeObjAddr, (imageFilter as Quantization).colors, cvType)
                filterId.THRESHOLD -> nThreshold(mainImageMat.nativeObjAddr, (imageFilter as Threshold).threshold)
                filterId.BLUR -> nBlur(mainImageMat.nativeObjAddr, (imageFilter as Blur).kernel)
                filterId.DOG -> nDoG(mainImageMat.nativeObjAddr, (imageFilter as DoG).kernel1, imageFilter.kernel2)
            }
        }
    }

    fun combineImages(
        mainImageUri: Uri,
        secondaryImageUri: Uri,
        combinationType: combinationId,
        cvType: Int,
        maxResolution: Int,
        setActivePopup: (availableDialogs) -> Unit
    ): Boolean {
        getMatFromUri(mainImageUri, mainImageMat, maxResolution)

        getMatFromUri(secondaryImageUri, secondaryImageMat, maxResolution)

        if (mainImageMat.size() != secondaryImageMat.size()) {
            setActivePopup(availableDialogs.sameSizeWarning)
            return false
        }

        mainImageMat.convertTo(mainImageMat, cvType)
        secondaryImageMat.convertTo(secondaryImageMat, cvType)


        val scalar255 = Scalar(255.0, 255.0, 255.0)
        when (combinationType) {
            combinationId.ADD -> Core.add(mainImageMat, secondaryImageMat, mainImageMat)
            combinationId.SUBTRACT -> Core.subtract(mainImageMat, secondaryImageMat, mainImageMat)
            combinationId.MULTIPLY -> {
                Core.divide(mainImageMat, scalar255, mainImageMat)
                Core.divide(secondaryImageMat, scalar255, secondaryImageMat)

                Core.multiply(mainImageMat, secondaryImageMat, mainImageMat, 1.0, cvType)

                Core.multiply(mainImageMat, scalar255, mainImageMat)
            }
            combinationId.DIVIDE -> {
                Core.divide(mainImageMat, scalar255, mainImageMat)
                Core.divide(secondaryImageMat, scalar255, secondaryImageMat)

                Core.divide(mainImageMat, secondaryImageMat, mainImageMat)

                Core.multiply(mainImageMat, scalar255, mainImageMat)
            }
            combinationId.MAX -> Core.max(mainImageMat, secondaryImageMat, mainImageMat)
            combinationId.MIN -> Core.min(mainImageMat, secondaryImageMat, mainImageMat)
        }
        return true
    }

    fun getMatFromUri(uri: Uri, mat: Mat, maxResolution: Int) {

        val listener = ImageDecoder.OnHeaderDecodedListener(
            fun(
                decoder: ImageDecoder,
                info: ImageDecoder.ImageInfo,
                source: ImageDecoder.Source
            ) {
                decoder.isMutableRequired = true

                if (maxResolution != 0) {
                    val targetSize =
                        getTargetBitmapSize(info.size.width, info.size.height, maxResolution)
                    decoder.setTargetSize(targetSize["width"]!!, targetSize["height"]!!)
                }
            }
        )

        Utils.bitmapToMat(
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(contentResolver, uri),
                listener
            ),
            mat, true
        )
    }

    companion object {
        init {
            System.loadLibrary("imagefilterapp")
        }
    }
    external fun nGrayscale(matAddr: Long, cvType: Int)
    external fun nInvert(matAddr: Long)
    external fun nAddition(matAddr: Long, brightnessChange: Float)
    external fun nMultiplication(matAddr: Long, brightnessChange: Float)
    external fun nExponentiation(matAddr: Long, exponent: Float)
    external fun nSobel(matAddr: Long, kernel: Int, direction: Int, imageDepth: Int)
    external fun nScharr(matAddr: Long, imageDepth: Int)
    external fun nQuantization(matAddr: Long, colorAmount: Int, imageDepth: Int)
    external fun nThreshold(matAddr: Long, threshold: Float)
    external fun nBlur(matAddr: Long, kernel: Int)
    external fun nDoG(matAddr: Long, kernel1: Int, kernel2: Int)
}

fun matToBitmap(mat: Mat): Bitmap {
    var bitmap = createBitmap(mat.width(), mat.height(), BITMAP_CONFIG)
    mat.convertTo(mat, CvType.CV_8U)

    Utils.matToBitmap(mat, bitmap)

    return bitmap
}

fun getTargetBitmapSize(originalWidth: Int, originalHeight: Int, max: Int): Map<String, Int> {
    if (max == 0 || originalWidth <= max && originalHeight <= max)
        return mapOf("width" to originalWidth, "height" to originalHeight)

    val aspectRatio: Float = originalWidth.toFloat() / originalHeight.toFloat()

    if (originalWidth < originalHeight)
        return mapOf(
            "width" to round(max * aspectRatio).toInt(),
            "height" to max
        )
    else (
        return mapOf(
            "width" to max,
            "height" to round(max / aspectRatio).toInt()
        )
    )
}