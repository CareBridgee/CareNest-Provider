package com.carenest.provider.designsystem.components.cards


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.carenest.provider.designsystem.theme.Theme

@Composable
fun OrderSummaryItemCard(
    imageUrl: String? = null,
    imagePainter: Painter? = null,
    name: String,
    specs: String,
    quantity: Int,
    priceFormatted: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(128.dp)
           .shadow(elevation =0.4.dp, shape = Theme.shapes.medium, clip = false)
            .clip(Theme.shapes.medium)
            .background(Theme.colors.surface)
            .border(1.dp, Theme.colors.onPrimary, Theme.shapes.medium)
            .padding(Theme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .clip(Theme.shapes.medium)
                    .background(Theme.colors.surfaceVariant),
            )
        } else if (imagePainter != null) {
            Image(
                painter = imagePainter,
                contentDescription = name,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .width(110.dp)
                    .fillMaxHeight()
                    .clip(Theme.shapes.medium),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            BasicText(
                text = name,
                style = Theme.typography.body.large.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            BasicText(
                text = specs,
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
            BasicText(
                text = stringResource(com.carenest.provider.designsystem.R.string.quantity_label, quantity),
                style = Theme.typography.body.medium.copy(color = Theme.colors.secondaryFont),
            )
        }

        BasicText(
            text = priceFormatted,
            style = Theme.typography.body.large.copy(
                color = Theme.colors.primaryFont,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .align(Alignment.Bottom)
                .padding(bottom = Theme.spacing.extraSmall),
        )
    }
}

@Preview
@Composable
private fun OrderSummaryItemCardPreview() {
    com.carenest.provider.designsystem.theme.SpTheme(
        isDarkTheme = false,
        languageCode = "en"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Theme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Theme.spacing.medium),
        ) {
            OrderSummaryItemCard(
                imagePainter = painterResource(com.carenest.provider.designsystem.R.drawable.img_placeholder),
                name = "Soft Knit Sweater",
                specs = "Cream / M",
                quantity = 2,
                priceFormatted = "\$98.00",
            )
        }
    }
}
