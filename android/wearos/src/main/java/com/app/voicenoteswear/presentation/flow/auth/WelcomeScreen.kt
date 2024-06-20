package com.app.voicenoteswear.presentation.flow.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.app.voicenoteswear.R
import com.app.voicenoteswear.presentation.common.CustomChip
import com.app.voicenoteswear.presentation.theme.Welcome

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = hiltViewModel(),
    onLogin: () -> Unit
) {
    val state = viewModel.state.collectAsState(WelcomeViewModel.State.Loading)
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.state.collect {
            when (it) {
                is WelcomeViewModel.State.AuthError -> {
                    loading = false
                    Log.d("WelcomeScreen", "AuthError")
                }
                WelcomeViewModel.State.Loading -> {
                    loading = true
                }
                WelcomeViewModel.State.LoginSuccess -> {
                    loading = false
                    onLogin.invoke()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    modifier = Modifier.size(38.dp),
                    painter = painterResource(id = R.drawable.ic_welcome_phone),
                    contentDescription = ""
                )

                Text(
                    text = "Please login from the mobile app to continue",
                    maxLines = 2,
                    overflow = TextOverflow.Visible,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = TextStyle.Welcome
                )
                CustomChip(
                    text = "Refresh",
                    onCLick = {
                        viewModel.login()
                    }
                )
            }
        }
    }
}