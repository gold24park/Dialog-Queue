package com.gold24park.dialogqueue

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.gold24park.dialogqueue.dialog_util.Text
import com.gold24park.dialogqueue.dialog_util.DialogQueue

class SecondaryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            Column(modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)) {
                startActivity(Intent(context, ThirdActivity::class.java))
            }
        }
    }
}