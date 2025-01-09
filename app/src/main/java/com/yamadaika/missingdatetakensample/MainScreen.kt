package com.yamadaika.missingdatetakensample

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yamadaika.missingdatetakensample.ui.theme.MissingDateTakenSampleTheme

sealed interface ItemUiState {
    data class Media(
        val uri: Uri,
    ) : ItemUiState

    data class Section(
        val date: String,
    ) : ItemUiState
}

data class MainScreenUiState(
    val items: List<ItemUiState>,
)

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    uiState: MainScreenUiState,
) {
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        LazyVerticalGrid(
            modifier = Modifier.padding(innerPadding),
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(
                items = uiState.items,
                span = { item ->
                    when (item) {
                        is ItemUiState.Media -> GridItemSpan(1)
                        is ItemUiState.Section -> GridItemSpan(maxLineSpan)
                    }
                },
            ) { item ->
                when (item) {
                    is ItemUiState.Media -> {
                        MediaImage(
                            modifier = Modifier.aspectRatio(1f),
                            uri = item.uri,
                        )
                    }
                    is ItemUiState.Section -> {
                        TookAtSection(
                            uiState = item,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TookAtSection(
    uiState: ItemUiState.Section,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            text = uiState.date,
        )
    }
}

@Composable
private fun MediaImage(
    uri: Uri,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Canvas(modifier = modifier.size(100.dp)) { drawRect(color = Color.Gray) }
        return
    }

    val resolver = LocalContext.current.contentResolver
    val bitmap = resolver.loadThumbnail(uri, android.util.Size(400, 400), null)
    Image(
        modifier = modifier,
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
@Preview(showBackground = true)
private fun MainScreenPreview() {
    MissingDateTakenSampleTheme {
        MainScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = MainScreenUiState(
                items = listOf(
                    ItemUiState.Section(date = "2024-3-14"),
                    ItemUiState.Media(uri = Uri.parse("")),
                    ItemUiState.Section(date = "2024-2-10"),
                    ItemUiState.Media(uri = Uri.parse("")),
                    ItemUiState.Media(uri = Uri.parse("")),
                    ItemUiState.Media(uri = Uri.parse("")),
                    ItemUiState.Media(uri = Uri.parse("")),
                    ItemUiState.Section(date = "2022-1-28"),
                    ItemUiState.Media(uri = Uri.parse("")),
                ),
            )
        )
    }
}