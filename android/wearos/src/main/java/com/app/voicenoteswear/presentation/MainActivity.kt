/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.app.voicenoteswear.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.app.voicenoteswear.navigation.AppNavigation
import com.app.voicenoteswear.navigation.auth.authGraph
import com.app.voicenoteswear.navigation.main.mainGraph
import com.app.voicenoteswear.presentation.theme.VoiceNotesWearOSTheme
import dagger.hilt.android.AndroidEntryPoint
import android.Manifest
import com.app.voicenoteswear.R
import com.app.voicenoteswear.data.datastorage.UserDataStorage
import com.app.voicenoteswear.presentation.flow.shared.NoteSharedViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener{

    @Inject
    lateinit var userDataStorage: UserDataStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        Wearable.getMessageClient(this).addListener(this)
        setContent {
            WearApp()
        }
    }

    override fun onMessageReceived(p0: MessageEvent) {
        Log.d("MessageReceiverService", "Message received p0: $")

        if (p0.path == "/token_path") {
            val message = String(p0.data)
            Log.d("MessageReceiverService", "Message received p0: $message")
            userDataStorage.accessToken = message
        }
    }
}

@Composable
fun WearApp() {
    VoiceNotesWearOSTheme {
        /* If you have enough items in your list, use [ScalingLazyColumn] which is an optimized
         * version of LazyColumn for wear devices with some added features. For more information,
         * see d.android.com/wear/compose.
         */

        val viewModel: MainActivityViewModel = viewModel()
        val noteSharedViewModel: NoteSharedViewModel = viewModel()

        val navController = rememberNavController()
        val currentBackStackEntry = navController.currentBackStackEntryAsState().value

        val currentDestination = currentBackStackEntry?.destination
        val startDestination = when (viewModel.startDestination) {
            DirectionState.Auth -> AppNavigation.AuthFlow
            DirectionState.Main -> AppNavigation.MainFlow
            else -> null
        }
        Log.d("MyApp", "Starting destination: ${startDestination?.route}")
        startDestination?.let { destination ->
            NavHost(
                navController = navController,
                startDestination = destination.route
            ) {
                authGraph(navController)
                mainGraph(navController, noteSharedViewModel)
            }
        }
    }
}

@Composable
fun Greeting(greetingName: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = MaterialTheme.colors.primary,
        text = stringResource(R.string.hello_world, greetingName)
    )
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}