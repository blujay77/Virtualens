package com.example.imagefilterapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

enum class combinationId {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MAX,
    MIN
}

@Composable
fun CombinationScreen(state: State, viewModel: MainActivityViewModel) {
    when (state.activeDialog) {
        availableDialogs.settings -> SettingsMenu(state, viewModel::setActivePopup, viewModel::writeSetting)
        availableDialogs.imageWarning -> WarningPopup(viewModel::setActivePopup, "Select two images!")
        availableDialogs.sameSizeWarning -> WarningPopup(viewModel::setActivePopup, "Select images of the same size!")
        availableDialogs.progressIndicator -> ProgressIndicator()
        null -> {}
        else -> WarningPopup(viewModel::setActivePopup, "Nothing ever happens.")
    }
    BaseUI(
        showBackArrow = false,
        showFloatingActionButton = false,
        showSettingsButton = true,
        onSettingsButtonClicked = {
            viewModel.setActivePopup(availableDialogs.settings)
        },
        showSwitchScreenButton = true,
        onSwitchScreenButtonClicked = {
            viewModel.navController.navigate(MainScreen)
        },
        content = { innerPadding ->
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ImageBox(state.mainImageUri, 140, viewModel::setMainImageUri)
                    Spacer(modifier = Modifier.width(20.dp))
                    ImageBox(state.secondaryImageUri, 140, viewModel::setSecondaryImageUri)
                }

                FilterList(
                    content = {
                        item {
                            CombineButton(
                                text = "Add",
                                onClick = {
                                    viewModel.combineImagesLauncher(combinationId.ADD)
                                }
                            )
                            FilterListSpacer()
                        }
                        item {
                            CombineButton(
                                text = "Subtract",
                                onClick = {
                                    viewModel.combineImagesLauncher(combinationId.SUBTRACT)
                                }
                            )
                            FilterListSpacer()
                        }
                        item {
                            CombineButton(
                                text = "Multiply",
                                onClick = {
                                    viewModel.combineImagesLauncher(combinationId.MULTIPLY)
                                }
                            )
                            FilterListSpacer()
                        }
                        item {
                            CombineButton(
                                text = "Divide",
                                onClick = {
                                    viewModel.combineImagesLauncher(combinationId.DIVIDE)
                                }
                            )
                            FilterListSpacer()
                        }
                        item {
                            CombineButton(
                                text = "Max",
                                onClick = {
                                    viewModel.combineImagesLauncher(combinationId.MAX)
                                }
                            )
                            FilterListSpacer()
                        }
                        item {
                            CombineButton(
                                text = "Min",
                                onClick = {
                                    viewModel.combineImagesLauncher(combinationId.MIN)
                                }
                            )
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun CombineButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        onClick = {
            onClick()
        },
    ) {
        Text(
            text = text
        )
    }
}