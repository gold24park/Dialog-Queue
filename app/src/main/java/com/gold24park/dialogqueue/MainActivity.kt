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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.gold24park.dialogqueue.ui.theme.DialogQueueTheme
import com.gold24park.dialogqueue.dialog_util.ConfirmAge
import com.gold24park.dialogqueue.dialog_util.SelectAge
import com.gold24park.dialogqueue.dialog_util.Text
import com.gold24park.dialogqueue.task_util.Task

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DialogQueueTheme {
                val context = LocalContext.current
                var tryCnt by remember { mutableStateOf(1) }
                LaunchedEffect(tryCnt) {
                    val task = Task<Int>(label = "get age", execute = { completable ->
                        val ageResult = dialogQueue.pushForResult(SelectAge)
                        ageResult.onSuccess { age ->
                            dialogQueue.pushForResult(
                                Text,
                                "Confirm user age: $age"
                            )
                            completable.complete(age)
                        }.onFailure {
                            Toast.makeText(context, "failed to receive user age.", Toast.LENGTH_LONG).show()
                            completable.completeExceptionally(Exception("failed to receive user age."))
                        }
                    })
                    val age = taskQueue.push(task)
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
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Button(onClick = {
                                tryCnt++
                            }) {
                                Text("Inc++ try count: $tryCnt")
                            }
                        }
                    }
                }
            }
        }
    }
}