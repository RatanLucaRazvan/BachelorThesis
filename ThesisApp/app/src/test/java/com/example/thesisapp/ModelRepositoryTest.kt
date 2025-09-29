package com.example.thesisapp

import com.example.thesisapp.data.detection_model.ModelApiService
import com.example.thesisapp.data.detection_model.ModelRepository
import com.example.thesisapp.data.detection_model.NewsRequest
import com.example.thesisapp.data.detection_model.PredictionResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.coVerify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ModelRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockModelApiService: ModelApiService

    private lateinit var modelRepository: ModelRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockModelApiService = mockk()

        modelRepository = ModelRepository(mockModelApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getDetection returns prediction response on success`() = runTest(testDispatcher) {
        val request = NewsRequest(content = "This is a test news content.")
        val expectedResponse = PredictionResponse(prediction = "real")

        coEvery { mockModelApiService.getDetection(request) } returns expectedResponse

        val actualResponse = modelRepository.getDetection(request)
        advanceUntilIdle()

        assertEquals(expectedResponse, actualResponse)

        coVerify(exactly = 1) { mockModelApiService.getDetection(request) }
    }

    @Test
    fun `getDetection returns fake prediction when api service returns fake`() =
        runTest(testDispatcher) {
            val request = NewsRequest(content = "This is a potentially fake news.")
            val expectedResponse = PredictionResponse(prediction = "fake")

            coEvery { mockModelApiService.getDetection(request) } returns expectedResponse

            val actualResponse = modelRepository.getDetection(request)
            advanceUntilIdle()

            assertEquals(expectedResponse.prediction, actualResponse.prediction)
            coVerify(exactly = 1) { mockModelApiService.getDetection(request) }
        }

    @Test
    fun `getDetection rethrows exception on api service failure`() = runTest(testDispatcher) {
        val request = NewsRequest(content = "Content that causes API error.")
        val expectedException = RuntimeException("Network connection failed")

        coEvery { mockModelApiService.getDetection(request) } throws expectedException

        var caughtException: Throwable? = null
        try {
            modelRepository.getDetection(request)
            advanceUntilIdle()
        } catch (e: Exception) {
            caughtException = e
        }

        assertNotNull(caughtException)
        assertEquals(expectedException.message, caughtException?.message)

        coVerify(exactly = 1) { mockModelApiService.getDetection(request) }
    }
}