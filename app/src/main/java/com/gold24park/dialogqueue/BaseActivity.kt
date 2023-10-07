package com.gold24park.dialogqueue

import androidx.appcompat.app.AppCompatActivity
import com.gold24park.dialogqueue.dialog_util.DialogHandlerImpl
import com.gold24park.dialogqueue.dialog_util.DialogQueueImpl

abstract class BaseActivity : AppCompatActivity() {

    protected val dialogHandler = DialogHandlerImpl(
        queue = DialogQueueImpl(
            context = this,
            fragmentManager = this.supportFragmentManager,
            lifecycleOwner = this,
        )
    )
}