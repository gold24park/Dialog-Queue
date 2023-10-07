package com.gold24park.dialogqueue.dialog_util

import android.util.Log
import kotlinx.coroutines.CompletableDeferred

interface DialogHandler {
    fun <Req, Res> showDialog(
        builder: DialogBuilder<Req, Res>,
        req: Req,
        priority: DialogQueue.Priority = DialogQueue.Priority.LOW
    )
    suspend fun <Res> showDialogForResult(
        builder: DialogBuilder<Unit, Res>,
        priority: DialogQueue.Priority = DialogQueue.Priority.LOW
    ): Result<Res>

    suspend fun <Req, Res> showDialogForResult(
        builder: DialogBuilder<Req, Res>,
        req: Req, priority: DialogQueue.Priority = DialogQueue.Priority.LOW
    ): Result<Res>
}

class DialogHandlerImpl(
    private val queue: DialogQueue,
): DialogHandler {

    override fun <Req, Res> showDialog(
        builder: DialogBuilder<Req, Res>,
        req: Req,
        priority: DialogQueue.Priority
    ) {
        show(builder = builder, req = req, priority = priority)
    }

    override suspend fun <Req, Res> showDialogForResult(
        builder: DialogBuilder<Req, Res>,
        req: Req,
        priority: DialogQueue.Priority
    ): Result<Res> {
        return runCatching {
            show(builder = builder, req = req, priority = priority).await()
        }
    }

    override suspend fun <Res> showDialogForResult(
        builder: DialogBuilder<Unit, Res>,
        priority: DialogQueue.Priority
    ): Result<Res> {
        return showDialogForResult(builder, Unit)
    }

    private fun <Req, Res> show(
        builder: DialogBuilder<Req, Res>,
        req: Req,
        priority: DialogQueue.Priority
    ): CompletableDeferred<Res> {
        var deferred = CompletableDeferred<Res>()
        val tag = "DialogHandler::${builder::class.java.simpleName}::${req}"
        queue.add(
            DialogQueueElement(
                priority = priority,
                tag = tag,
                dialogBuilder =  { context, dismiss ->
                    Log.d(DialogQueueImpl.TAG, "$tag: ${deferred.isCompleted}")
                    if (deferred.isCompleted) {
                        deferred = CompletableDeferred()
                    }
                    deferred.invokeOnCompletion { dismiss() }
                    builder.build(
                        context = context,
                        req = req,
                        deferred = deferred,
                    )
                }
            )
        )
        return deferred
    }
}