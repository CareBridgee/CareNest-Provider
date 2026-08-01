package com.carenest.provider.profile.presentation.ui.registration

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carenest.provider.core.mvi.ObserveEffect
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.components.stepper.HorizontalStepper
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.components.topbar.CareNestTopBar
import com.carenest.provider.designsystem.components.topbar.TopBarLeading
import com.carenest.provider.profile.presentation.ui.registration.component.ApplicationReviewComponent
import com.carenest.provider.profile.presentation.ui.registration.component.PersonalInfoComponent
import com.carenest.provider.profile.presentation.ui.registration.component.ServicesSelectionComponent
import com.carenest.provider.profile.presentation.ui.registration.component.VerificationDocumentsComponent
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment as AlignmentUI
import com.carenest.provider.designsystem.components.bottomsheet.BaseBottomSheet
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHostState
import com.carenest.provider.designsystem.components.toast.SnackbarHost
import com.carenest.provider.designsystem.components.toast.showSnack
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.button.SecondaryButton
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.carenest.provider.profile.R as ProfileR
import com.carenest.provider.designsystem.R as DesignR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    modifier: Modifier = Modifier,
    onNavigateToApplicationUnderReview: () -> Unit,
    registrationViewmodel: RegistrationViewmodel = hiltViewModel()
) {
    val state by registrationViewmodel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    var showGenderSheet by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    BackHandler(enabled = pagerState.currentPage > 0) {
        registrationViewmodel.onIntent(RegistrationIntent.OnBackClicked)
    }

    if (showGenderSheet) {
        BaseBottomSheet(
            onDismissRequest = { showGenderSheet = false },
            title = stringResource(ProfileR.string.select_gender)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.spacing.medium)
            ) {
                Gender.entries.filter { it != Gender.UNKNOWN }.forEach { gender ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                registrationViewmodel.onIntent(
                                    RegistrationIntent.OnGenderChanged(
                                        gender
                                    )
                                )
                                showGenderSheet = false
                            }
                            .padding(vertical = Theme.spacing.small),
                        verticalAlignment = AlignmentUI.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
                    ) {
                        RadioButton(
                            selected = state.personalInfoState.gender == gender,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Theme.colors.primary,
                                unselectedColor = Theme.colors.hint
                            )
                        )
                        BasicText(
                            text = gender.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.primaryFont
                            )
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                        sdf.timeZone = TimeZone.getTimeZone("UTC")
                        val date = sdf.format(Date(millis))
                        registrationViewmodel.onIntent(RegistrationIntent.OnDateOfBirthChanged(date))
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(id = android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { registrationViewmodel.onIntent(RegistrationIntent.OnProfilePhotoPicked(it)) }
    }

    val nidPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = getFileName(context, it)
            registrationViewmodel.onIntent(
                RegistrationIntent.OnNationalIdDocumentPicked(
                    Attachment(
                        it,
                        name = fileName,
                        mimeType = context.contentResolver.getType(it) ?: ""
                    )
                )
            )
        }
    }

    val certificatePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = getFileName(context, it)
            registrationViewmodel.onIntent(
                RegistrationIntent.OnProfessionalCertificatePicked(
                    Attachment(
                        it,
                        name = fileName,
                        mimeType = context.contentResolver.getType(it) ?: "",
                    )
                )
            )
        }
    }

    val licensePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val fileName = getFileName(context, it)
            registrationViewmodel.onIntent(
                RegistrationIntent.OnNursingLicensePicked(
                    Attachment(
                        it,
                        name = fileName,
                        mimeType = context.contentResolver.getType(it) ?: "",
                    )
                )
            )
        }
    }

    ObserveEffect(registrationViewmodel.effect) {
        when (it) {
            RegistrationEffect.NavigateToNextStep -> {
                if (pagerState.currentPage < 3) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onNavigateToApplicationUnderReview()
                }
            }

            RegistrationEffect.NavigateToPreviousStep -> {
                if (pagerState.currentPage > 0) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
            }

            RegistrationEffect.OpenProfilePhotoPicker -> {
                photoPicker.launch("image/*")
            }

            RegistrationEffect.OpenNIDPicker -> {
                nidPicker.launch("*/*")
            }

            RegistrationEffect.OpenCertificatePicker -> {
                certificatePicker.launch("*/*")
            }

            RegistrationEffect.OpenLicensePicker -> {
                licensePicker.launch("*/*")
            }

            RegistrationEffect.OpenGenderSelection -> {
                showGenderSheet = true
            }

            RegistrationEffect.ShowCalendar -> {
                showDatePicker = true
            }

            is RegistrationEffect.ShowMessage -> {
                scope.launch {
                    val message = context.resources.getIdentifier(
                        it.message,
                        "string",
                        context.packageName
                    ).let { id ->
                        if (id != 0) context.getString(id) else it.message
                    }
                    snackbarHostState.showSnack(message, it.type)
                }
            }
        }
    }

    RegistrationScreenContent(
        modifier = modifier,
        state = state,
        onIntent = registrationViewmodel::onIntent,
        pagerState = pagerState,
        snackbarHostState = snackbarHostState
    )
}

@Composable
private fun RegistrationScreenContent(
    state: RegistrationUiState,
    onIntent: (RegistrationIntent) -> Unit,
    pagerState: PagerState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CareNestTopBar(
                title = stringResource(ProfileR.string.registration_title),
                leading = TopBarLeading.Back {
                    onIntent(RegistrationIntent.OnBackClicked)
                },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        snackbarHost = {
            Box(modifier = Modifier.fillMaxSize()) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(AlignmentUI.TopCenter)
                        .padding(top = Theme.spacing.space36)
                )
            }
        },
        bottomBar = {
            NavigationActions(
                currentPage = pagerState.currentPage,
                isNextDisabled = when (pagerState.currentPage) {
                    2 -> state.servicesUiState.selectedServices.isEmpty()
                    3 -> !state.applicationReviewUiState.isCertified
                    else -> false
                },
                onBackClick = {
                    if (pagerState.currentPage > 0) {
                        onIntent(RegistrationIntent.OnBackClicked)
                    }
                },
                onNextClick = {
                    if (pagerState.currentPage == 3) {
                        onIntent(RegistrationIntent.OnSubmitApplication)
                    } else {
                        onIntent(RegistrationIntent.OnContinueClicked(pagerState.currentPage))
                    }
                }
            )
        },
        containerColor = Theme.colors.backGround
    ) { innerPadding ->
        RegistrationContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = state,
            onIntent = onIntent,
            pagerState = pagerState
        )
    }
}

@Composable
fun RegistrationContent(
    modifier: Modifier = Modifier,
    state: RegistrationUiState,
    onIntent: (RegistrationIntent) -> Unit,
    pagerState: PagerState = rememberPagerState(pageCount = { 4 })
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier.background(Theme.colors.backGround),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalStepper(
            currentStep = pagerState.currentPage + 1,
            steps = listOf(
                stringResource(ProfileR.string.step_personal_info),
                stringResource(ProfileR.string.step_documents),
                stringResource(ProfileR.string.step_services),
                stringResource(ProfileR.string.step_review)
            ),
            modifier = Modifier.padding(Theme.spacing.medium),
            showStepLabel = false
        )

        Box(modifier = Modifier.weight(1f)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> PersonalInfoComponent(
                        state = state.personalInfoState,
                        onFirstNameChanged = { onIntent(RegistrationIntent.OnFirstNameChanged(it)) },
                        onLastNameChanged = { onIntent(RegistrationIntent.OnLastNameChanged(it)) },
                        onDateOfBirthChanged = { onIntent(RegistrationIntent.OnDateOfBirthChanged(it)) },
                        onDateOfBirthClick = { onIntent(RegistrationIntent.OnDateOfBirthClick) },
                        onNationalIdChanged = { onIntent(RegistrationIntent.OnNationalIdChanged(it)) },
                        onGenderClick = { onIntent(RegistrationIntent.OnGenderClick) },
                        onProfilePhotoClick = {
                            onIntent(RegistrationIntent.OnProfilePhotoClick)
                        }
                    )

                    1 -> VerificationDocumentsComponent(
                        state = state.verificationDocumentsUiState,
                        onNationalIdClick = { onIntent(RegistrationIntent.OnNationalIdClick) },
                        onNursingLicenseClick = { onIntent(RegistrationIntent.OnNursingLicenseClick) },
                        onProfessionalCertificateClick = { onIntent(RegistrationIntent.OnProfessionalCertificateClick) },
                        onRemoveNationalId = { onIntent(RegistrationIntent.OnRemoveNationalId) },
                        onRemoveNursingLicense = { onIntent(RegistrationIntent.OnRemoveNursingLicense) },
                        onRemoveProfessionalCertificate = { onIntent(RegistrationIntent.OnRemoveProfessionalCertificate) },
                        onYearsOfExpChanged = { onIntent(RegistrationIntent.OnYearsOfExpChanged(it)) },
                        onPrimarySpecialityChanged = {
                            onIntent(
                                RegistrationIntent.OnPrimarySpecialityChanged(
                                    it
                                )
                            )
                        }
                    )

                    2 -> ServicesSelectionComponent(
                        state = state.servicesUiState,
                        onServiceToggle = { onIntent(RegistrationIntent.OnServiceToggle(it)) }
                    )

                    3 -> ApplicationReviewComponent(
                        state = state,
                        onEditPersonalInfo = { scope.launch { pagerState.animateScrollToPage(0) } },
                        onEditProfessionalInfo = { scope.launch { pagerState.animateScrollToPage(1) } },
                        onEditServices = { scope.launch { pagerState.animateScrollToPage(2) } },
                        onEditDocuments = { scope.launch { pagerState.animateScrollToPage(1) } },
                        onCertificationToggle = {
                            onIntent(
                                RegistrationIntent.OnCertificationToggle(
                                    it
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationActions(
    currentPage: Int,
    isNextDisabled: Boolean,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val isLastPage = currentPage == 3
    val isFirstPage = currentPage == 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(Theme.spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            if (!isFirstPage) {
                SecondaryButton(
                    caption = stringResource(DesignR.string.back),
                    onClick = onBackClick,
                    modifier = Modifier.weight(.5f)
                )
            }

            PrimaryButton(
                caption = if (isLastPage) {
                    stringResource(ProfileR.string.submit)
                } else {
                    stringResource(ProfileR.string.next)
                },
                onClick = onNextClick,
                modifier = Modifier.weight(if (isFirstPage) 2f else 1f),
                isDisabled = isNextDisabled,
                iconPainter = if (isLastPage) null else painterResource(id = DesignR.drawable.ic_chevron_right),
                iconPosition = ButtonIconPosition.End
            )
        }
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor.use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result ?: "unknown"
}

@Preview(showBackground = true, name = "Registration Light")
@Composable
private fun RegistrationScreenLightPreview() {
    SpTheme(isDarkTheme = false) {
        RegistrationScreenPreviewContent()
    }
}

@Preview(showBackground = true, name = "Registration Dark")
@Composable
private fun RegistrationScreenDarkPreview() {
    SpTheme(isDarkTheme = true) {
        RegistrationScreenPreviewContent()
    }
}

@Composable
private fun RegistrationScreenPreviewContent() {
    RegistrationScreenContent(
        state = RegistrationUiState().copy(
            stepperState = StepperState(remainingSteps = 1)
        ),
        onIntent = {},
        pagerState = rememberPagerState(pageCount = { 4 }),
        snackbarHostState = remember { SnackbarHostState() }
    )
}
