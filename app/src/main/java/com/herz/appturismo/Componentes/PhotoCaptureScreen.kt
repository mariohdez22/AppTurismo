package com.herz.appturismo.Componentes

import android.Manifest
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.herz.appturismo.R
import com.herz.appturismo.ui.theme.Aqua50
import com.herz.appturismo.ui.theme.Gray50
import java.io.File
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoCaptureScreen(navController: NavHostController) {

    val context = LocalContext.current

    // Estado para el permiso de cámara
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Solicitar el permiso de cámara al entrar en la composición
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Verificar el estado del permiso de cámara
    when (cameraPermissionState.status) {
        is PermissionStatus.Granted -> {

            // Mostrar la interfaz de captura de fotos
            PhotoCaptureContent(navController)
        }
        is PermissionStatus.Denied -> {

            // Mostrar una UI para solicitar el permiso
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Se requiere permiso de cámara para usar esta función.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    cameraPermissionState.launchPermissionRequest()
                }) {
                    Text("Solicitar Permiso")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoCaptureContent(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var flashMode by remember { mutableStateOf(ImageCapture.FLASH_MODE_OFF) }

    // Selector de cámara (trasera)
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    // Estado para el previewView
    val previewView = remember { PreviewView(context) }

    // Variable para almacenar el zoom actual
    var zoomState by remember { mutableStateOf(0f) } // valor inicial de zoom

    // Variable para acceder a CameraControl
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    // Estado para ImageCapture (actualizado para recrearse cuando flashMode cambia)
    val imageCapture = remember(flashMode) {
        ImageCapture.Builder()
            .setFlashMode(flashMode)
            .build()
    }

    // Iniciar CameraX
    LaunchedEffect(lensFacing, flashMode) {
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
                imageCapture
            )

            cameraControl = camera.cameraControl

        } catch (exc: Exception) {
            Log.e("Camera", "Error al abrir la cámara", exc)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Capturar Foto") },
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


                // Botón para alternar entre modos de flash
                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 16.dp, start = 16.dp)
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.7f), shape = CircleShape)
                ) {
                    val flashIcon = when (flashMode) {
                        ImageCapture.FLASH_MODE_OFF -> R.drawable.flash_off
                        ImageCapture.FLASH_MODE_ON -> R.drawable.flash_on
                        ImageCapture.FLASH_MODE_AUTO -> R.drawable.flash_auto
                        else -> R.drawable.flash_off
                    }
                    Icon(
                        painter = painterResource(id = flashIcon),
                        contentDescription = "Flash",
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
                        CameraSelector.LENS_FACING_BACK -> R.drawable.camera_front
                        CameraSelector.LENS_FACING_FRONT -> R.drawable.camera_back
                        else -> R.drawable.camera_front
                    }
                    Icon(
                        painter = painterResource(id = cameraIcon), // Usa un ícono representativo
                        contentDescription = "Cambiar Cámara",
                        tint = Color.Black
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    // Botón de captura
                    IconButton(
                        onClick = {
                            // Lógica para capturar la foto
                            val photoFile = createFile(context)

                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        // La foto ha sido guardada con éxito
                                        Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        // Error al guardar la foto
                                        Toast.makeText(context, "Error al guardar la foto: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        },
                        modifier = Modifier
                            .padding(bottom = 0.dp)
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                    ) {
                        Icon(painter = painterResource(id = R.drawable.screenshot), contentDescription = "Capturar Foto", tint = Color.Black)
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


fun createFile(context: Context): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    if (storageDir != null && !storageDir.exists()) {
        storageDir.mkdirs()
    }

    return File(storageDir, "JPEG_${timestamp}.jpg")
}

// Extensión para obtener el CameraProvider
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
    val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
    cameraProviderFuture.addListener({
        continuation.resume(cameraProviderFuture.get())
    }, ContextCompat.getMainExecutor(this))
}