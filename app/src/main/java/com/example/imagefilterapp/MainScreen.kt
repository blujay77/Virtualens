package com.example.imagefilterapp

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage

//@Preview
//@Composable
//fun MainScreenPreview() {
//    MainScreen(State(
//        addedFilterList = mutableListOf(
//            Brightness()
//        )
//    ), MainActivityViewModel(null, null, null), rememberNavController())
//}

@Composable
fun MainScreen(state: State, viewModel: MainActivityViewModel) {
    when (state.activeDialog) {
        availableDialogs.selectFilterMenu -> FilterSelectMenu(viewModel::addFilterToList, viewModel::setActivePopup)
        availableDialogs.settings -> SettingsMenu(state, viewModel::setActivePopup, viewModel::writeSetting)
        availableDialogs.parameterGuide -> ParameterGuide(state, viewModel::setActivePopup, viewModel::setParameterGuide)
        availableDialogs.imageWarning -> WarningPopup(viewModel::setActivePopup, "Select an image!")
        availableDialogs.filterWarning  -> WarningPopup(viewModel::setActivePopup, "Select at least one filter!")
        availableDialogs.inputWarning -> WarningPopup(viewModel::setActivePopup, "Your inputs are invalid!")
        availableDialogs.progressIndicator -> ProgressIndicator()
        null -> {}
        else -> WarningPopup(viewModel::setActivePopup, "Oops! Something went wrong somewhere.")
    }
    BaseUI (
        showBackArrow = false,
        showFloatingActionButton = true,
        showSettingsButton = true,
        onSettingsButtonClicked = {
            viewModel.setActivePopup(availableDialogs.settings)
        },
        showSwitchScreenButton = true,
        onSwitchScreenButtonClicked = {
            viewModel.navController.navigate(CombinationScreen)
        },
        floatingActionButtonIcon = {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "Process image with selected filters"
            )
        },
        onFloatingActionButtonClick = {
            viewModel.onProcessButtonClickedLauncher()
        },
        content = { innerPadding ->
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                ImageBox(state.mainImageUri, 300, viewModel::setMainImageUri)
                FilterList(content = {
                    for (filter in state.addedFilterList) {
                        item {
                            FilterListItem(
                                filter = filter,
                                listIndex = state.addedFilterList.indexOf(filter),
                                isMutable = true,
                                viewModel::moveFilterUpList,
                                viewModel::removeFilterFromList,
                                viewModel::showToast,
                                viewModel::setActivePopup,
                                viewModel::setParameterGuide
                            )
                            FilterListSpacer()
                        }
                    }
                    item {
                        AddFilterButton(viewModel::setActivePopup)
                    }
                })
            }
        }
    )
}

@Composable
fun ImageBox(
    imageUri: Uri?,
    width: Int,
    setImageUri: (Uri) -> Unit
){

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            setImageUri(uri)
        }
    }

    if (imageUri == null) {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .size(width = width.dp, height = 200.dp)
                .clickable {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
        ) {
            Icon(
                imageVector = Icons.Filled.FolderOpen,
                contentDescription = "Select an image",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
            )
        }
    }
    else {
        Box (
            modifier = Modifier
                .width(width.dp)
        ){
            AsyncImage(
                model = imageUri,
                contentDescription = "Your image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(width.dp)
                    .height(250.dp)
            )
            Button(
                onClick = {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(60.dp)
                    .aspectRatio(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.FolderOpen,
                    contentDescription = "Select an image",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(40.dp)
                )
            }
        }
    }
}

@Composable
fun FilterSelectMenu (
    addFilterToList: (filterId) -> Unit,
    setActivePopup: (availableDialogs?) -> Unit
) {
    val filterSelectMenuItems: Map<String, List<filterId>> = mapOf(
        "Brightness" to listOf(
            filterId.ADDITION,
            filterId.MULTIPLICATION,
            filterId.EXPONENTIATION
        ),
        "Color" to listOf(
            filterId.GRAYSCALE,
            filterId.INVERT,
            filterId.QUANTIZATION,
            filterId.THRESHOLD
        ),
        "Edges" to listOf(
            filterId.BLUR,
            filterId.SOBEL,
            filterId.SCHARR,
            filterId.DOG
        )
    )

    Dialog(
        onDismissRequest = {
            setActivePopup(null)
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        content = {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.75f),
                    content = {
                        for (category in filterSelectMenuItems) {
                            item {
                                FilterSelectMenuItem(
                                    text = category.key,
                                    isHeader = true,
                                    addFilterToList = addFilterToList,
                                    setActivePopup = setActivePopup
                                )
                            }
                            for (filter in category.value) {
                                item {
                                    FilterSelectMenuItem(
                                        filter = filter,
                                        isHeader = false,
                                        addFilterToList = addFilterToList,
                                        setActivePopup = setActivePopup
                                    )
                                    if (category.value.last() !== filter) {
                                        HorizontalDivider()
                                    }
                                }
                            }
                        }
                    }
                )

        }
    )
}

@Composable
fun FilterSelectMenuItem(filter: filterId? = null,
                         text: String? = null,
                         isHeader: Boolean,
                         addFilterToList: (filterId) -> Unit,
                         setActivePopup: (availableDialogs?) -> Unit
) {
    if (isHeader) {
        MenuHeader(text!!)
    }
    else {
        Button (
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = {
                addFilterToList(filter!!)
                setActivePopup(null)
            }
        ) {
            Text(text = getFilterNameFromId(filter!!), color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@Composable
fun WarningPopup (setActivePopup: (availableDialogs?) -> Unit, message: String) {
    Dialog(
        onDismissRequest = {
            setActivePopup(null)
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.2f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuHeader("Warning!")
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 35.sp
                    )
                }
            }
        }
    )
}

@Composable
fun MenuHeader(
    header: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text = header, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
    }
}

@Composable
fun ProgressIndicator () {
    Dialog(
        onDismissRequest = {},
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    )
}

@Composable
fun ParameterGuide(
    state: State,
    setActivePopup: (availableDialogs?) -> Unit,
    setParameterGuide: (filterId?) -> Unit
) {
    Dialog(
        onDismissRequest = {
            setActivePopup(null)
            setParameterGuide(null)
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(color = MaterialTheme.colorScheme.secondaryContainer),
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            if (state.currentParameterGuide == null)
                item {
                    Text("Oops. looks like something went wrong somewhere around here!")
                }
            item {
                if (state.currentParameterGuide == filterId.DOG) {
                    MenuHeader("Difference of Gaussians")
                }
                else {
                    MenuHeader(getFilterNameFromId(state.currentParameterGuide!!))
                }
            }
            item {
                when (state.currentParameterGuide) {
                    filterId.MULTIPLICATION -> {
                        ParameterGuideParameter(Multiplication.Parameters.Brightness)
                        //HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                    filterId.ADDITION -> {
                        ParameterGuideParameter(Addition.Parameters.Brightness)
                    }
                    filterId.EXPONENTIATION -> {
                        ParameterGuideParameter(Exponentiation.Parameters.Exponent)
                    }
                    filterId.SOBEL -> {
                        ParameterGuideParameter(Sobel.Parameters.Kernel)
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
                        ParameterGuideParameter(Sobel.Parameters.Direction)
                    }
                    filterId.QUANTIZATION -> {
                        ParameterGuideParameter(Quantization.Parameters.Colors)
                    }
                    filterId.THRESHOLD -> {
                        ParameterGuideParameter(Threshold.Parameters.Threshold)
                    }
                    filterId.BLUR -> {
                        ParameterGuideParameter(Blur.Parameters.Kernel)
                    }
                    filterId.DOG -> {
                        ParameterGuideParameter(DoG.Parameters.Kernel1)
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSecondaryContainer)
                        ParameterGuideParameter(DoG.Parameters.Kernel2)
                    }

                    else -> {
                        Text("Oops. looks like something went wrong somewhere around here! \n (Hint: It's ${state.currentParameterGuide}!")
                    }
                }
            }
        }
    }
}

@Composable
fun ParameterGuideParameter(
    parameterInfo: ParameterInfo
) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(vertical = 30.dp, horizontal = 15.dp)
    ){
        Text(text = parameterInfo.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Text(text = parameterInfo.inputGuide, color = MaterialTheme.colorScheme.onSecondaryContainer, textAlign = TextAlign.Center,
        )
    }
}