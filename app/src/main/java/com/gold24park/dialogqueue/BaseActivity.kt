package com.gold24park.dialogqueue

import androidx.appcompat.app.AppCompatActivity
import com.gold24park.dialogqueue.dialog_util.DialogQueue
import com.gold24park.dialogqueue.task_util.TaskQueue

abstract class BaseActivity : AppCompatActivity() {

    protected val taskQueue = TaskQueue(lifecycleOwner = this)

    protected val dialogQueue = DialogQueue(
        context = this,
        fragmentManager = this.supportFragmentManager,
        lifecycleOwner = this,
    )
}