package com.niclauscott.jetdrive.features.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.niclauscott.jetdrive.core.ui.component.JetDriveButton
import com.niclauscott.jetdrive.core.ui.util.percentOfScreenHeight
import com.niclauscott.jetdrive.features.auth.domain.model.dto.LoginRequestDTO
import com.niclauscott.jetdrive.features.auth.domain.repository.AuthRepository
import kotlinx.coroutines.launch
import org.koin.compose.getKoin
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


class Dependencies: KoinComponent {
    val authRepository: AuthRepository = get()
    val address: String = get()
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val authRepository: AuthRepository = getKoin().get()
    val address: String = getKoin().get()
    val scope = rememberCoroutineScope()
    var isLoginIn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Profile", style = MaterialTheme.typography.titleLarge)

        Text("Address: $address", style = MaterialTheme.typography.labelMedium)

        Spacer(modifier = Modifier.height(1.percentOfScreenHeight()))

        JetDriveButton(
            text = "Login",
            isLoginIn = isLoginIn
        ) {
            scope.launch {
                isLoginIn = true
                authRepository.login(
                    loginRequestDTO = LoginRequestDTO("info@abc.com", "300819Nas"))
                isLoginIn = false
            }
        }

    }
}