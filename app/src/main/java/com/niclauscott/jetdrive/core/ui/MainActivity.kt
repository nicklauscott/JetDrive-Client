package com.niclauscott.jetdrive.core.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.niclauscott.jetdrive.core.ui.navigation.NavigationRoot
import com.niclauscott.jetdrive.core.ui.theme.JetDriveTheme
import com.niclauscott.jetdrive.core.splash.domain.SplashScreenViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    private val splashScreenViewModel: SplashScreenViewModel = get()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawable(null)
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            !splashScreenViewModel.validationComplete
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge(/* statusBarStyle = SystemBarStyle.dark(0) */)
        setContent {
            JetDriveTheme {
                NavigationRoot(splashScreenViewModel = splashScreenViewModel)
            }
        }
    }
}

