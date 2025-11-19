package com.vocabulary

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Test rule for setting up and tearing down Dispatchers.Main for coroutine tests
 * Usage: @get:Rule val coroutinesTestRule = CoroutinesTestRule()
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CoroutinesTestRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}
