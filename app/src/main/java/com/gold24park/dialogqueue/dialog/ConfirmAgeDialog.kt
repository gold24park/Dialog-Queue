package com.gold24park.dialogqueue.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmAgeDialog(
    age: Int,
    onClickNavigate: () -> Unit,
    dismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.White)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Confirm Age: $age", color = Color.Black)

        Row(
            modifier = Modifier.align(Alignment.BottomEnd),
        ) {
            Button(
                onClick = onClickNavigate,
            ) {
                Text("Next")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = dismiss,
            ) {
                Text("Dismiss")
            }
        }
    }
}