package com.example.imagefilterapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterList(content: LazyListScope.() -> Unit) {
    Box(modifier = Modifier
        .background(color = MaterialTheme.colorScheme.primaryContainer)
        .size(width = 300.dp, height = 400.dp)
    ) {
        LazyColumn (
            modifier = Modifier
                .padding(15.dp, vertical = 20.dp)
                .fillMaxSize(),
            content = {
                content()
            }
        )
    }
}

@Composable
fun AddFilterButton(
    setActivePopup: (availableDialogs?) -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        onClick = {
            setActivePopup(availableDialogs.selectFilterMenu)
        },
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Filter",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .size(30.dp)
        )
    }
}

@Composable
fun FilterListItem (
    filter: ImageFilter,
    listIndex: Int,
    isMutable: Boolean,
    moveFilterUpList: (Int) -> Unit = fun (a){},
    removeFilterFromList: (Int) -> Unit = fun (a){},
    showToast: (String, Int) -> Unit = fun (a, b){},
    setActivePopup: (availableDialogs?) -> Unit = fun (a){},
    setParameterGuide: (filterId) -> Unit = fun (a){}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(MaterialTheme.colorScheme.primary),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMutable) {
                    if (listIndex != 0)
                        MoveUpListButton(listIndex, moveFilterUpList)
                    else
                        Spacer(modifier = Modifier.width(50.dp))
                }
                else
                    Spacer(modifier = Modifier.width(15.dp))

                Text(
                    text = getFilterNameFromId(filter.effect),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
            if (isMutable)
                RemoveFromListButton(listIndex, removeFilterFromList)
        }

        when(filter.effect) {
            filterId.MULTIPLICATION -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Multiplication.Parameters.Brightness,
                    parameterValueStringified = (filter as Multiplication).brightness.toString(),
                    parameterValueString = (filter as Multiplication).brightnessString,
                    suffix = "x",
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as Multiplication)::SetBrightnessString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            filterId.ADDITION -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Addition.Parameters.Brightness,
                    parameterValueStringified = (filter as Addition).brightness.toString(),
                    parameterValueString = (filter as Addition).brightnessString,
                    suffix = null,
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as Addition)::SetBrightnessString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            filterId.EXPONENTIATION -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Exponentiation.Parameters.Exponent,
                    parameterValueStringified = (filter as Exponentiation).exponent.toString(),
                    parameterValueString = (filter as Exponentiation).exponentString,
                    suffix = null,
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as Exponentiation)::SetExponentString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            filterId.SOBEL -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Sobel.Parameters.Kernel,
                    parameterValueStringified = (filter as Sobel).kernel.toString(),
                    parameterValueString = (filter as Sobel).kernelString,
                    suffix = "px",
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as Sobel)::SetKernelString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Sobel.Parameters.Direction,
                    parameterValueStringified = (filter as Sobel).direction.toString(),
                    parameterValueString = (filter as Sobel).directionString,
                    keyboardType = KeyboardType.Text,
                    suffix = null,
                    isMutable = isMutable,
                    showInfoButton = false,
                    setParameterString = (filter as Sobel)::SetDirectionString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            filterId.QUANTIZATION -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Quantization.Parameters.Colors,
                    parameterValueStringified = (filter as Quantization).colors.toString(),
                    parameterValueString = (filter as Quantization).colorsString,
                    suffix = null,
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as Quantization)::SetColorsString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            filterId.THRESHOLD -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Threshold.Parameters.Threshold,
                    parameterValueStringified = (filter as Threshold).threshold.toString(),
                    parameterValueString = (filter as Threshold).thresholdString,
                    suffix = null,
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as Threshold)::SetThresholdString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            filterId.BLUR -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = Blur.Parameters.Kernel,
                    parameterValueStringified = (filter as Blur).kernel.toString(),
                    parameterValueString = (filter as Blur).kernelString,
                    suffix = "px",
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as Blur)::SetKernelString,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            filterId.DOG -> {
                FilterListParameter(
                    filter = filter,
                    parameterInfo = DoG.Parameters.Kernel1,
                    parameterValueStringified = (filter as DoG).kernel1.toString(),
                    parameterValueString = (filter as DoG).kernel1String,
                    suffix = "px",
                    isMutable = isMutable,
                    showInfoButton = true,
                    setParameterString = (filter as DoG)::SetKernel1String,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
                FilterListParameter(
                    filter = filter,
                    parameterInfo = DoG.Parameters.Kernel2,
                    parameterValueStringified = (filter as DoG).kernel2.toString(),
                    parameterValueString = (filter as DoG).kernel2String,
                    suffix = "px",
                    isMutable = isMutable,
                    showInfoButton = false,
                    setParameterString = (filter as DoG)::SetKernel2String,
                    showToast = showToast,
                    setActivePopup = setActivePopup,
                    setParameterGuide = setParameterGuide
                )
            }
            else -> {}
        }
    }
}

@Composable
fun FilterListParameter(
    filter: ImageFilter,
    parameterInfo: ParameterInfo,
    parameterValueStringified: String,
    parameterValueString: String,
    keyboardType: KeyboardType = KeyboardType.Decimal,
    suffix: String?,
    isMutable: Boolean,
    showInfoButton: Boolean,
    setParameterString: (String) -> Unit = fun (a){},
    showToast: (String, Int) -> Unit = fun (a, b){},
    setActivePopup: (availableDialogs?) -> Unit = fun (a){},
    setParameterGuide: (filterId) -> Unit = fun (a){}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 20.dp)
            .fillMaxWidth()
    ) {
        Row {
            if (isMutable) {
                if (showInfoButton) {
                    IconButton(
                        onClick = {
                            setActivePopup(availableDialogs.parameterGuide)
                            setParameterGuide(filter.effect)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = "Information about ${parameterInfo.name}'s parameters"
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .size(25.dp)
                    )
                }
                else
                    Spacer(modifier = Modifier.width(45.dp))
            }

            Text(text = parameterInfo.name, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }

        val textFieldValue: String = if (isMutable) {
            parameterValueString
        }
        else {
            parameterValueStringified
        }

        TextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                setParameterString(newValue)
            },
            suffix = {
                if (suffix != null)
                    Text(text = suffix, color = MaterialTheme.colorScheme.onPrimaryContainer)
            },
            textStyle = TextStyle(
                textAlign = TextAlign.Right
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                errorTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer,

                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            ),
            enabled = isMutable,
            modifier = Modifier
                .width(100.dp)
        )
    }
}

@Composable
fun FilterListSpacer() {
    Spacer(
        modifier = Modifier
            .height(12.dp)
    )
}

@Composable
fun MoveUpListButton(index: Int, moveFilterUpList: (Int) -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
    ) {
        Button(
            modifier = Modifier
                .fillMaxSize(),
            shape = RectangleShape,
            onClick = {
                moveFilterUpList(index)
            }
        ) {}
        Icon(
            imageVector = Icons.Filled.ArrowUpward,
            contentDescription = "Move filter up the list",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxSize(0.6f)
        )
    }
}

@Composable
fun RemoveFromListButton(index: Int, removeFilterFromList: (Int) -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(50.dp)
    ) {
        Button(
            modifier = Modifier
                .fillMaxSize(),
            shape = RectangleShape,
            onClick = {
                removeFilterFromList(index)
            }
        ) {}
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Remove filter from the list",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxSize(0.6f)
        )
    }
}