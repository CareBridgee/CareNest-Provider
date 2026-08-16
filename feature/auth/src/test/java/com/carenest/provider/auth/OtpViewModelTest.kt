package com.carenest.provider.auth

import com.carenest.provider.auth.domain.model.AuthenticatedNurse
import com.carenest.provider.auth.domain.model.AuthenticatedUser
import com.carenest.provider.auth.domain.model.GoogleLoginResult
import com.carenest.provider.auth.domain.repository.AuthRepository
import com.carenest.provider.auth.domain.usecase.AuthenticationDestinationResolver
import com.carenest.provider.auth.domain.usecase.DevLoginUseCase
import com.carenest.provider.auth.domain.usecase.ResolveAuthenticationDestinationUseCase
import com.carenest.provider.auth.domain.usecase.VerifyOtpUseCase
import com.carenest.provider.auth.presentation.auth.otp.OtpIntent
import com.carenest.provider.auth.presentation.auth.otp.OtpViewModel
import com.carenest.provider.core.datastore.AuthenticationCredentials
import com.carenest.provider.core.datastore.AuthenticationSession
import com.carenest.provider.core.datastore.AuthenticationSessionStore
import com.carenest.provider.core.datastore.AuthenticationState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OtpViewModelTest {
    private lateinit var dispatcher: TestDispatcher
    private lateinit var authRepository: FakeOtpAuthRepository

    @Before
    fun setUp() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        authRepository = FakeOtpAuthRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun countdownStartsAtThirtyAndDecreasesWithoutRealWaiting() = runTest(dispatcher) {
        val viewModel = viewModel()

        assertEquals(30, viewModel.state.value.remainingSeconds)
        advanceTimeBy(1_000)
        runCurrent()
        assertEquals(29, viewModel.state.value.remainingSeconds)
        advanceUntilIdle()
        assertEquals(0, viewModel.state.value.remainingSeconds)
        assertTrue(viewModel.state.value.canResend)
    }

    @Test
    fun resendIsUnavailableBeforeCountdownCompletes() = runTest(dispatcher) {
        val viewModel = viewModel()
        viewModel.onEvent(OtpIntent.PhoneNumberChanged("+201027642749"))

        viewModel.onEvent(OtpIntent.ResendClicked)
        runCurrent()

        assertEquals(0, authRepository.requestCount)
        assertFalse(viewModel.state.value.canResend)
        advanceUntilIdle()
    }

    @Test
    fun successfulResendRunsOnceAndRestartsCountdown() = runTest(dispatcher) {
        authRepository.otpResult = Result.success("123456")
        val viewModel = viewModel()
        viewModel.onEvent(OtpIntent.PhoneNumberChanged("+201027642749"))
        advanceUntilIdle()

        viewModel.onEvent(OtpIntent.ResendClicked)
        viewModel.onEvent(OtpIntent.ResendClicked)
        runCurrent()

        assertEquals(1, authRepository.requestCount)
        assertEquals("+201027642749", authRepository.lastRequestedPhone)
        assertEquals("123456", viewModel.state.value.otpCode)
        assertEquals(30, viewModel.state.value.remainingSeconds)
        assertFalse(viewModel.state.value.canResend)
        advanceUntilIdle()
    }

    @Test
    fun failedResendRemainsRetryableAndDoesNotRestartCountdown() = runTest(dispatcher) {
        authRepository.otpResult = Result.failure(IllegalStateException("offline"))
        val viewModel = viewModel()
        viewModel.onEvent(OtpIntent.PhoneNumberChanged("+966501234567"))
        advanceUntilIdle()

        viewModel.onEvent(OtpIntent.ResendClicked)
        runCurrent()

        assertEquals(1, authRepository.requestCount)
        assertEquals(0, viewModel.state.value.remainingSeconds)
        assertTrue(viewModel.state.value.canResend)
        assertNotNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun otpStateChangesDoNotResetCountdown() = runTest(dispatcher) {
        val viewModel = viewModel()
        advanceTimeBy(5_000)
        runCurrent()
        assertEquals(25, viewModel.state.value.remainingSeconds)

        viewModel.onEvent(OtpIntent.OtpCodeChanged("1"))
        viewModel.onEvent(OtpIntent.OtpCodeChanged("12"))

        assertEquals(25, viewModel.state.value.remainingSeconds)
        advanceUntilIdle()
    }

    private fun viewModel() = OtpViewModel(
        verifyOtpUseCase = VerifyOtpUseCase(authRepository),
        devLoginUseCase = DevLoginUseCase(authRepository),
        resolveDestination = ResolveAuthenticationDestinationUseCase(
            authRepository,
            AuthenticationDestinationResolver(),
        ),
        authenticationSessionStore = FakeAuthenticationSessionStore(),
    )
}

private class FakeOtpAuthRepository : AuthRepository {
    var otpResult: Result<String> = Result.success("123456")
    var requestCount: Int = 0
    var lastRequestedPhone: String? = null

    override suspend fun login(phoneNumber: String): Result<Unit> = Result.success(Unit)

    override suspend fun devLogin(phoneNumber: String): Result<String> {
        requestCount += 1
        lastRequestedPhone = phoneNumber
        return otpResult
    }

    override suspend fun googleLogin(
        idToken: String,
        firstName: String?,
        lastName: String?,
        email: String?,
        profileImageUrl: String?,
    ): Result<GoogleLoginResult> =
        Result.failure(UnsupportedOperationException())

    override suspend fun verifyOtp(
        phoneNumber: String,
        otp: String,
        pendingToken: String?,
    ): Result<AuthenticatedNurse?> = Result.failure(UnsupportedOperationException())

    override suspend fun getCurrentUser(): Result<AuthenticatedUser> =
        Result.success(AuthenticatedUser(profileCompleted = false))
}

private class FakeAuthenticationSessionStore : AuthenticationSessionStore {
    private val authenticationState = MutableStateFlow(AuthenticationState())

    override val state: Flow<AuthenticationState> = authenticationState
    override val session: Flow<AuthenticationSession?> = MutableStateFlow(null)
    override val currentSession: AuthenticationSession?
        get() = authenticationState.value.session

    override suspend fun beginAuthentication(accessToken: String, refreshToken: String) = Unit

    override suspend fun completeAuthentication(
        expectedCredentials: AuthenticationCredentials,
        session: AuthenticationSession,
    ): Boolean = true

    override suspend fun replaceCredentials(
        expectedCredentials: AuthenticationCredentials,
        accessToken: String,
        refreshToken: String,
    ): Boolean = true

    override suspend fun updateProfileImageUrl(
        nurseId: String,
        profileImageUrl: String?,
    ): Boolean {
        val current = authenticationState.value
        val savedSession = current.session ?: return false
        if (savedSession.nurseId != nurseId) return false
        authenticationState.value = current.copy(
            session = savedSession.copy(profileImageUrl = profileImageUrl),
        )
        return true
    }

    override suspend fun clearSession() = Unit

    override suspend fun clearInvalidSession(): Boolean = false

    override suspend fun clearSessionIfCurrent(
        expectedCredentials: AuthenticationCredentials,
    ): Boolean = false
}
