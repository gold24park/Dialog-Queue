package com.gold24park.dialogqueue

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gold24park.dialogqueue.dialog_util.Text
import com.gold24park.dialogqueue.dialog_util.DialogQueue

class SecondaryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)) {

            }
        }
    }
}