package com.herz.appturismo.Componentes

import android.graphics.BitmapFactory
import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.compose.ui.unit.dp
import java.io.File
import androidx.compose.foundation.lazy.items
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import android.media.MediaMetadataRetriever
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import com.herz.appturismo.ui.theme.Aqua50

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(navController: NavHostController) {

    val context = LocalContext.current
    val photosDirectory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val videosDirectory = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)

    val photoFiles = photosDirectory?.listFiles()?.toList() ?: emptyList()
    val videoFiles = videosDirectory?.listFiles()?.toList() ?: emptyList()

    val mediaFiles = (photoFiles + videoFiles).sortedByDescending { it.lastModified() }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(bottom = 22.dp),
                title = { Text("Galería") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Aqua50,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (mediaFiles.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 20.dp, end = 20.dp)
            ) {
                items(mediaFiles) { mediaFile ->
                    val extension = mediaFile.extension.lowercase()
                    if (extension == "jpg") {
                        val bitmap = BitmapFactory.decodeFile(mediaFile.absolutePath)?.asImageBitmap()
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = "Foto",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    } else if (extension == "mp4") {
                        VideoItem(mediaFile, navController)
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay fotos o videos para mostrar.")
            }
        }
    }
}

@Composable
fun VideoItem(videoFile: File, navController: NavHostController) {
    val context = LocalContext.current

    // Estado para almacenar la miniatura
    var thumbnail by remember { mutableStateOf<ImageBitmap?>(null) }

    // Obtener la miniatura en un efecto secundario
    LaunchedEffect(videoFile) {
        val bitmap = getVideoThumbnail(videoFile)
        thumbnail = bitmap?.asImageBitmap()
    }

    Card(
        modifier = Modifier.padding(bottom = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0ECE3)
        )
    ) {

        // Contenedor del item
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp)
                .clickable {
                    val encodedPath = URLEncoder.encode(videoFile.absolutePath, StandardCharsets.UTF_8.toString())
                    navController.navigate("video_player/$encodedPath")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (thumbnail != null) {
                Image(
                    bitmap = thumbnail!!,
                    contentDescription = "Miniatura del Video",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                // Placeholder mientras se carga la miniatura
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.Gray)
                )
            }
            Text(
                text = "Video: ${videoFile.name}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
    }


}

fun getVideoThumbnail(videoFile: File): Bitmap? {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(videoFile.absolutePath)
        return retriever.frameAtTime // Obtiene el fotograma en el tiempo 0
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        retriever.release()
    }
    return null
}
