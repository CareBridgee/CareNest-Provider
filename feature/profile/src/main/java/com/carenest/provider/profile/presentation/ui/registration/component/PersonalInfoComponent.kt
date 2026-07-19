package com.carenest.provider.profile.presentation.ui.registration.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.components.button.ButtonIconPosition
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.components.textfield.CustomTextField
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.noRippleClickable
import com.carenest.provider.profile.R
import com.carenest.provider.profile.presentation.ui.registration.Gender
import com.carenest.provider.profile.presentation.ui.registration.PersonalInfoState

@Composable
fun PersonalInfoComponent(
    state: PersonalInfoState,
    onFirstNameChanged: (String) -> Unit,
    onLastNameChanged: (String) -> Unit,
    onDateOfBirthChanged: (String) -> Unit,
    onNationalIdChanged: (String) -> Unit,
    onGenderClick: () -> Unit,
    onProfilePhotoClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState()
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .verticalScroll(scrollState)
            .padding(horizontal = Theme.spacing.medium, Theme.spacing.extraLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicText(
            text = stringResource(R.string.personal_info_title),
            style = Theme.typography.displayMedium.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.small))

        BasicText(
            text = stringResource(R.string.personal_info_subtitle),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.secondaryFont
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        ProfilePhotoPicker(
            profilePhotoUri = state.profilePhotoUri,
            onClick = onProfilePhotoClick
        )

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        CustomTextField(
            text = state.firstName,
            onTextChange = onFirstNameChanged,
            title = stringResource(R.string.first_name_label),
            hint = stringResource(R.string.first_name_hint),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        CustomTextField(
            text = state.lastName,
            onTextChange = onLastNameChanged,
            title = stringResource(R.string.last_name_label),
            hint = stringResource(R.string.last_name_hint),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        CustomTextField(
            text = state.dateOfBirth,
            onTextChange = onDateOfBirthChanged,
            title = stringResource(R.string.dob_label),
            hint = stringResource(R.string.dob_hint),
            leadingIcon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_calendar),
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            onClickLeadingIcon = { /* Should trigger calendar */ }
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        CustomTextField(
            text = state.nationalId,
            onTextChange = onNationalIdChanged,
            title = stringResource(R.string.national_id_label),
            hint = stringResource(R.string.national_id_hint),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Theme.spacing.medium))

        Box(modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { onGenderClick() }) {
            CustomTextField(
                text = if (state.gender == Gender.UNKNOWN) "" else state.gender.name,
                onTextChange = {},
                title = stringResource(R.string.gender_label),
                hint = stringResource(R.string.gender_hint),
                leadingIcon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_profile),
                trailingIcon = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_arrow_drop_down),
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(Theme.spacing.extraLarge))

        PrimaryButton(
            caption = stringResource(R.string.next),
            onClick = onContinueClick,
            modifier = Modifier.fillMaxWidth(),
            iconPainter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_chevron_right),
            iconPosition = ButtonIconPosition.End
        )

        Spacer(modifier = Modifier.height(Theme.spacing.large))

        BasicText(
            text = stringResource(
                R.string.step_indicator,
                1,
                3,
                stringResource(R.string.personal_info_title)
            ),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
fun ProfilePhotoPicker(
    profilePhotoUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.noRippleClickable { onClick() }
    ) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color.LightGray,
                            style = Stroke(
                                width = 4.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            )
                        )
                    }
                    .background(Theme.colors.disable, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profilePhotoUri == null) {
                    Image(
                        painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_camera),
                        contentDescription = null,
                        modifier = Modifier.size(Theme.spacing.extraLarge),
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                } else {
                    Image(
                        painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_camera),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(Theme.spacing.extraLarge)
                    .align(Alignment.BottomEnd)
                    .background(Theme.colors.primary, CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.carenest.provider.designsystem.R.drawable.ic_edit),
                    contentDescription = null,
                    modifier = Modifier.size(Theme.spacing.medium),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BasicText(
            text = stringResource(R.string.upload_profile_photo),
            style = Theme.typography.body.medium.copy(
                color = Theme.colors.primary,
                fontWeight = FontWeight.SemiBold
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PersonalInfoComponentPreview() {
    SpTheme {
        PersonalInfoComponent(
            state = PersonalInfoState(),
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onDateOfBirthChanged = {},
            onNationalIdChanged = {},
            onGenderClick = {},
            onProfilePhotoClick = {},
            onContinueClick = {}
        )
    }
}
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PersonalInfoComponentDarkPreview() {
    SpTheme {
        PersonalInfoComponent(
            state = PersonalInfoState(),
            onFirstNameChanged = {},
            onLastNameChanged = {},
            onDateOfBirthChanged = {},
            onNationalIdChanged = {},
            onGenderClick = {},
            onProfilePhotoClick = {},
            onContinueClick = {}
        )
    }
}
