package com.gold24park.dialogqueue.task_util

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.LinkedList

class TaskQueue(
    private val lifecycleOwner: LifecycleOwner,
) {
    companion object {
        var processingTask: Task<*>? = null
        val queue = LinkedList<TaskQueueItem<*>>()
        const val TAG = "TaskQueue"
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
            check(queue.isNotEmpty()) {
                "queue is empty."
            }

            check(processingTask == null) {
                "there is already processing task: ${processingTask?.label}"
            }

            check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                "lifecycle must be resumed."
            }

            val item = queue.poll()!!
            processingTask = item.task

            item.deferred.invokeOnCompletion {
                Log.d(TAG, "invokeOnCompletion()")
                processingTask = null
                tryPoll()
            }

            lifecycleOwner.lifecycleScope.launch {
                Log.d(TAG, "process before delay: ${item.task.label}")
                delay(1000)
                Log.d(TAG, "process: ${item.task.label}")
                item.process()
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "failed to poll: ${e.message}")
        }
    }

    suspend fun <T: Any?> pushForResult(task: Task<T>): Result<T> {
        val deferred = CompletableDeferred<T>()
        queue.push(TaskQueueItem(
            task = task,
            deferred = deferred,
        ))
        tryPoll()
        return runCatching {
            deferred.await()
        }
    }

    fun <T : Any?> push(task: Task<T>) {
        val deferred = CompletableDeferred<T>()
        queue.push(TaskQueueItem(
            task = task,
            deferred = deferred,
        ))
        tryPoll()
    }

    private suspend fun <T: Any?> TaskQueueItem<T>.process() = withContext(task.coroutineDispatcher) {
        task.execute(deferred)
    }

}