package com.example.imagefilterapp

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream
import java.util.Collections

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@OptIn(SavedStateHandleSaveableApi::class)
class MainActivityViewModel(
    val context: Context?,
    val navController: NavController,
    savedStateHandle: SavedStateHandle,
    contentResolver: ContentResolver
): ViewModel() {

    init {
        viewModelScope.launch {
            context!!.datastore.data.map { preferences ->
                mapOf<String, Int>(
                    MAX_RESOLUTION_KEY_STRING to (preferences[MAX_RESOLUTION_KEY] ?: 0),
                    CV_TYPE_KEY_STRING to (preferences[CV_TYPE_KEY] ?: 0)
                )
            }.collect { values ->
                state = state.copy(
                    maxResolution = values[MAX_RESOLUTION_KEY_STRING]!!,
                    cvType = values[CV_TYPE_KEY_STRING]!!
                )
            }
        }
    }

    fun writeSetting(key: Preferences.Key<Int>, value: Int) {
        viewModelScope.launch (Dispatchers.IO){
            context!!.datastore.edit {
                it[key] = value
            }
        }
    }

    var state by savedStateHandle.saveable {
        mutableStateOf(State())
    }

    val imageProcessor = ImageProcessor(contentResolver)

    fun showToast(message: Any, length: Int) {
        Toast.makeText(context, message.toString(), length).show()
    }

    fun setMainImageUri(uri: Uri) {
        state = state.copy(mainImageUri = uri)
    }

    fun setSecondaryImageUri(uri: Uri) {
        state = state.copy(secondaryImageUri = uri)
    }

    fun setActivePopup(dialog: availableDialogs?){
        state = state.copy(activeDialog = dialog)
    }

    fun setParameterGuide(filter: filterId?) {
        state = state.copy(currentParameterGuide = filter)
    }

    fun setShowFilterList(bool: Boolean) {
        state = state.copy(showFilterList = bool)
    }

    fun addFilterToList(filter: filterId) {
        val imageFilter: ImageFilter = getFilterFromId(filter)

        val newList: MutableList<ImageFilter> = state.addedFilterList as MutableList
        newList.add(imageFilter)

        state = state.copy(addedFilterList = newList)
    }

    fun moveFilterUpList(index: Int) {
        if (index == 0)
            return

        val newList: MutableList<ImageFilter> = state.addedFilterList as MutableList

        Collections.swap(newList, index, index - 1)

        state = state.copy(addedFilterList = newList)
    }

    fun removeFilterFromList(index: Int) {
        val newList: MutableList<ImageFilter> = state.addedFilterList as MutableList

        newList.removeAt(index)

        state = state.copy(addedFilterList = newList)
    }

    fun onProcessButtonClickedLauncher() {
        viewModelScope.launch {
            onProcessButtonClicked()
        }
    }

    suspend fun onProcessButtonClicked() {
        if (checkInputs() == false) {
            return
        }

        setActivePopup(availableDialogs.progressIndicator)

        withContext(Dispatchers.Default) {
            imageProcessor.processImage(
                state.mainImageUri!!,
                state.addedFilterList,
                state.cvType,
                state.maxResolution
            )
        }


        //showToast(timeTaken)

        setActivePopup(null)
        setShowFilterList(true)
        navController.navigate(ResultScreen)
    }

    fun checkInputs(): Boolean {
        if (state.mainImageUri == null) {
            setActivePopup(availableDialogs.imageWarning)
            return false
        }

        if (state.addedFilterList.isEmpty()) {
            setActivePopup(availableDialogs.filterWarning)
            return false
        }

        if (transferParameters() == false) {
            setActivePopup(availableDialogs.inputWarning)
            return false
        }

        return true
    }

    fun combineImagesLauncher(combinationType: combinationId) {
        viewModelScope.launch {
            combineImages(combinationType)
        }
    }

    suspend fun combineImages(combinationType: combinationId) {

        if (state.mainImageUri == null || state.secondaryImageUri == null) {
            setActivePopup(availableDialogs.imageWarning)
            return
        }

        setActivePopup(availableDialogs.progressIndicator)

         if (withContext(Dispatchers.Default) {
            imageProcessor.combineImages(
                state.mainImageUri!!,
                state.secondaryImageUri!!,
                combinationType,
                state.cvType,
                state.maxResolution,
                this@MainActivityViewModel::setActivePopup
            )
        } == false){
             return
        }

        setActivePopup(null)
        setShowFilterList(false)
        navController.navigate(ResultScreen)
    }

    fun transferParameters(): Boolean {
        for (filter in state.addedFilterList) {
            if (filter.transferParameterStrings(state.cvType) == false)
                return false
        }
        return true
    }

    fun onSaveImageButtonClickedLauncher() {
        viewModelScope.launch {
            onSaveImageButtonClicked()
        }
    }


    suspend fun onSaveImageButtonClicked() {
        try {
            withContext(Dispatchers.IO) {
                saveImage()
            }
            showToast("Image saved!", Toast.LENGTH_SHORT)
        }
        catch (e: Exception) {
            showToast("Saving failed", Toast.LENGTH_LONG)
        }
    }

    //https://www.simplifiedcoding.net/android-save-bitmap-to-gallery/#file-storage-in-android
    fun saveImage() {
        fun generateImageName(): String {
            val calendar = java.util.Calendar.getInstance()
            return  "${app_name}_" +
                    "${calendar.get(java.util.Calendar.YEAR)}_" +
                    "${calendar.get(java.util.Calendar.MONTH)}_" +
                    "${calendar.get(java.util.Calendar.DAY_OF_MONTH)}_" +
                    "${calendar.get(java.util.Calendar.HOUR_OF_DAY)}_" +
                    "${calendar.get(java.util.Calendar.MINUTE)}_" +
                    "${calendar.get(java.util.Calendar.SECOND)}"

        }
        val fileName: String = generateImageName() + ".jpg"

        var outputStream: OutputStream? = null

        context?.contentResolver?.let { contentResolver ->
            val fileValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val outputUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, fileValues)

            outputStream = outputUri?.let { contentResolver.openOutputStream(it) }
        }

        outputStream?.use {
            matToBitmap(imageProcessor.mainImageMat).compress(Bitmap.CompressFormat.JPEG, 90, it)
        }
    }
}