package com.carenest.provider.designsystem.components.topbar


import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carenest.provider.designsystem.theme.SpTheme


@Composable
fun TrovesTopBar(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onAiClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(com.carenest.provider.designsystem.R.string.app_name),
    cartBadgeCount: Int = 0,
    border: BorderStroke? = null,
) {
    BaseTopAppBar(
        modifier = modifier,
        title = title,
        border = border,
        actions = listOf(
            TopBarAction(
                icon = painterResource(com.carenest.provider.designsystem.R.drawable.ic_ai_sparkles),
                contentDescription = stringResource(com.carenest.provider.designsystem.R.string.ai_chat_content_description),
                onClick = onAiClick,
            ),
            TopBarAction(
                icon = painterResource(com.carenest.provider.designsystem.R.drawable.ic_search),
                contentDescription = stringResource(com.carenest.provider.designsystem.R.string.search_content_description),
                onClick = onSearchClick,
            ),
            TopBarAction(
                icon = painterResource(com.carenest.provider.designsystem.R.drawable.ic_cart),
                contentDescription = stringResource(com.carenest.provider.designsystem.R.string.cart_content_description),
                badgeCount = cartBadgeCount,
                onClick = onCartClick,
            ),
        ),
    )
}

@Preview
@Composable
private fun TrovesTopBarPreview() {
    SpTheme {
        TrovesTopBar(
            onSearchClick = {},
            onCartClick = {},
            border = BorderStroke(1.dp, Color.LightGray),
            onAiClick = {}
        )
    }
}
