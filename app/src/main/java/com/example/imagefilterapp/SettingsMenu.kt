package com.example.imagefilterapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import org.opencv.core.CvType

const val MAX_RESOLUTION_KEY_STRING = "maxResolution"
val MAX_RESOLUTION_KEY = intPreferencesKey(MAX_RESOLUTION_KEY_STRING)
const val CV_TYPE_KEY_STRING = "cvType"
val CV_TYPE_KEY = intPreferencesKey(CV_TYPE_KEY_STRING)

@Preview
@Composable
fun SettingsPreview() {
    SettingsMenu(
        state = State(cvType = CvType.CV_8U),
        setActivePopup = {},
        writeSetting = fun (b: Preferences.Key<Int>, c: Int) {}
    )
}

@Composable
fun SettingsMenu(
    state: State,
    setActivePopup: (availableDialogs?) -> Unit,
    writeSetting: (Preferences.Key<Int>, Int) -> Unit
) {
    Dialog(
        onDismissRequest = {
            setActivePopup(null)
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        ),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.75f)
                    .background(Color.Gray)
            ) {
                LazyColumn {
                    item {
                        Setting(
                            header = "Color Depth",
                            description = "A greater color depth prevents clipping during processing, " +
                                    "making large changes in brightness reversible and increases visual quality " +
                                    "of some filters at the cost of increased memory usage.",
                            content = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    LabelledRadioButton(
                                        label = "Low",
                                        affectedValue = state.cvType,
                                        buttonValue = CvType.CV_8U,
                                        writeSetting = writeSetting,
                                        settingKey = CV_TYPE_KEY
                                    )
                                    LabelledRadioButton(
                                        label = "High",
                                        affectedValue = state.cvType,
                                        buttonValue = CvType.CV_32F,
                                        writeSetting = writeSetting,
                                        settingKey = CV_TYPE_KEY
                                    )
                                }
                            }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    item {
                        Setting(
                            header = "Resolution",
                            description = "Downscale large images to a maximum height/width to " +
                                    "reduce required processing power at the cost of image quality.",
                            content = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    LabelledRadioButton(
                                        label = "4k",
                                        affectedValue = state.maxResolution,
                                        buttonValue = 4000,
                                        writeSetting = writeSetting,
                                        settingKey = MAX_RESOLUTION_KEY
                                    )
                                    LabelledRadioButton(
                                        label = "8k",
                                        affectedValue = state.maxResolution,
                                        buttonValue = 8000,
                                        writeSetting = writeSetting,
                                        settingKey = MAX_RESOLUTION_KEY
                                    )
                                    LabelledRadioButton(
                                        label = "12k",
                                        affectedValue = state.maxResolution,
                                        buttonValue = 12000,
                                        writeSetting = writeSetting,
                                        settingKey = MAX_RESOLUTION_KEY
                                    )
                                    LabelledRadioButton(
                                        label = "Unlimited",
                                        affectedValue = state.maxResolution,
                                        buttonValue = 0,
                                        writeSetting = writeSetting,
                                        settingKey = MAX_RESOLUTION_KEY
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun Setting(
    header: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column {
        MenuHeader(header)
        content()
        Spacer(modifier = Modifier.height(15.dp))
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            Text(text = description)
        }
    }
}

@Composable
fun LabelledRadioButton(
    label: String,
    affectedValue: Int,
    buttonValue: Int,
    writeSetting: (Preferences.Key<Int>, Int) -> Unit,
    settingKey: Preferences.Key<Int>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RadioButton(
            selected = affectedValue == buttonValue,
            onClick = {
                if (affectedValue != buttonValue) {
                    writeSetting(settingKey, buttonValue)
                }
            }
        )
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            style = TextStyle.Default.copy(
                lineBreak = LineBreak.Paragraph
            ))
    }
}