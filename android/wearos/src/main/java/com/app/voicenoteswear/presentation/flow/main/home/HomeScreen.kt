package com.app.voicenoteswear.presentation.flow.main.home

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import com.app.voicenoteswear.R
import com.app.voicenoteswear.data.apimodels.response.Recording
import com.app.voicenoteswear.data.apimodels.response.RecordingStatus
import com.app.voicenoteswear.presentation.common.lazycolumn.NotesListLazyColumn
import com.app.voicenoteswear.presentation.flow.shared.NoteSharedViewModel
import com.app.voicenoteswear.presentation.theme.DarkGray
import com.app.voicenoteswear.presentation.theme.Opacity50Size10White1
import com.app.voicenoteswear.utils.AndroidAudioPlayer
import com.app.voicenoteswear.utils.AndroidAudioRecorder
import com.app.voicenoteswear.utils.ConnectivityObserver
import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    refresh: Boolean = false,
    sharedViewModel: NoteSharedViewModel,
    navigateToNoteDetail: () -> Unit,
    onLogout: () -> Unit
) {
    val state = viewModel.state.collectAsState(HomeViewModel.State.Loading)
    var loading by remember { mutableStateOf(false) }

    val connectivityObserver = ConnectivityObserver(context = LocalContext.current)
    var isConnected by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var isStarted by remember { mutableStateOf(false) }
    var maxAmplitude = remember { mutableStateOf(8) }
    var shouldRefresh by remember { mutableStateOf(refresh) }

    var showBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    var showGotItDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var showSureToCancelDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val timerState by viewModel.timerState.collectAsState()
    val stopWatchText by viewModel.stopWatchText.collectAsState()

    val appContext = LocalContext.current.applicationContext
    val recorder by lazy {
        AndroidAudioRecorder(appContext)
    }
    val player by lazy {
        AndroidAudioPlayer(appContext)
    }

    var notesList by rememberSaveable {
        mutableStateOf<List<Recording>>(emptyList())
    }

    if (shouldRefresh) {
        viewModel.getAllRecordings()
        shouldRefresh = false
    }

    val sheetScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isConnected = true
            Log.d("connectivityObserver", "onAvailable")
        }

        // Network capabilities have changed for the network
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            Log.d("connectivityObserver", "onCapabilitiesChanged")

            val unmetered = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        }

        // lost network connection
        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("connectivityObserver", "onLost")

            isConnected = false
        }
    }

    LaunchedEffect(Unit) {
        connectivityObserver.observeConnectivity().collect {
            Log.d("connectivityObserver", "networkState = $it")
            isConnected = it
        }
    }


    LaunchedEffect(key1 = true) {
        viewModel.state.collect {
            when (it) {
                is HomeViewModel.State.Error -> {
                    loading = false
                    Log.d("HomeScreen", "Error")
                    if (!isConnected) viewModel.getFilesFromDb()
                }
                HomeViewModel.State.Loading -> {
                    loading = true
                    Log.d("HomeScreen", "Loading")
                }

                is HomeViewModel.State.FetchNotesSuccess -> {
                    loading = false
                    Log.d("HomeScreen", "fetched notes: ${it.notes}")
                    notesList = it.notes
                    if (isConnected) {
                        Log.d("HomeScreen", "syncFilesFromDbIfNeeded")
                        viewModel.syncFilesFromDbIfNeeded()
                    }
                }
                HomeViewModel.State.LogoutSuccess -> {
                    loading = false
                    onLogout.invoke()
                }

                is HomeViewModel.State.StoreAudioSuccess -> {
                    loading = false
                    viewModel.addTranscript(it.recordingId)
                    viewModel.addTitle(it.recordingId)
                    viewModel.getAllRecordings()
                }

                HomeViewModel.State.AddTitleSuccess -> {
                    loading = false
                    viewModel.getAllRecordings()
                }
                HomeViewModel.State.AddTransciptSuccess -> {
                    loading = false
                    viewModel.getAllRecordings()
                }

                is HomeViewModel.State.RetrieveFilePathsSuccess -> {
                    Log.d("HomeScreen", "RetrieveFilePathsSuccess")
                    var updatedList = listOf<Recording>()
                    it.files.forEach {
                        val note = Recording(
                            id = it.id.toString(),
                            recordingId = "",
                            title = "New Recording",
                            transcript = "Synced and transcribed when you\'re back online",
                            duration = 1000,
                            createdAt = "2024-06-20T18:46:12.000000Z"
                        )
                        note.status = RecordingStatus.SYNCED
                        if (!notesList.contains(note)) {
                            updatedList = updatedList + listOf(note)
                        }
                    }

                    notesList = updatedList + notesList
                }

                is HomeViewModel.State.LoadMoreItemsSuccess -> {
                    if (it.notes.isNotEmpty()) {
                        notesList = notesList + it.notes
                    }
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.padding(start = 16.dp),
                        painter = painterResource(id = R.drawable.text_voicenotes),
                        contentDescription = ""
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colors.background),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val listState = rememberScalingLazyListState()

                if (!isConnected) {
                    Image(
                        painter = painterResource(id = R.drawable.text_offline_mode),
                        contentDescription = ""
                    )
                }

                NotesListLazyColumn(
                    listState = listState,
                    notes = notesList,
                    onItemClick = {
                        if (it.status == RecordingStatus.SYNCED) {
                            showGotItDialog = true
                        } else {
                            sharedViewModel.updateNoteData(it)
                            navigateToNoteDetail.invoke()
                        }
                    },
                    onLogout = {
                        viewModel.logout()
                    },
                    onLoadMoreItems = {
                        viewModel.loadMoreItems()
                    }
                )
            }
        }

        Image(
            modifier = Modifier
                .padding(top = 4.dp, bottom = 14.dp)
                .clickable {
                    showBottomSheet = true
                }
                .align(Alignment.BottomCenter),
            painter = painterResource(id = R.drawable.button_record),
            contentDescription = ""
        )
    }

    if (showGotItDialog) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    BasicAlertDialog(
                        properties = DialogProperties(
                            usePlatformDefaultWidth = true,
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        onDismissRequest = {
                            showGotItDialog = false
                        }
                    ) {
                        Gravity.BOTTOM.let {
                            val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
                            dialogWindowProvider.window.setGravity(it)
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkGray)
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.text_dont_worry),
                                contentDescription = ""
                            )
                            Image(
                                modifier = Modifier.clickable {
                                    showGotItDialog = false
                                },
                                painter = painterResource(id = R.drawable.button_got_it),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
        }

    if (showSureToCancelDialog) {

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {
                    BasicAlertDialog(
                        properties = DialogProperties(
                            usePlatformDefaultWidth = true,
                            dismissOnBackPress = false,
                            dismissOnClickOutside = false
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(127.dp)
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        onDismissRequest = {
                            showSureToCancelDialog = false
                        }
                    ) {
                        Gravity.BOTTOM.let {
                            val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
                            dialogWindowProvider.window.setGravity(it)
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(DarkGray)
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.text_sure_to_cancel),
                                contentDescription = ""
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                Image(
                                    modifier = Modifier
                                        .clickable {
                                            showBottomSheet = false
                                            viewModel.resetTimer()
                                            recorder.stop()
                                            isRecording = false
                                            showSureToCancelDialog = false
                                        },
                                    painter = painterResource(id = R.drawable.btn_yes_cancel),
                                    contentDescription = ""
                                )
                                Image(
                                    modifier = Modifier
                                        .clickable {
                                            showSureToCancelDialog = false
//                                        showBottomSheet = false
//                                            isRecording = false
                                        },
                                    painter = painterResource(id = R.drawable.btn_no_continue),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicAlertDialog(
                    properties = DialogProperties(
                        usePlatformDefaultWidth = true,
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(127.dp)
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    onDismissRequest = {
                        showBottomSheet = false
                    }
                ) {
                    Gravity.BOTTOM.let {
                        val dialogWindowProvider = LocalView.current.parent as DialogWindowProvider
                        dialogWindowProvider.window.setGravity(it)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DarkGray)
                            .padding(top = 16.dp, start = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isRecording) {
                                    Image(
                                        modifier = Modifier.padding(start = 12.dp),
                                        painter = painterResource(id = R.drawable.text_rec),
                                        contentDescription = ""
                                    )
                                } else {
                                    Image(
                                        modifier = Modifier.padding(start = 12.dp),
                                        painter = painterResource(id = R.drawable.text_paused),
                                        contentDescription = ""
                                    )
                                }
                            }
                            Image(painter = painterResource(id = R.drawable.ic_red_dot), contentDescription = "")
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                modifier = Modifier.padding(end = 16.dp),
                                text = stopWatchText,
                                textAlign = TextAlign.Start,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle.Opacity50Size10White1
                            )
                        }

                        if (isRecording) Animation(maxAmplitude.value) else AnimationStop()

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Image(
                                modifier = Modifier
                                    .clickable {
//                                        showBottomSheet = false
                                        viewModel.toggleIsRunning()
                                        showSureToCancelDialog = true
                                        isRecording = false
                                },
                                painter = painterResource(id = R.drawable.btn_cancel),
                                contentDescription = ""
                            )
                            if (isRecording) {
                                Image(
                                    modifier = Modifier.clickable {
                                        isRecording = false
                                        viewModel.toggleIsRunning()
                                        recorder.pause()
                                        recorder.isRecording = false
                                    },
                                    painter = painterResource(id = R.drawable.btn_pause),
                                    contentDescription = ""
                                )
                            } else {
                                Image(
                                    modifier = Modifier.clickable {
                                        viewModel.toggleIsRunning()
                                        if (isStarted) {
                                            recorder.resume()
                                        } else {
                                            if (!isConnected) {
                                                val fileName = (0..10000000000).random()
                                                File(appContext.cacheDir, "audio$fileName.mp3").also {
                                                    recorder.start(it)
                                                    viewModel.audioFile = it
                                                    recorder.audioFile = it
                                                }
                                            } else {
                                                File(appContext.cacheDir, "audio.mp3").also {
                                                    recorder.start(it)
                                                    viewModel.audioFile = it
                                                    recorder.audioFile = it
                                                }
                                            }

                                            Log.d("start writing file", "audiofile ${viewModel.audioFile}")
                                        }

                                        isRecording = true
                                        val handler = Handler(Looper.getMainLooper())
                                        handler.post(object : Runnable {
                                            override fun run() {
                                                if (isRecording) {
                                                    val amplitude = recorder.getAmplitude()
                                                    maxAmplitude.value =  if (amplitude <= 8) 8 else if (amplitude <= 100) 12 else if (amplitude <= 500) 16 else 24
                                                    // Update UI with maxAmplitude
                                                    Log.d("VoiceAmplitude1", "Max Amplitude: $maxAmplitude")
                                                    handler.postDelayed(this, 30) // Update every 100ms
                                                }
                                            }
                                        })
                                    },
                                    painter = painterResource(id = R.drawable.btn_play),
                                    contentDescription = ""
                                )
                            }
                            Image(
                                modifier = Modifier.clickable {
                                    Log.d("HomeScreen", "audio ${viewModel.audioFile}")
//player.playFile(recorder.audioFile!!)
//                                    audioFile?.let {
//                                        Log.d("HomeScreen", "audio isplaying")
//                                        player.playFile(it)
//                                    }
                                    showBottomSheet = false
                                    isRecording = false
                                    recorder.stop()
                                    viewModel.resetTimer()
                                    if (isConnected) {
                                        try {
                                            viewModel.audioFile?.let { file ->
                                                viewModel.storeAudio(file, 4000)
                                            }
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }
                                    } else {
                                        viewModel.audioFile?.let { file ->
                                            viewModel.insertFile(file)
                                        }
                                        viewModel.getAllRecordings()
                                    }
                                    viewModel.audioFile = null
                                },
                                painter = painterResource(id = R.drawable.btn_accept),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Animation(maxAmplitude: Int) {
    Box(
        modifier = Modifier
            .height(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val min = 8
        val max = 24
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            repeat(5) {
//                val max =
//                    remember(boolean(times = 60, interval = 100)) { 24 }
                val animatedAmplitude by animateDpAsState(
                    targetValue = maxAmplitude.dp,
                    label = ""
                )
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(animatedAmplitude)
                        .clip(RoundedCornerShape(27.dp))
                        .background(Color.White)
                )
            }
        }
    }
}

@Composable
private fun AnimationStop() {
    Box(
        modifier = Modifier
            .height(16.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val min = 8
        val max = 8
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            repeat(5) {
                val amplitude =
                    remember(boolean(times = 0, interval = 100)) { 8 }
                val animatedAmplitude by animateDpAsState(
                    targetValue = amplitude.dp,
                    label = ""
                )
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(animatedAmplitude)
                        .clip(RoundedCornerShape(27.dp))
                        .background(Color.White)
                )
            }
        }
    }
}

@Composable
fun boolean(
    initial: Boolean = false,
    times: Int = 3,
    interval: Long = 2000,
): Boolean = produceState(initial) {
    repeat(times) {
        delay(interval)
        value = !value
    }
}.value