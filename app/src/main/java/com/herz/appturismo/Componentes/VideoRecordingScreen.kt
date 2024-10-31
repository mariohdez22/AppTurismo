package com.herz.appturismo.Componentes

import android.Manifest
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.herz.appturismo.R
import com.herz.appturismo.ui.theme.Aqua50
import com.herz.appturismo.ui.theme.Gray50
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideoRecordingScreen(navController: NavHostController) {

    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val audioPermissionState = rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

    // Solicitar permisos al entrar en la composición
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
        if (!audioPermissionState.status.isGranted) {
            audioPermissionState.launchPermissionRequest()
        }
    }

    // Verificar si los permisos han sido concedidos
    if (cameraPermissionState.status.isGranted && audioPermissionState.status.isGranted) {

        // Mostrar la interfaz de grabación de video
        VideoRecordingContent(navController)

    } else {

        // Mostrar una UI para solicitar los permisos
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Se requieren permisos de cámara y audio para usar esta función.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                cameraPermissionState.launchPermissionRequest()
                audioPermissionState.launchPermissionRequest()
            }) {
                Text("Solicitar Permisos")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoRecordingContent(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estados para CameraX
    val videoCapture = remember {
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        VideoCapture.withOutput(recorder)
    }

    var torchEnabled by remember { mutableStateOf(false) }

    // Selector de cámara (trasera)
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    // Estado para el previewView
    val previewView = remember { PreviewView(context) }

    // Estado para controlar la grabación
    var isRecording by remember { mutableStateOf(false) }
    var recording: Recording? = null

    // Variable para almacenar el zoom actual
    var zoomState by remember { mutableStateOf(0f) }

    // Variable para acceder a CameraControl
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    // Iniciar CameraX
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()

        // Preview
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        try {
            // Unbind use cases before rebinding
            cameraProvider.unbindAll()

            // Bind use cases to camera
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                videoCapture
            )

            // Accedemos a cameraControl
            cameraControl = camera.cameraControl

            // Aplicamos el estado actual de la torch
            cameraControl?.enableTorch(torchEnabled)

        } catch (exc: Exception) {
            Log.e("Camera", "Error al abrir la cámara", exc)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grabar Video") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main_menu") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Aqua50,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Aqua50,
                modifier = Modifier.height(25.dp)
            ){}
        },
        content = { paddingValues ->

            // UI de la pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Vista de la cámara
                AndroidView(
                    factory = { previewView },
                    modifier = Modifier.fillMaxSize()
                )

                // Botón para alternar la torch
                IconButton(
                    onClick = {
                        torchEnabled = !torchEnabled
                        cameraControl?.enableTorch(torchEnabled)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 16.dp, start = 16.dp)
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                ) {
                    val torchIcon = if (torchEnabled) {
                        R.drawable.flash_on
                    } else {
                        R.drawable.flash_off
                    }
                    Icon(
                        painter = painterResource(id = torchIcon),
                        contentDescription = "Torch",
                        tint = Color.Black
                    )
                }

                // Botón para alternar entre cámaras
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 16.dp)
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                ) {
                    val cameraIcon = when (lensFacing) {
                        CameraSelector.LENS_FACING_FRONT -> R.drawable.camera_back
                        CameraSelector.LENS_FACING_BACK -> R.drawable.camera_front
                        else -> R.drawable.camera_front
                    }

                    Icon(
                        painter = painterResource(id = cameraIcon),
                        contentDescription = "Cambiar Cámara",
                        tint = Color.Black
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){

                    // Botón de grabación
                    IconButton(
                        onClick = {
                            if (isRecording) {
                                // Detener la grabación
                                recording?.stop()
                            } else {
                                // Iniciar la grabación
                                val videoFile = createVideoFile(context)
                                val mediaStoreOutput = FileOutputOptions.Builder(videoFile).build()
                                recording = videoCapture.output
                                    .prepareRecording(context, mediaStoreOutput)
                                    .apply {
                                        if (PermissionChecker.checkSelfPermission(
                                                context,
                                                Manifest.permission.RECORD_AUDIO
                                            ) == PermissionChecker.PERMISSION_GRANTED
                                        ) {
                                            withAudioEnabled()
                                        }
                                    }
                                    .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                                        when (recordEvent) {
                                            is VideoRecordEvent.Start -> {
                                                isRecording = true
                                            }
                                            is VideoRecordEvent.Finalize -> {
                                                if (!recordEvent.hasError()) {
                                                    Toast.makeText(context, "Video guardado", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    recording?.close()
                                                    recording = null
                                                    Log.e("Camera", "Error al grabar video: ${recordEvent.error}")
                                                }
                                                isRecording = false
                                            }
                                        }
                                    }
                            }
                        },
                        modifier = Modifier
                            .padding(bottom = 0.dp)
                            .size(64.dp)
                            .background(
                                if (isRecording) Color.Red.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f),
                                shape = MaterialTheme.shapes.medium
                            )
                    ) {
                        Icon(
                            painter = if (isRecording) painterResource(id = R.drawable.camera_stop) else painterResource(id = R.drawable.camera_play),
                            contentDescription = if (isRecording) "Detener Grabación" else "Iniciar Grabación",
                            tint = if (isRecording) Color.White else Color.Black
                        )
                    }

                    // Slider de zoom
                    cameraControl?.let { control ->
                        Slider(
                            value = zoomState,
                            onValueChange = {
                                zoomState = it
                                control.setLinearZoom(it)
                            },
                            valueRange = 0f..1f,
                            steps = 10,
                            colors = MaterialTheme.colorScheme.primary.let {
                                SliderDefaults.colors(
                                    thumbColor = Aqua50,
                                    activeTrackColor = it.copy(alpha = 0.9f, 0.5f, 0.9f, 0.7f),
                                    inactiveTrackColor = it.copy(alpha = 0.4f, 0.5f, 0.9f, 0.8f)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        )
                    }
                }

            }
        }
    )
}

// Función para crear el archivo de video
fun createVideoFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)

    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }

    return File(storageDir, "VIDEO_${timestamp}.mp4")
}

// Extensión para obtener el CameraProvider
suspend fun Context.getVideoProvider(): ProcessCameraProvider = suspendCancellableCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        continuation.resume(cameraProviderFuture.get())
    }, ContextCompat.getMainExecutor(this))
}