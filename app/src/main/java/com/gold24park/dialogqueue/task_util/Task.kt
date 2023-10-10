package com.gold24park.dialogqueue.task_util

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

data class Task<T: Any?>(
    val label: String, // Task label for debugging
    val execute: suspend (completable: CompletableDeferred<T>) -> Unit,
    val runner: CoroutineScope, // CoroutineScope for executing the task
)