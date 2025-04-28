package com.example.imagefilterapp

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.mutableStateListOf
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import org.opencv.core.CvType

@Parcelize
@TypeParceler<List<ImageFilter>, ListParceler>
data class State(
    val mainImageUri: Uri? = null,
    val secondaryImageUri: Uri? = null,
    val activeDialog: availableDialogs? = null,
    val addedFilterList: List<ImageFilter> = mutableStateListOf<ImageFilter>(),
    val maxResolution: Int = 0,
    val cvType: Int = CvType.CV_8U,
    val currentParameterGuide: filterId? = null,
    val showFilterList: Boolean = true
): Parcelable

enum class availableDialogs {
    selectFilterMenu,
    imageWarning,
    filterWarning,
    inputWarning,
    progressIndicator,
    settings,
    parameterGuide,
    sameSizeWarning
}

object ListParceler: Parceler<List<ImageFilter>> {
    override fun create(parcel: Parcel): List<ImageFilter> {
        val size = parcel.readInt()
        val newList: MutableList<ImageFilter> = mutableStateListOf()

        for (i in 0..<size) {
            val effect = filterId.valueOf(parcel.readString()!!)

            when (effect) {
                filterId.ADDITION -> {
                    val newAddition = Addition()
                    val brightnessString = parcel.readString()

                    newAddition.SetBrightnessString(brightnessString!!)

                    newList.add(newAddition)
                }
                filterId.MULTIPLICATION -> {
                    val newMultiplication = Multiplication()
                    val brightnessString = parcel.readString()

                    newMultiplication.SetBrightnessString(brightnessString!!)

                    newList.add(newMultiplication)
                }
                filterId.EXPONENTIATION -> {
                    val newExponentiation = Exponentiation()
                    val exponentString = parcel.readString()

                    newExponentiation.SetExponentString(exponentString!!)

                    newList.add(newExponentiation)
                }
                filterId.SOBEL -> {
                    val newSobel = Sobel()
                    val kernelString = parcel.readString()
                    val directionString = parcel.readString()

                    newSobel.SetKernelString(kernelString!!)
                    newSobel.SetDirectionString(directionString!!)


                    newList.add(newSobel)
                }
                filterId.QUANTIZATION -> {
                    val newQuantization = Quantization()
                    val colorsString = parcel.readString()

                    newQuantization.SetColorsString(colorsString!!)

                    newList.add(newQuantization)
                }
                filterId.THRESHOLD -> {
                    val newThreshold = Threshold()
                    val thresholdString = parcel.readString()

                    newThreshold.SetThresholdString(thresholdString!!)

                    newList.add(newThreshold)
                }
                filterId.BLUR -> {
                    val newBlur = Blur()
                    val kernelString = parcel.readString()

                    newBlur.SetKernelString(kernelString!!)

                    newList.add(newBlur)
                }
                filterId.DOG -> {
                    val newDoG = DoG()
                    val kernel1String = parcel.readString()
                    val kernel2String = parcel.readString()

                    newDoG.SetKernel1String(kernel1String!!)
                    newDoG.SetKernel2String(kernel2String!!)

                    newList.add(newDoG)
                }
                filterId.GRAYSCALE -> newList.add(Grayscale())
                filterId.INVERT -> newList.add(Invert())
                filterId.SCHARR -> newList.add(Scharr())
            }
        }

        return newList
    }

    override fun List<ImageFilter>.write(parcel: Parcel, flags: Int) {
        parcel.writeInt(this.size)

        for (filter in this) {
            parcel.writeString(filter.effect.toString())

            when (filter.effect) {
                filterId.ADDITION -> {
                    parcel.writeString((filter as Addition).brightnessString)
                }
                filterId.MULTIPLICATION -> {
                    parcel.writeString((filter as Multiplication).brightnessString)
                }
                filterId.EXPONENTIATION -> {
                    parcel.writeString((filter as Exponentiation).exponentString)
                }
                filterId.SOBEL -> {
                    parcel.writeString((filter as Sobel).kernelString)
                    parcel.writeString((filter as Sobel).directionString)
                }
                filterId.QUANTIZATION -> {
                    parcel.writeString((filter as Quantization).colorsString)
                }
                filterId.THRESHOLD -> {
                    parcel.writeString((filter as Threshold).thresholdString)
                }
                filterId.BLUR -> {
                    parcel.writeString((filter as Blur).kernelString)
                }
                filterId.DOG -> {
                    parcel.writeString((filter as DoG).kernel1String)
                    parcel.writeString((filter as DoG).kernel2String)
                }
                else -> {}
            }
        }
    }
}