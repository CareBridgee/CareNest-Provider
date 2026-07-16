package com.carenest.provider.designsystem.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.carenest.provider.designsystem.components.chip.PendingStatusChip
import com.carenest.provider.designsystem.components.chip.VerifiedStatusChip
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.bounceClick

@Composable
fun DocumentCard(
    title: String,
    subtitle: String,
    iconPainter: Painter,
    status: @Composable () -> Unit,
    action: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val cardShape = Theme.shapes.large
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.divider, cardShape)
            .bounceClick(shape = cardShape, onClick = onClick)
            .padding(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(Theme.shapes.medium)
                .background(Theme.colors.secondary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = null,
                tint = Theme.colors.tint,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(Theme.spacing.medium))

        // Middle Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            BasicText(
                text = title,
                style = Theme.typography.title.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Theme.colors.primaryFont,
                    lineHeight = 22.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            BasicText(
                text = subtitle,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 13.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(Theme.spacing.small))

        // Right Content (Status and Action)
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            status()
            Spacer(modifier = Modifier.height(16.dp))
            action()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DocumentCardPreview() {
    SpTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.colors.backGround)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DocumentCard(
                title = "National ID",
                subtitle = "Uploaded on Oct 12, 2023",
                iconPainter = painterResource(id = R.drawable.ic_id_card),
                status = { VerifiedStatusChip() },
                action = {
                    BasicText(
                        text = stringResource(id = R.string.action_view),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.tint,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.bounceClick(shape = RoundedCornerShape(0.dp)) { }
                    )
                }
            )

            DocumentCard(
                title = "Nursing License",
                subtitle = "Uploaded on Oct 14, 2023",
                iconPainter = painterResource(id = R.drawable.ic_document_text),
                status = { PendingStatusChip() },
                action = {
                    BasicText(
                        text = stringResource(id = R.string.action_edit),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.tint,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.bounceClick(shape = RoundedCornerShape(0.dp)) { }
                    )
                }
            )

            DocumentCard(
                title = "ACLS Certification",
                subtitle = "Uploaded on Sep 28, 2023",
                iconPainter = painterResource(id = R.drawable.ic_badge),
                status = { VerifiedStatusChip() },
                action = {
                    BasicText(
                        text = stringResource(id = R.string.action_view),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.tint,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.bounceClick(shape = RoundedCornerShape(0.dp)) { }
                    )
                }
            )
        }
    }
}
