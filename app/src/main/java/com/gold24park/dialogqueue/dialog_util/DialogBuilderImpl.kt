package com.gold24park.dialogqueue.dialog_util

import android.content.Context
import com.gold24park.dialogqueue.dialog.ComposeDialog
import com.gold24park.dialogqueue.dialog.ConfirmAgeDialog
import com.gold24park.dialogqueue.dialog.PickAgeDialog
import com.gold24park.dialogqueue.dialog.TextDialog
import kotlinx.coroutines.CompletableDeferred

object SelectAge : DialogBuilder<Unit, Int> {
    override fun build(
        context: Context,
        req: Unit,
        deferred: CompletableDeferred<Int>,
    ): QueueDialogFragment<Int> {
        return ComposeDialog(
            content = {
                PickAgeDialog(onPick = { age ->
                    deferred.complete(age)
                })
            },
            deferred = deferred,
        )
    }
}

object ConfirmAge : DialogBuilder<ConfirmAge.Req, Unit> {

    data class Req(
        val exp: Int,
        val onClickNavigate: () -> Unit,
    )

    override fun build(
        context: Context,
        req: Req,
        deferred: CompletableDeferred<Unit>,
    ): QueueDialogFragment<Unit> {
        return ComposeDialog(
            content = {
                ConfirmAgeDialog(
                    age = req.exp,
                    onClickNavigate = {
                        req.onClickNavigate()
                        deferred.complete(Unit)
                    },
                    dismiss = {
                        deferred.complete(Unit)
                    }
                )
            },
            deferred = deferred,
        )
    }
}

object Text : DialogBuilder<String, Unit> {

    override fun build(
        context: Context,
        req: String,
        deferred: CompletableDeferred<Unit>,
    ): QueueDialogFragment<Unit> {
        return ComposeDialog(
            content = {
                TextDialog(
                    text = req,
                    dismiss = {
                        deferred.complete(Unit)
                    }
                )
            },
            deferred = deferred,
        )
    }

}