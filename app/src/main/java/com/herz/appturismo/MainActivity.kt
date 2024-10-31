package com.herz.appturismo

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.herz.appturismo.Componentes.CameraSettingsScreen
import com.herz.appturismo.Componentes.GalleryScreen
import com.herz.appturismo.Componentes.MainMenuScreen
import com.herz.appturismo.Componentes.NavRoutes
import com.herz.appturismo.Componentes.PhotoCaptureScreen
import com.herz.appturismo.Componentes.VideoPlayerScreen
import com.herz.appturismo.Componentes.VideoRecordingScreen
import com.herz.appturismo.ui.theme.AppTurismoTheme
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTurismoTheme {

                val navController = rememberNavController()
                SetupNavGraph(navController = navController)

            }
        }
    }
}

@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavRoutes.MainMenu.route) {
        composable(NavRoutes.MainMenu.route) {
            MainMenuScreen(navController)
        }
        composable(NavRoutes.PhotoCapture.route) {
            PhotoCaptureScreen(navController)
        }
        composable(NavRoutes.VideoRecording.route) {
            VideoRecordingScreen(navController)
        }
        composable(NavRoutes.CameraSettings.route) {
            CameraSettingsScreen(navController)
        }
        composable(NavRoutes.Gallery.route) {
            GalleryScreen(navController)
        }
        composable(
            route = NavRoutes.VideoPlayer.route,
            arguments = listOf(navArgument("videoPath") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedPath = backStackEntry.arguments?.getString("videoPath")
            val videoPath = encodedPath?.let { Uri.decode(it) }
            if (videoPath != null) {
                VideoPlayerScreen(navController, videoPath)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppTurismoTheme {

    }
}