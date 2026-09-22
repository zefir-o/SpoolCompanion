package com.hexxotest.spoolcompanion

import com.hexxotest.spoolcompanion.network.normalizeSpoolmanUrl
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun normalizesSpoolmanUrls() {
        assertEquals(
            "http://trident.local:7912",
            normalizeSpoolmanUrl("trident.local")
        )
        assertEquals(
            "http://trident.local:8123",
            normalizeSpoolmanUrl("http://trident.local:8123/")
        )
        assertEquals(
            "https://trident.local",
            normalizeSpoolmanUrl("https://trident.local/")
        )
    }
}