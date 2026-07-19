package com.carenest.presentation.ui.auth.login.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.designsystem.theme.Theme
import com.carenest.presentation.R
import com.carenest.designsystem.R as DR

@Composable
fun SecureCareInfoCard() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Theme.colors.surface,
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(DR.drawable.secure_care),
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.phone_header_brand),
                    style = Theme.typography.body.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = Theme.colors.primaryFont
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.phone_input_secure_care_desc),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}
