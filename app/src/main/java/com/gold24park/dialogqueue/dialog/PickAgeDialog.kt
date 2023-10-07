package com.gold24park.dialogqueue.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun PickAgeDialog(
    onPick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(20.dp),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Button(onClick = { onPick(Random.nextInt(100)) }) {
            Text("Pick Your Age (Random)")
        }
    }
}