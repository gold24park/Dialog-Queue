package com.gold24park.dialogqueue.dialog_util

import android.content.Context
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import java.util.PriorityQueue

interface DialogQueue {
    fun add(element: DialogQueueElement)

    enum class Priority {
        HIGH,
        MEDIUM,
        LOW,
    }
}

data class DialogQueueElement(
    val priority: DialogQueue.Priority,
    val tag: String,
    val dialogBuilder: (context: Context, dismiss: () -> Unit) -> QueueDialogFragment<*>,
): Comparable<DialogQueueElement> {
    override fun compareTo(other: DialogQueueElement): Int {
        return priority.compareTo(other.priority)
    }
}

class DialogQueueImpl(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val fragmentManager: FragmentManager,
): DialogQueue {

    private var showingElement: DialogQueueElement? = null

    companion object {
        private val queue = PriorityQueue<DialogQueueElement>()
        const val TAG = "DialogQueue"
    }

    init {
        lifecycleOwner.lifecycle.addObserver(object: LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_RESUME -> flush()
                    Lifecycle.Event.ON_PAUSE -> {
                        addFirst(showingElement)
                        showingElement = null
                    }
                    else -> {}
                }
            }
        })
    }

    private fun addFirst(element: DialogQueueElement?) {
        if (element != null) {
            queue.offer(element.copy(
                priority = maxOf(element.priority, DialogQueue.Priority.MEDIUM),
            ))
        }
    }


    override fun add(
        element: DialogQueueElement,
    ) {
        queue.offer(element)
        flush()
        debug()
    }

    private fun flush() {
        try {
            debug()
            check(queue.isNotEmpty())
            check(showingElement == null) {
                "dialog is already showing."
            }
            check(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                "lifecycle must be resumed."
            }

            // show dialog
            val element = queue.poll()!!

            showingElement = element

            val dialog = element.dialogBuilder(context) { // on dismiss
                showingElement = null
                (fragmentManager.findFragmentByTag(element.tag) as? DialogFragment)?.dismiss()
                flush()
            }
            dialog.show(fragmentManager, element.tag)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "failed to flush: $e")
        }
    }

    private fun debug() {
        Log.d(TAG, "log.queue: ${queue.map { it.tag }}")
        Log.d(TAG, "log.showingElement: $showingElement")
    }
}