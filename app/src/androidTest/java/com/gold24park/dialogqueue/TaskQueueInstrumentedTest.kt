package com.gold24park.dialogqueue

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gold24park.dialogqueue.task_util.Task
import com.gold24park.dialogqueue.task_util.TaskQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskQueueInstrumentedTest {

    private lateinit var scenario: ActivityScenario<ThirdActivity>
    private lateinit var lifecycleScope: CoroutineScope
    private lateinit var taskQueue: TaskQueue
    @Before
    fun setup() {
        scenario = launchActivity<ThirdActivity>()
        scenario.onActivity { activity ->
            taskQueue = TaskQueue(activity)
            lifecycleScope = activity.lifecycleScope
        }
    }

    @Test
    fun should_run_task_sequentially() {
        // given
        val completedTaskIds = mutableListOf<Int>()
        val createTask: (id: Int) -> Task<Int> = { id ->
            Task(
                label = "task", {
                    completedTaskIds.add(id)
                    it.complete(id)
                },
                runner = lifecycleScope
            )
        }

        // when
        taskQueue.add(createTask(1))
        taskQueue.add(createTask(2))
        taskQueue.add(createTask(3))

        Thread.sleep(1000)

        // then
        Assert.assertEquals(mutableListOf(1, 2, 3), completedTaskIds)
    }

    @Test
    fun should_return_correct_value() {
        // given
        val task = Task(label = "task", execute = { completable ->
            completable.complete(5)
        }, runner = lifecycleScope)

        // when/then
        lifecycleScope.launch {
            val result = taskQueue.addForResult(task)
            Assert.assertEquals(5, result.getOrNull())
        }
    }

    @Test
    fun should_not_run_when_runner_is_not_active() {
        // given
        val scope = CoroutineScope(Dispatchers.Default)

        val task = Task(label = "task", execute = { completable ->
            completable.complete(Unit)
        }, runner = scope)

        // when/then
        scope.cancel()
        lifecycleScope.launch {
            val result = taskQueue.addForResult(task)
            Assert.assertEquals(true, result.isFailure)
        }
    }

}