package com.yamadaika.missingdatetakensample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import com.yamadaika.missingdatetakensample.ui.theme.MissingDateTakenSampleTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {
    var uiState = MutableStateFlow(
        MainScreenUiState(
            items = listOf(),
        ),
    )
        private set

    fun load() {}
}

class MainActivity : ComponentActivity() {
    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.load()
            }
        }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel = MainViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                viewModel.load()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                viewModel.load()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        setContent {
            MissingDateTakenSampleTheme {
                val uiState by viewModel.uiState.collectAsState()

                MainScreen(
                    uiState = uiState
                )
            }
        }
    }
}
