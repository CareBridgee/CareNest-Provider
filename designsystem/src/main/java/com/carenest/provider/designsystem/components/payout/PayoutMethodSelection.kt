package com.carenest.provider.designsystem.components.payout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.components.button.PrimaryButton
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme


enum class PaymentMethods(
    val value: String
){
    INSTAPAY("INSTAPAY"),
    VODAFONE("VODAFONE"),
    BANk("BANk"),
}


@Composable
fun PayoutMethodCard(
    title: String,
    subtitle: String,
    icon: Painter,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    iconContainerColor: Color = Theme.colors.surfaceVariant,
    iconContentColor: Color = Theme.colors.tint
) {
    val cardShape = Theme.shapes.large
    val borderColor = if (selected) Theme.colors.onPrimaryContainer else Theme.colors.divider
    val backgroundColor = if (selected) Theme.colors.primaryContainer else Theme.colors.surface

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, cardShape)
            .clickable(onClick = onClick)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(Theme.shapes.medium)
                .background(iconContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = iconContentColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(Theme.spacing.medium))

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = title,
                    style = Theme.typography.body.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = Theme.colors.primaryFont
                    )
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(Theme.spacing.small))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Theme.colors.onPrimaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        BasicText(
                            text = badge,
                            style = Theme.typography.body.small.copy(
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
            BasicText(
                text = subtitle,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 12.sp
                )
            )
        }

        // Radio Button (Custom)
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, if (selected) Theme.colors.onPrimaryContainer else Theme.colors.hint, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Theme.colors.onPrimaryContainer)
                )
            }
        }
    }
}

@Composable
fun PayoutInfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(Theme.shapes.medium)
            .background(Theme.colors.infoContainer)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = null,
            tint = Theme.colors.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
        BasicText(
            text = text,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.onInfoContainer,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
fun PayoutMethodSelection(
    modifier: Modifier = Modifier,
    onWithdraw:()-> Unit,
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethods.VODAFONE) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Theme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
    ) {
        BasicText(
            text = stringResource(id = R.string.payout_title),
            style = Theme.typography.title.copy(
                fontWeight = FontWeight.Bold,
                color = Theme.colors.primaryFont,
                fontSize = 20.sp
            )
        )

        PayoutMethodCard(
            title = stringResource(id = R.string.payout_bank_transfer),
            subtitle = stringResource(id = R.string.payout_arrival_bank),
            icon = painterResource(id = R.drawable.ic_bank),
            selected = selectedMethod == PaymentMethods.BANk,
            onClick = { selectedMethod = PaymentMethods.BANk },
            iconContainerColor = Theme.colors.bankContainer,
            iconContentColor = Theme.colors.onBankContainer
        )

        PayoutMethodCard(
            title = stringResource(id = R.string.payout_vodafone_cash),
            subtitle = stringResource(id = R.string.payout_arrival_vodafone),
            icon = painterResource(id = R.drawable.ic_wallet),
            selected = selectedMethod == PaymentMethods.VODAFONE,
            onClick = { selectedMethod = PaymentMethods.VODAFONE },
            iconContainerColor = Theme.colors.vodafoneContainer,
            iconContentColor = Theme.colors.onVodafoneContainer
        )

        PayoutMethodCard(
            title = stringResource(id = R.string.payout_instapay),
            subtitle = stringResource(id = R.string.payout_arrival_instant),
            icon = painterResource(id = R.drawable.ic_flash),
            selected = selectedMethod == PaymentMethods.INSTAPAY,
            onClick = { selectedMethod = PaymentMethods.INSTAPAY },
            badge = stringResource(id = R.string.payout_fastest),
            iconContainerColor = Theme.colors.onPrimaryContainer,
            iconContentColor = Color.White
        )

        PayoutInfoBanner(
            text = stringResource(id = R.string.payout_info)
        )

        Spacer(modifier = Modifier.height(Theme.spacing.large))

        PrimaryButton(
            caption = stringResource(id = R.string.action_withdraw),
            onClick = onWithdraw,
            modifier = Modifier.fillMaxWidth(),
            iconPainter = painterResource(id = R.drawable.ic_chevron_double_right),
            iconPosition = com.carenest.provider.designsystem.components.button.ButtonIconPosition.End,
            containerColor = Theme.colors.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PayoutMethodSelectionPreview() {
    SpTheme {
        Surface(color = Theme.colors.backGround) {
            PayoutMethodSelection(){}
        }
    }
}
