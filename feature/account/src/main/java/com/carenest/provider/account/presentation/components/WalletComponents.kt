package com.carenest.provider.account.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.carenest.provider.account.presentation.wallet.AlternativePayoutAccent
import com.carenest.provider.account.presentation.wallet.AlternativePayoutMethodUiModel
import com.carenest.provider.account.presentation.wallet.PrimaryPayoutMethodUiModel
import com.carenest.provider.designsystem.R as DesignSystemR
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun WalletSecureBadge(
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(Theme.colors.primaryContainer)
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Lock,
            contentDescription = null,
            tint = Theme.colors.tint,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(6.dp))
        BasicText(
            text = label,
            style = Theme.typography.hint.large.copy(
                color = Theme.colors.secondaryFont,
                fontWeight = FontWeight.Medium,
            ),
        )
    }
}

@Composable
fun PrimaryPayoutCard(
    method: PrimaryPayoutMethodUiModel,
    title: String,
    subtitle: String,
    accountNumberLabel: String,
    maskedAccountNumber: String,
    statusLabel: String,
    verifiedLabel: String,
    manageLabel: String,
    addMethodLabel: String,
    onManageClick: () -> Unit,
    onAddMethodClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, Theme.shapes.extraLarge)
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(Theme.shapes.medium)
                    .background(Theme.colors.primaryVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(DesignSystemR.drawable.ic_bank),
                    contentDescription = null,
                    tint = Theme.colors.onPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.width(Theme.spacing.medium))
            Column(Modifier.weight(1f)) {
                BasicText(
                    text = title,
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                BasicText(
                    text = subtitle,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                BasicText(
                    text = accountNumberLabel,
                    style = Theme.typography.hint.large.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                BasicText(
                    text = maskedAccountNumber,
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                BasicText(
                    text = statusLabel,
                    style = Theme.typography.hint.large.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                if (method.isVerified) {
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Theme.colors.successContainer)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Theme.colors.onSuccessContainer,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(5.dp))
                        BasicText(
                            text = verifiedLabel,
                            style = Theme.typography.hint.large.copy(
                                color = Theme.colors.onSuccessContainer,
                                fontWeight = FontWeight.Medium,
                            ),
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Theme.colors.divider)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
        ) {
            Button(
                onClick = onManageClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = Theme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Theme.colors.primary,
                    contentColor = Theme.colors.onPrimary,
                ),
                contentPadding = PaddingValues(horizontal = Theme.spacing.small),
            ) {
                Icon(Icons.Rounded.Edit, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(6.dp))
                BasicText(
                    text = manageLabel,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.onPrimary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            OutlinedButton(
                onClick = onAddMethodClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = Theme.shapes.large,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Theme.colors.tint.copy(alpha = .18f),
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Theme.colors.tint,
                    containerColor = Theme.colors.primaryContainer,
                ),
                contentPadding = PaddingValues(horizontal = Theme.spacing.small),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                BasicText(
                    text = addMethodLabel,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.tint,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
        }
    }
}

@Composable
fun AlternativePayoutMethodCard(
    method: AlternativePayoutMethodUiModel,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconContainer = when (method.accent) {
        AlternativePayoutAccent.Vodafone -> Theme.colors.vodafoneContainer
        AlternativePayoutAccent.Neutral -> Theme.colors.primaryContainer
    }
    val iconTint = when (method.accent) {
        AlternativePayoutAccent.Vodafone -> Theme.colors.onVodafoneContainer
        AlternativePayoutAccent.Neutral -> Theme.colors.secondaryFont
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, Theme.shapes.extraLarge)
            .clip(Theme.shapes.extraLarge)
            .background(Theme.colors.surface)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(method.iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(Theme.spacing.medium))
        Column(Modifier.weight(1f)) {
            BasicText(
                text = title,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                ),
            )
            BasicText(
                text = subtitle,
                style = Theme.typography.hint.large.copy(
                    color = Theme.colors.secondaryFont,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = Theme.colors.secondaryFont,
            modifier = Modifier.size(22.dp),
        )
    }
}
