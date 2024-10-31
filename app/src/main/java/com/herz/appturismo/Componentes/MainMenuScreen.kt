package com.herz.appturismo.Componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.herz.appturismo.R
import com.herz.appturismo.ui.theme.AppTurismoTheme
import com.herz.appturismo.ui.theme.Aqua50
import com.herz.appturismo.ui.theme.Gray50
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(navController: NavHostController) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        drawerContent = {
            Box(
                modifier = Modifier
                    .width(250.dp) // Ajusta el ancho del menú a un tamaño personalizado
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                DrawerContent(navController = navController, closeDrawer = {
                    scope.launch {
                        drawerState.close()
                    }
                })
            }
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "TourismApp",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Justify,
                            fontWeight = FontWeight.Bold // Hace el texto en negrita
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {

                            scope.launch {
                                drawerState.open()
                            }

                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Aqua50,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MenuCard(
                        icon = R.drawable.camera,
                        label = "Camara",
                        onClick = { navController.navigate(NavRoutes.PhotoCapture.route) }
                    )
                    MenuCard(
                        icon = R.drawable.videocam,
                        label = "Grabación",
                        onClick = { navController.navigate(NavRoutes.VideoRecording.route) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MenuCard(
                        icon = R.drawable.galery,
                        label = "Galería",
                        onClick = { navController.navigate(NavRoutes.Gallery.route) }
                    )
                    MenuCard(
                        icon = R.drawable.info,
                        label = "Acerca de",
                        onClick = { navController.navigate(NavRoutes.CameraSettings.route) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFADC9AD), // Color de fondo de la tarjeta
                        )
                    ){

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "¡Dedicada para turismo!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Image(
                                painter = painterResource(id = R.drawable.siete_maravillas_del_mundo),
                                contentDescription = "Descripción de la imagen", // Importante para accesibilidad
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Ahora tus vacaciones quedaran grabadas para siempre con esta aplicación",
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.White,
                            )

                        }
                    }

                }

            }
        }

    }
}

@Composable
fun DrawerContent(navController: NavHostController, closeDrawer: () -> Unit) {

    Column(modifier = Modifier.padding(top = 42.dp, start = 25.dp, end = 16.dp)) {

        Text(
            text = "Opciones",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Gray50,
            modifier = Modifier.padding(bottom = 25.dp)
        )
        DrawerItem(label = "Cámara", iconRes = R.drawable.camera) {
            navController.navigate(NavRoutes.PhotoCapture.route)
            closeDrawer()
        }
        DrawerItem(label = "Grabación", iconRes = R.drawable.videocam) {
            navController.navigate(NavRoutes.VideoRecording.route)
            closeDrawer()
        }
        DrawerItem(label = "Galería", iconRes = R.drawable.galery) {
            navController.navigate(NavRoutes.Gallery.route)
            closeDrawer()
        }
    }
}

@Composable
fun DrawerItem(label: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Gray50,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 6.dp),
        )
        Text(text = label, fontSize = 20.sp, color = Gray50)
    }
}

@Composable
fun MenuCard(icon: Int, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFABD7B1), // Color de fondo de la tarjeta
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MainMenuScreenPreview() {

    AppTurismoTheme {

        val navController = rememberNavController()
        MainMenuScreen(navController)
    }
}
