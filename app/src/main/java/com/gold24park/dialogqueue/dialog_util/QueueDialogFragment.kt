package com.gold24park.dialogqueue.dialog_util

import android.content.DialogInterface
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CompletableDeferred

abstract class QueueDialogFragment<T>(
    private val deferred: CompletableDeferred<T>,
): DialogFragment() {

    override fun onPause() {
        super.onPause()
        dismiss()
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        deferred.completeExceptionally(IllegalStateException("Dialog dismissed"))
    }
}