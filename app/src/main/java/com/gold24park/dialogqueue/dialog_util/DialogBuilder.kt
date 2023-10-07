package com.gold24park.dialogqueue.dialog_util

import android.content.Context
import kotlinx.coroutines.CompletableDeferred

sealed interface DialogBuilder<Req, Res> {
    fun build(
        context: Context,
        req: Req,
        deferred: CompletableDeferred<Res>,
    ): QueueDialogFragment<Res>
}