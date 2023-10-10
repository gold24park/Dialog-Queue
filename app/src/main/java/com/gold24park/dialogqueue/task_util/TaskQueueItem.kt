package com.gold24park.dialogqueue.task_util

import kotlinx.coroutines.CompletableDeferred

data class TaskQueueItem<T: Any?>(
    val task: Task<T>,
    val deferred: CompletableDeferred<T>,
)