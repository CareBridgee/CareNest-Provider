package com.carenest.provider.designsystem.components.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun UploadCard(
    title: String,
    description: String,
    iconPainter: Painter,
    isUploaded: Boolean,
    uploadContent: @Composable () -> Unit,
    uploadedContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = Theme.shapes.large

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, cardShape)
            .padding(Theme.spacing.medium),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(Theme.size.medium)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.disable),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(Theme.size.iconMedium)
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BasicText(
                text = title,
                style = Theme.typography.title.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Theme.colors.primaryFont,
                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(Theme.spacing.extraSmall))
            BasicText(
                text = description,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            )
            Spacer(modifier = Modifier.height(Theme.spacing.medium))
            if (isUploaded) {
                uploadedContent()
            } else {
                uploadContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UploadCardPreview() {
    SpTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround)
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium)
        ) {
            UploadCard(
                title = "National ID",
                description = "Upload a clear scan of your Government issued Identity Card or Passport.",
                iconPainter = painterResource(id = R.drawable.ic_id_card),
                isUploaded = true,
                uploadContent = { /* Empty for preview */ },
                uploadedContent = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Theme.shapes.medium)
                            .background(Theme.colors.disable)
                            .padding(Theme.spacing.small),
                        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_document_text),
                            contentDescription = null,
                            tint = Theme.colors.tint,
                            modifier = Modifier.size(Theme.size.iconMedium)
                        )
                        BasicText(
                            text = "national_id_front.pdf",
                            style = Theme.typography.body.medium.copy(
                                color = Theme.colors.primaryFont,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(Theme.size.iconMedium)
                                .clip(CircleShape)
                                .background(Theme.colors.success, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_check),
                                contentDescription = null,
                                tint = Theme.colors.surface, // Use surface (white) for the check icon inside the green circle
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            )

            UploadCard(
                title = "Professional Certificate",
                description = "Degree or specialization certificates (e.g., ICU, Pediatric Care).",
                iconPainter = painterResource(id = R.drawable.ic_badge),
                isUploaded = false,
                uploadContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                        // Ensure button shape and border are visible against white background
                            .clip(Theme.shapes.medium)
                            .border(1.dp, Theme.colors.tint, Theme.shapes.medium)
                            .padding(vertical = Theme.spacing.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Theme.spacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicText(
                                text = "+ Upload Certificate",
                                style = Theme.typography.body.medium.copy(
                                    color = Theme.colors.tint,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                },
                uploadedContent = { /* Empty for preview */ }
            )
        }
    }
}
