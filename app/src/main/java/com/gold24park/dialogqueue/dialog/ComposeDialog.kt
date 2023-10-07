package com.gold24park.dialogqueue.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.gold24park.dialogqueue.R
import com.gold24park.dialogqueue.dialog_util.QueueDialogFragment
import com.gold24park.dialogqueue.ui.theme.DialogQueueTheme
import kotlinx.coroutines.CompletableDeferred

class ComposeDialog<T>(
    val content: @Composable () -> Unit,
    deferred: CompletableDeferred<T>,
) : QueueDialogFragment<T>(deferred) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_compose, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        composeView.setContent {
            DialogQueueTheme {
                content()
            }
        }
        return view
    }
}