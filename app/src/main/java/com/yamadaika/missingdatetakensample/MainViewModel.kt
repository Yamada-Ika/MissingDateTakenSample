package com.yamadaika.missingdatetakensample

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.database.getLongOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

data class LocalMedia(
    val uri: Uri,
    val tookAt: Long,
)

class MainViewModel : ViewModel() {
    var uiState = MutableStateFlow(
        MainScreenUiState(
            items = listOf(),
        ),
    )
        private set

    private val formatter = SimpleDateFormat("yyyy-MM-dd")

    fun load(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_TAKEN,
                )
                val queryArgs = Bundle()
                val mediaList = mutableListOf<LocalMedia>()
                context
                    .contentResolver
                    .query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        queryArgs,
                        null,
                    )?.use { cursor ->
                        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                        val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                        while (cursor.moveToNext()) {
                            val id = cursor.getLong(idColumn)
                            val uri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id,
                            )
                            val dateTaken = cursor.getLongOrNull(dateTakenColumn)

                            mediaList.add(LocalMedia(uri, dateTaken ?: System.currentTimeMillis()))
                        }
                    }

                uiState.update {
                    val groupedMediaList = mediaList.groupBy {
                        formatter.format(Date(it.tookAt))
                    }
                    val items = groupedMediaList.flatMap {
                        listOf(
                            ItemUiState.Section(date = it.key),
                        ) + it.value.map {
                            ItemUiState.Media(
                                uri = it.uri,
                            )
                        }
                    }
                    it.copy(items = items)
                }
            }
        }
    }
}
