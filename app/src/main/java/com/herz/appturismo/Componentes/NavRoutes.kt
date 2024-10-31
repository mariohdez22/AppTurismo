package com.herz.appturismo.Componentes

sealed class NavRoutes(val route: String) {

    object MainMenu : NavRoutes("main_menu")
    object PhotoCapture : NavRoutes("photo_capture")
    object VideoRecording : NavRoutes("video_recording")
    object CameraSettings : NavRoutes("camera_settings")
    object Gallery : NavRoutes("gallery")
    object VideoPlayer : NavRoutes("video_player/{videoPath}") {
        fun createRoute(videoPath: String) = "video_player/$videoPath"
    }
}