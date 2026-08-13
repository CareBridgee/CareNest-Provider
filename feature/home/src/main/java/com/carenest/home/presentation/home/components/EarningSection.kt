package com.carenest.home.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.home.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun EarningsSection(
    earnings: String, changePercent: String, jobsToday: Int, rating: String
) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Theme.colors.primary.copy(alpha = 0.85f))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.today_earning),
                    color = Theme.colors.disable.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = earnings,
                    color = Theme.colors.disable.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Theme.colors.onPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = Theme.spacing.medium, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Theme.colors.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$changePercent " + stringResource(R.string.from_yesterday),
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(90.dp)
                    .offset(x = 20.dp, y = 20.dp)
                    .clip(RoundedCornerShape(topStart = 40.dp))
                    .background(Theme.colors.onPrimary.copy(alpha = 0.08f))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f), label = stringResource(R.string.today_job)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$jobsToday",
                        color = Theme.colors.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.total),
                        color = Theme.colors.onWarning,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }
            StatCard(
                modifier = Modifier.weight(1f), label = stringResource(R.string.rating)
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = rating,
                        color = Theme.colors.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = "★",
                        color = Theme.colors.primary,
                        fontSize = 20.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier, label: String, value: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Theme.colors.onPrimary)
            .padding(16.dp)
    ) {
        Text(text = label, color = Theme.colors.secondary, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        value()
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    SpTheme {
        EarningsSection(
            earnings = "$240.00", changePercent = "12%", jobsToday = 3, rating = "4.9"
        )
    }
}