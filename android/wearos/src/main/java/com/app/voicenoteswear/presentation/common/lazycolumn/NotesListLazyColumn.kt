package com.app.voicenoteswear.presentation.common.lazycolumn

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.app.voicenoteswear.R
import com.app.voicenoteswear.data.apimodels.response.Recording
import com.app.voicenoteswear.data.apimodels.response.RecordingStatus
import com.app.voicenoteswear.presentation.theme.Blue
import com.app.voicenoteswear.presentation.theme.GreenTransparent50
import com.app.voicenoteswear.presentation.theme.NoteDate
import com.app.voicenoteswear.presentation.theme.NoteDuration
import com.app.voicenoteswear.presentation.theme.NoteSynced
import com.app.voicenoteswear.presentation.theme.NoteTitle
import com.app.voicenoteswear.presentation.theme.NoteTranscript
import com.app.voicenoteswear.presentation.theme.TranscriptingAudio
import com.app.voicenoteswear.presentation.theme.White1Opacity50
import com.app.voicenoteswear.utils.formatDateString
import com.app.voicenoteswear.utils.formatDurationString

@Composable
fun NotesListLazyColumn(
    listState: ScalingLazyListState = rememberScalingLazyListState(),
    notes: List<Recording>,
    onItemClick: (note: Recording) -> Unit,
    onLogout: () -> Unit,
    onLoadMoreItems: () -> Unit
) {
    val contentModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 4.dp)

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        autoCentering = AutoCenteringParams(itemIndex = 0),
        state = listState
    ) {
        items(notes) {
            TextItem(
                contentModifier,
                title = it.title,
                content = it.transcript,
                date = formatDateString(it.createdAt),
                duration = formatDurationString(it.duration),
                note = it,
                onItemClick
            )
        }
        item {
            LaunchedEffect(Unit) {
                onLoadMoreItems.invoke()
            }
        }
//        item {
//            CustomChip(text = "Logout", onCLick = {
//                onLogout.invoke()
//            })
//        }
    }
}

@Composable
fun TextItem(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: String? = null,
    date: String = "",
    duration: String = "",
    note: Recording,
    onItemClick: (note: Recording) -> Unit
) {
    Card(
        onClick = { onItemClick(note) },
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        shape = RoundedCornerShape(12.dp),
    ) {

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.baseline_play_circle_outline), contentDescription = "", tint = White1Opacity50)
                        Text(
                            text = duration,
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle.NoteDuration
                        )
                    }
                }
                Text(
                    text = date,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle.NoteDate
                )
            }
            if (title != null && content != null) {
                Text(title, textAlign = TextAlign.Start, maxLines = 1, overflow = TextOverflow.Ellipsis, style = TextStyle.NoteTitle)
                if (note.status == RecordingStatus.SYNCED) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_access_time_14),
                            contentDescription = "",
                            tint = Blue
                        )
                        Text(
                            "Synced and transcribed",
                            textAlign = TextAlign.Start,
                            maxLines = 2,
                            style = TextStyle.NoteSynced
                        )
                    }
                    Text(
                        "when you\'re back online....",
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        style = TextStyle.NoteSynced
                    )
                } else {
                    Text(
                        content,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        style = TextStyle.NoteTranscript
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    DotsTyping()
                }
                Text(stringResource(id = R.string.record_transcripting), textAlign = TextAlign.Start, style = TextStyle.TranscriptingAudio)
            }
        }
    }
}

val dotSize = 4.dp // made it bigger for demo
val delayUnit = 400 // you can change delay to change animation speed
@Composable
fun DotsTyping() {
    val maxOffset = 3f

    @Composable
    fun Dot(
        offset: Float
    ) = Spacer(
        Modifier
            .size(dotSize)
            .offset(y = -offset.dp)
            .background(
                color = GreenTransparent50,
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition()

    @Composable
    fun animateOffsetWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delayUnit * 4
                0f at delay with LinearEasing
                maxOffset at delay + delayUnit with LinearEasing
                0f at delay + delayUnit * 2
            }
        )
    )

    val offset1 by animateOffsetWithDelay(0)
    val offset2 by animateOffsetWithDelay(delayUnit)
    val offset3 by animateOffsetWithDelay(delayUnit * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = maxOffset.dp)
    ) {
        val spaceSize = 2.dp

        Dot(offset1)
        Spacer(Modifier.width(spaceSize))
        Dot(offset2)
        Spacer(Modifier.width(spaceSize))
        Dot(offset3)
    }
}