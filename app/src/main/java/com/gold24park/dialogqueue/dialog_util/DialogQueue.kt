package com.gold24park.dialogqueue.dialog_util

import android.content.Context
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CompletableDeferred
import java.util.LinkedList
data class DialogQueueElement(
    val tag: String,
    val dialogBuilder: (context: Context, dismiss: () -> Unit) -> QueueDialogFragment<*>,
)

class DialogQueue(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val fragmentManager: FragmentManager,
) {

    private var showingElement: DialogQueueElement? = null

    companion object {
        private val queue = LinkedList<DialogQueueElement>()
        const val TAG = "DialogQueue"
    }

    init {
        lifecycleOwner.lifecycle.addObserver(object: LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> tryPoll()
                    else -> {}
                }
            }
        })
    }

    private fun tryPoll() {
        try {
            Log.d(TAG, "log.queue: ${queue.map { it.tag }}")
            Log.d(TAG, "log.showingElement: $showingElement")

            check(queue.isNotEmpty()) {
                "queue is empty."
            }
            check(showingElement == null) {
                "dialog is already showing."
            }
            check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                "lifecycle must be resumed: $context"
            }

            // show dialog
            val element = queue.poll()!!

            showingElement = element

            val dialog = element.dialogBuilder(context) { // on dismiss
                showingElement = null
                (fragmentManager.findFragmentByTag(element.tag) as? DialogFragment)?.dismiss()
                tryPoll()
            }
            dialog.show(fragmentManager, element.tag)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "failed to poll: $e")
        }
    }

    fun <Req, Res> push(
        builder: DialogBuilder<Req, Res>,
        req: Req,
    ) {
        process(builder = builder, req = req)
    }

    suspend fun <Req, Res> pushForResult(
        builder: DialogBuilder<Req, Res>,
        req: Req,
    ): Result<Res> {
        return runCatching {
            process(builder = builder, req = req).await()
        }
    }

    suspend fun <Res> pushForResult(
        builder: DialogBuilder<Unit, Res>,
    ): Result<Res> {
        return pushForResult(builder, Unit)
    }

    private fun <Req, Res> process(
        builder: DialogBuilder<Req, Res>,
        req: Req,
    ): CompletableDeferred<Res> {
        var deferred = CompletableDeferred<Res>()
        val tag = "DialogHandler::${builder::class.java.simpleName}::${req}"
        queue.add(
            DialogQueueElement(
                tag = tag,
                dialogBuilder =  { context, dismiss ->
                    Log.d(TAG, "$tag: ${deferred.isCompleted}")
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
        tryPoll()
        return deferred
    }
}