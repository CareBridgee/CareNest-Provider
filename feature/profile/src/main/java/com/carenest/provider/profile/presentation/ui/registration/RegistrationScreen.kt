package com.carenest.provider.profile.presentation.ui.registration

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.components.stepper.HorizontalStepper
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.profile.presentation.ui.registration.component.PersonalInfoComponent
import com.carenest.provider.profile.presentation.ui.registration.component.ServicesSelectionComponent
import com.carenest.provider.profile.presentation.ui.registration.component.VerificationDocumentsComponent
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onNavigateToApplicationUnderReview: () -> Unit,
    registrationViewmodel: RegistrationViewmodel = hiltViewModel()
) {
    val state by registrationViewmodel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    ObserveEffect(registrationViewmodel.effect) {
        when (it) {
            RegistrationEffect.NavigateToNextStep -> {
                if (pagerState.currentPage < 2) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onNavigateToApplicationUnderReview()
                }
            }
            RegistrationEffect.OpenFilePicker -> { /* Handle file picker */ }
            RegistrationEffect.ShowCalendar -> { /* Handle calendar */ }
            RegistrationEffect.ShowMessage -> { /* Handle message */ }
        }
    }

    RegistrationContent(
        modifier = modifier.fillMaxSize(),
        state = state,
        onIntent = registrationViewmodel::onIntent,
        pagerState = pagerState
    )
}

@Composable
fun RegistrationContent(
    modifier: Modifier = Modifier,
    state: RegistrationUiState,
    onIntent: (RegistrationIntent) -> Unit,
    pagerState: PagerState = rememberPagerState(pageCount = { 3 })
) {
    Column(modifier = modifier) {
        HorizontalStepper(
            currentStep = pagerState.currentPage + 1,
            totalSteps = 3,
            modifier = Modifier.padding(Theme.spacing.medium)
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> PersonalInfoComponent(
                    state = state.personalInfoState,
                    onFirstNameChanged = { onIntent(RegistrationIntent.OnFirstNameChanged(it)) },
                    onLastNameChanged = { onIntent(RegistrationIntent.OnLastNameChanged(it)) },
                    onDateOfBirthChanged = { onIntent(RegistrationIntent.OnDateOfBirthChanged(it)) },
                    onNationalIdChanged = { onIntent(RegistrationIntent.OnNationalIdChanged(it)) },
                    onGenderClick = { /* Should trigger gender selection dialog/bottomsheet */ },
                    onProfilePhotoClick = { /* Should trigger image picker */ },
                    onContinueClick = { onIntent(RegistrationIntent.OnContinueClicked) }
                )
                1 -> VerificationDocumentsComponent(
                    state = state.verificationDocumentsUiState,
                    onNationalIdClick = { /* Trigger file picker for National ID */ },
                    onNursingLicenseClick = { /* Trigger file picker for Nursing License */ },
                    onProfessionalCertificateClick = { /* Trigger file picker for Prof. Certificate */ },
                    onYearsOfExpChanged = { onIntent(RegistrationIntent.OnYearsOfExpChanged(it)) },
                    onPrimarySpecialityChanged = { onIntent(RegistrationIntent.OnPrimarySpecialityChanged(it)) },
                    onContinueClick = { onIntent(RegistrationIntent.OnContinueClicked) }
                )
                2 -> ServicesSelectionComponent(
                    state = state.servicesUiState,
                    onServiceToggle = { onIntent(RegistrationIntent.OnServiceToggle(it)) },
                    onContinueClick = { onIntent(RegistrationIntent.OnContinueClicked) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationContentPreview() {
    SpTheme {
        RegistrationContent(
            state = RegistrationUiState().copy(
                stepperState = StepperState(remainingSteps = 1)
            ),
            onIntent = {}
        )
    }
}
