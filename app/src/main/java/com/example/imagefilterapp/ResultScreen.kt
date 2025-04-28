package com.example.imagefilterapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import coil3.compose.AsyncImage

@Preview
@Composable
fun ResultScreenPreview() {
    //ResultScreen(State(activeDialog = null), MainActivityViewModel(), null)
}


@Composable
fun ResultScreen(state: State, viewModel: MainActivityViewModel) {
    BaseUI(
        showBackArrow = true,
        onBackArrowClick = {
            viewModel.navController.navigate(MainScreen)
        },
        showSettingsButton = false,
        showSwitchScreenButton = false,
        showFloatingActionButton = true,
        floatingActionButtonIcon = {
            Icon(
                imageVector = Icons.Filled.Save,
                contentDescription = "Save image"
            )
        },
        onFloatingActionButtonClick = {
            viewModel.onSaveImageButtonClickedLauncher()
        },
        content = { innerPadding ->
            var height = 250
            if (!state.showFilterList) {
                height = 500
            }

            Column (modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                AsyncImage(
                    model = matToBitmap(viewModel.imageProcessor.mainImageMat).let {
                        val size = getTargetBitmapSize(it.width, it.height, 2000)
                        it.scale(size["width"]!!, size["height"]!!)
                    },
                    contentDescription = "Your processed image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(300.dp)
                        .height(height.dp)
                )

                if (state.showFilterList) {
                    FilterList(content = {
                        for (filter in state.addedFilterList) {
                            item {
                                FilterListItem(
                                    filter = filter,
                                    listIndex = state.addedFilterList.indexOf(filter),
                                    isMutable = false
                                )
                                if (filter !== state.addedFilterList.last())
                                    FilterListSpacer()
                            }
                        }
                    })
                }
            }
        }
    )
}