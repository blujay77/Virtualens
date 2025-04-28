package com.example.imagefilterapp

/*
1. Skelett bauen
2. Image importieren und anzeigen
3. simples OpenCV script ausführen
    -hinzugefügte operanden in liste/distionary aus distionaries speichern
    - operator = [ {operator: "sobel", ksize: "3"}, {operator: "grayscale"} ]
    - for i in range len(operator):
        executeOperator(operators[i])
    - executeOperators (dict values) {
        switch(values[operator]):
            case "sobel":
                ...
            case "grayscale":
                ...
4. Image speichern
5. Alle OpenCV scripts implementieren

*/

// TODO Save image to gallery
// TODO Fix savedHandle crash after process death
// TODO Preferences, toggle CV_32F, image downscaling


import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.imagefilterapp.ui.theme.ImageFilterAppTheme
import kotlinx.serialization.Serializable
import org.opencv.android.OpenCVLoader


const val app_name = "Virtualens"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {

            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            OpenCVLoader.initLocal()

            ImageFilterAppTheme {
                val navController = rememberNavController()

                val viewModel = viewModel<MainActivityViewModel>(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T: ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                            return MainActivityViewModel(
                                this@MainActivity,
                                navController,
                                extras.createSavedStateHandle(),
                                this@MainActivity.contentResolver,
                            ) as T
                        }
                    }
                )

                NavHost(
                    navController = navController,
                    startDestination = MainScreen
                ) {
                    composable<MainScreen> {
                        MainScreen(viewModel.state, viewModel)
                    }
                    composable<ResultScreen> {
                        ResultScreen(viewModel.state, viewModel)
                    }
                    composable<CombinationScreen> {
                        CombinationScreen(viewModel.state, viewModel)
                    }
                }
            }
        }
    }
}

@Serializable
object MainScreen

@Serializable
object ResultScreen

@Serializable
object CombinationScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseUI(
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit = {},
    showFloatingActionButton: Boolean,
    showSettingsButton: Boolean,
    onSettingsButtonClicked: () -> Unit = {},
    showSwitchScreenButton: Boolean,
    onSwitchScreenButtonClicked: () -> Unit = {},
    floatingActionButtonIcon: @Composable ()-> Unit = {},
    onFloatingActionButtonClick: () -> Unit = {},
    content: @Composable (innerPadding: PaddingValues) -> Unit
) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = app_name, color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    if (showBackArrow) {
                        IconButton(
                            onClick = {
                                onBackArrowClick()
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    }
                },
                actions = {
                    if (showSettingsButton) {
                        IconButton(
                            onClick = {
                                onSettingsButtonClicked()
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.Build,
                                    contentDescription = "Settings",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    }
                    if (showSwitchScreenButton) {
                        IconButton(
                            onClick = {
                                onSwitchScreenButtonClicked()
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.SwapHoriz,
                                    contentDescription = "Swap screen",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            if (showFloatingActionButton) {
                FloatingActionButton(
                    onClick = {
                        onFloatingActionButtonClick()
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    content = floatingActionButtonIcon
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        content(innerPadding)
    }
}