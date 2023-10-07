package com.gold24park.dialogqueue

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.gold24park.dialogqueue.ui.theme.DialogQueueTheme
import com.gold24park.dialogqueue.dialog_util.ConfirmAge
import com.gold24park.dialogqueue.dialog_util.SelectAge

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DialogQueueTheme {
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val ageResult = dialogHandler.showDialogForResult(SelectAge)
                    ageResult.onSuccess { age ->
                        Toast.makeText(context, "Age: $age", Toast.LENGTH_LONG).show()
                        dialogHandler.showDialog(
                            ConfirmAge, ConfirmAge.Req(
                                exp = age,
                                onClickNavigate = {
                                    startActivity(Intent(context, SecondaryActivity::class.java))
                                }
                            ))
                    }.onFailure {
                        Toast.makeText(context, "failed to receive user age.", Toast.LENGTH_LONG).show()
                    }
                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Yellow
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Button(onClick = {
                                startActivity(Intent(context, SecondaryActivity::class.java))
                            }) {
                                Text("Secondary Activity")
                            }
                        }
                    }
                }
            }
        }
    }
}