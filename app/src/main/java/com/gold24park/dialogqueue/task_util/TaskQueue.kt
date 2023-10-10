package com.gold24park.dialogqueue.task_util

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.LinkedList

class TaskQueue(
    lifecycleOwner: LifecycleOwner,
) {
    companion object {
        private var processingTask: Task<*>? = null
        private val queue = LinkedList<TaskQueueItem<*>>()
        private const val TAG = "TaskQueue"
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

            val item = queue.poll()!!

            check(item.task.runner.isActive) {
                val message = "runner is not active for task: ${item.task.label}"
                item.deferred.completeExceptionally(IllegalStateException(message))
                message
            }

            processingTask = item.task

            item.deferred.invokeOnCompletion {
                processingTask = null
                tryPoll()
            }

            item.task.runner.launch {
                item.process()
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "failed to poll: ${e.message}")
        }
    }

    suspend fun <T: Any?> addForResult(task: Task<T>): Result<T> {
        val deferred = CompletableDeferred<T>()
        queue.add(TaskQueueItem(
            task = task,
            deferred = deferred,
        ))
        tryPoll()
        return runCatching {
            deferred.await()
        }
    }

    fun <T : Any?> add(task: Task<T>) {
        val deferred = CompletableDeferred<T>()
        queue.add(TaskQueueItem(
            task = task,
            deferred = deferred,
        ))
        tryPoll()
    }

    private suspend fun <T: Any?> TaskQueueItem<T>.process() = task.execute(deferred)

}