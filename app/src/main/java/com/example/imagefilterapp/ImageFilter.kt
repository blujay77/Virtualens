package com.example.imagefilterapp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.opencv.core.CvType
import kotlin.math.round

interface ImageFilter {
    val effect: filterId

    fun transferParameterStrings(matType: Int): Boolean
}

interface ParameterInfo {
    val name: String
    val defaultValue: Any
    val inputGuide: String
}

/*
To add new filter:
-add filterId
-create new Class in ImageFilter.kt (here)
-add to the two "get...From..." functions in ImageFilter.kt (here)
-add to the "FilterSelectMenu" list in MainScreen.kt
-if applicable, add to "ParameterGuide" in MainScreen.kt
-if applicable, add to "FilterListItem" in FilterList.kt
-add to the two functions for "ListParceler" in State.kt
-add to "processImage" in ImageProcessor.kt
-add as external fun in ImageProcessor.kt
 */

class Grayscale: ImageFilter {
    override val effect = filterId.GRAYSCALE

    override fun transferParameterStrings(matType: Int): Boolean {return true}
}

class Invert: ImageFilter {
    override val effect: filterId = filterId.INVERT

    override fun transferParameterStrings(matType: Int): Boolean {return true}
}

class Addition: ImageFilter {
    override val effect = filterId.ADDITION

    var brightness by mutableStateOf<Float>(Brightness.defaultValue)
        private set

    var brightnessString by mutableStateOf<String>(brightness.toString())
        private set

    fun SetBrightnessString(value: String) {
        brightnessString = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            brightness = brightnessString.toFloat()
        }
        catch (e: Exception) {
            return false

        }

        if (brightness < -1 || brightness > 1)
            return false

        return true
    }

    companion object Parameters {
        object Brightness: ParameterInfo {
            override val name: String = "Brightness"
            override val defaultValue: Float = 0f
            override val inputGuide = "Any decimal number from -1-1"
        }
    }
}

class Multiplication: ImageFilter {
    override val effect = filterId.MULTIPLICATION

    var brightness by mutableStateOf<Float>(Brightness.defaultValue)
        private set

    var brightnessString by mutableStateOf<String>(brightness.toString())
        private set

    fun SetBrightnessString(value: String) {
        brightnessString = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            brightness = brightnessString.toFloat()
        }
        catch (e: Exception) {
            return false
        }

        if (brightness < 0)
            return false

        return true
    }

    companion object Parameters {
        object Brightness: ParameterInfo {
            override val name: String = "Brightness"
            override val defaultValue: Float = 1f
            override val inputGuide = "Any positive decimal number or 0"
        }
    }
}

class Exponentiation: ImageFilter {
    override val effect = filterId.EXPONENTIATION

    var exponent by mutableStateOf<Float>(Exponent.defaultValue)
        private set

    var exponentString by mutableStateOf<String>(exponent.toString())
        private set

    fun SetExponentString(value: String) {
        exponentString = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            if (matType == CvType.CV_8U) {
                if (
                    //round(exponentString.toFloat().mod(1f) * 100000) / 100000 != 0f
                    exponentString.toFloat().mod(1f) > 0.00001f
                ) {
                    throw Exception("No decimals with low color depth")
                }
                else exponent = round(exponentString.toFloat())
            }
            else {
                exponent = exponentString.toFloat()
            }
        }
        catch (e: Exception) {
            return false
        }

        return true
    }

    companion object Parameters {
        object Exponent: ParameterInfo {
            override val name: String = "Exponent"
            override val defaultValue: Float = 1f
            override val inputGuide: String = "High color depth: Any decimal number \n Low color depth: Any whole number"
        }
    }
}

class Sobel: ImageFilter {
    override val effect: filterId = filterId.SOBEL

    var kernel by mutableStateOf<Int>(Kernel.defaultValue)
        private set

    var kernelString by mutableStateOf<String>(kernel.toString())
        private set

    fun SetKernelString(value: String) {
        kernelString = value
    }

    var direction by mutableStateOf<String>(Direction.defaultValue)
        private set

    var directionString by mutableStateOf<String>(direction)
        private set

    fun SetDirectionString(value: String) {
        directionString = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            kernel = kernelString.toInt()
            direction = directionString.uppercase()
        } catch (e: Exception) {
            return false
        }

        if (kernel > 31 || kernel < 0 || (kernel.mod(2) != 1 && kernel != 0)) {
            return false
        }

        if (direction !in arrayOf("XY", "X", "Y")) {
            return false
        }

        return true
    }

    companion object Parameters {
        object Kernel: ParameterInfo {
            override val name = "Kernel"
            override val defaultValue = 3
            override val inputGuide = "An uneven number from 1-31 or 0"
        }
        object Direction: ParameterInfo {
            override val name = "Direction"
            override val defaultValue = "XY"
            override val inputGuide = "\"XY\" or \"X\" or \"Y\""
            val directionConverter = mapOf(
                "XY" to 0,
                "X" to 1,
                "Y" to 2
            )
        }
    }
}

class Scharr: ImageFilter {
    override var effect = filterId.SCHARR

    override fun transferParameterStrings(matType: Int): Boolean { return true }
}

class Quantization: ImageFilter {
    override val effect = filterId.QUANTIZATION

    var colors by mutableStateOf<Int>(Colors.defaultValue)
        private set

    var colorsString by mutableStateOf<String>(colors.toString())
        private set

    fun SetColorsString(value: String) {
        colorsString = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            colors = colorsString.toInt()
        }
        catch (e: Exception) {
            return false
        }

        if (colors < 2 || colors > 100) {
            return false
        }

        return true
    }

    companion object Parameters {
        object Colors: ParameterInfo {
            override val name = "Colors"
            override val defaultValue = 5
            override val inputGuide = "A whole number from 2-100"
        }
    }
}

class Threshold: ImageFilter {
    override val effect = filterId.THRESHOLD

    var threshold by mutableStateOf<Float>( Threshold.defaultValue )
        private set

    var thresholdString by mutableStateOf<String>(threshold.toString())
        private set

    fun SetThresholdString (value: String) {
        thresholdString = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            threshold = thresholdString.toFloat()
        }
        catch (e: Exception) {
            return false
        }

        if (threshold < 0 || threshold > 1) {
            return false
        }

        return true
    }

    companion object Parameters {
        object Threshold: ParameterInfo {
            override val name = "Threshold"
            override val defaultValue = 0.5f
            override val inputGuide = "Any decimal number from 0-1"
        }
    }
}

class Blur: ImageFilter {
    override val effect = filterId.BLUR

    var kernel by mutableStateOf<Int>( Kernel.defaultValue )
        private set

    var kernelString by mutableStateOf<String>( kernel.toString() )
        private set

    fun SetKernelString(value: String) {
        kernelString = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            kernel = kernelString.toInt()
        }
        catch (e: Exception) {
            return false
        }

        if (kernel < 0 || kernel % 2 != 1) {
            return false
        }

        return true
    }

    companion object Parameters {
        object Kernel: ParameterInfo {
            override val name = "Kernel"
            override val defaultValue = 15
            override val inputGuide = "Any positive uneven number"
        }
    }
}

class DoG: ImageFilter {
    override val effect = filterId.DOG

    var kernel1 by mutableStateOf( Kernel1.defaultValue )
        private set

    var kernel1String by mutableStateOf( kernel1.toString() )
        private set

    fun SetKernel1String(value: String) {
        kernel1String = value
    }

    var kernel2 by mutableStateOf( Kernel2.defaultValue )
        private set

    var kernel2String by mutableStateOf( kernel2.toString() )
        private set

    fun SetKernel2String(value: String) {
        kernel2String = value
    }

    override fun transferParameterStrings(matType: Int): Boolean {
        try {
            kernel1 = kernel1String.toInt()
            kernel2 = kernel2String.toInt()
        }
        catch (e: Exception) {
            return false
        }

        if (kernel1 < 0 || (kernel1 != 0 && kernel1 % 2 == 0)) {
            return false
        }

        if (kernel2 < 0 || (kernel2 != 0 && kernel2 % 2 == 0)) {
            return false
        }

        return true
    }

    companion object Parameters {
        object Kernel1: ParameterInfo {
            override val name = "Kernel 1"
            override val defaultValue = 0
            override val inputGuide = "Any positive uneven number or 0"
        }
        object Kernel2: ParameterInfo {
            override val name = "Kernel 2"
            override val defaultValue = 15
            override val inputGuide = "Any positive uneven number or 0"
        }
    }
}

enum class filterId {
    GRAYSCALE,
    ADDITION,
    MULTIPLICATION,
    EXPONENTIATION,
    INVERT,
    SOBEL,
    SCHARR,
    QUANTIZATION,
    THRESHOLD,
    BLUR,
    DOG
}

fun getFilterNameFromId(filter: filterId): String {
    return when (filter) {
        filterId.GRAYSCALE -> "Grayscale"
        filterId.INVERT -> "Invert"
        filterId.ADDITION -> "Addition"
        filterId.MULTIPLICATION -> "Multiplication"
        filterId.EXPONENTIATION -> "Exponentiation"
        filterId.SOBEL -> "Sobel"
        filterId.SCHARR -> "Scharr"
        filterId.QUANTIZATION -> "Quantization"
        filterId.THRESHOLD -> "Threshold"
        filterId.BLUR -> "Blur"
        filterId.DOG -> "DoG"
    }
}

fun getFilterFromId(filter: filterId): ImageFilter {
    return when (filter) {
        filterId.GRAYSCALE -> Grayscale()
        filterId.INVERT -> Invert()
        filterId.ADDITION -> Addition()
        filterId.MULTIPLICATION -> Multiplication()
        filterId.EXPONENTIATION -> Exponentiation()
        filterId.SOBEL -> Sobel()
        filterId.SCHARR -> Scharr()
        filterId.QUANTIZATION -> Quantization()
        filterId.THRESHOLD -> Threshold()
        filterId.BLUR -> Blur()
        filterId.DOG -> DoG()
    }
}

