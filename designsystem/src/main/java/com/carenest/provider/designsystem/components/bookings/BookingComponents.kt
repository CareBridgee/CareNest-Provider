package com.carenest.provider.designsystem.components.bookings

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import kotlin.collections.get

// ---------------------------------------------------------------------------
// Booking Status Badge
// ---------------------------------------------------------------------------

/**
 * A small pill-shaped badge displaying the booking status with appropriate
 * colours following the app's design-system colour tokens.
 */
@Composable
fun BookingStatusBadge(
    status: BookingStatus,
    modifier: Modifier = Modifier,
) {
    val (label, containerColor, contentColor) = when (status) {
        BookingStatus.Upcoming -> Triple(
            stringResource(R.string.bookings_status_upcoming),
            Theme.colors.primaryContainer,
            Theme.colors.onPrimaryContainer,
        )
        BookingStatus.Ongoing -> Triple(
            stringResource(R.string.bookings_status_ongoing),
            Theme.colors.warningContainer,
            Theme.colors.onWarningContainer,
        )
        BookingStatus.Completed -> Triple(
            stringResource(R.string.bookings_status_completed),
            Theme.colors.successContainer,
            Theme.colors.onSuccessContainer,
        )
        BookingStatus.Cancelled -> Triple(
            stringResource(R.string.bookings_status_cancelled),
            Theme.colors.errorContainer,
            Theme.colors.onErrorContainer,
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Booking Service Icon Box
// ---------------------------------------------------------------------------

/**
 * Rounded square icon container shown on the left of each booking card.
 * The icon and tint colour are derived from the booking status.
 */
@Composable
fun BookingServiceIconBox(
    status: BookingStatus,
    modifier: Modifier = Modifier,
) {
    val (iconRes, iconTint, bgColor) = when (status) {
        BookingStatus.Upcoming -> Triple(
            R.drawable.ic_syringe,
            Theme.colors.onPrimaryContainer,
            Theme.colors.primaryContainer,
        )
        BookingStatus.Ongoing -> Triple(
            R.drawable.ic_heart_beat,
            Theme.colors.onWarningContainer,
            Theme.colors.warningContainer,
        )
        BookingStatus.Completed -> Triple(
            R.drawable.ic_physical_therapy,
            Theme.colors.onSuccessContainer,
            Theme.colors.successContainer,
        )
        BookingStatus.Cancelled -> Triple(
            R.drawable.ic_elderly,
            Theme.colors.onErrorContainer,
            Theme.colors.errorContainer,
        )
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp),
        )
    }
}

// ---------------------------------------------------------------------------
// Booking Card
// ---------------------------------------------------------------------------

/**
 * A card component representing a single booking entry.
 *
 * The card automatically adapts its footer content based on [BookingItem.status]:
 * - **Upcoming**: shows date + time, and a full-width "View Details" primary button.
 * - **Ongoing**: shows "Today" + "Active Now" label.
 * - **Completed**: shows date + a "Download Report" text action.
 * - **Cancelled**: shows a "Cancelled by patient" error label.
 *
 * State is **hoisted** — the caller decides what happens on each action via lambdas.
 *
 * @param booking        The booking data to display.
 * @param onViewDetails  Called when the "View Details" button is tapped (Upcoming).
 * @param onDownload     Called when "Download Report" is tapped (Completed).
 * @param modifier       Optional modifier for the outer card.
 */
@Composable
fun BookingCard(
    booking: BookingItem,
    onViewDetails: (BookingItem) -> Unit,
    onDownload: (BookingItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ---- Header row: icon + title/patient + status badge ----
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BookingServiceIconBox(status = booking.status)

                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        text = booking.title,
                        style = Theme.typography.body.large.copy(
                            fontWeight = FontWeight.Bold,
                            color = Theme.colors.primaryFont,
                        ),
                    )
                    Spacer(Modifier.height(2.dp))
                    BasicText(
                        text = stringResource(R.string.patient_label, booking.patientName),
                        style = Theme.typography.body.small.copy(
                            color = Theme.colors.secondaryFont,
                            fontSize = 13.sp,
                        ),
                    )
                }

                BookingStatusBadge(status = booking.status)
            }

            Spacer(Modifier.height(14.dp))

            // ---- Divider ----
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Theme.colors.divider),
            )

            Spacer(Modifier.height(12.dp))

            // ---- Footer: content varies by status ----
            when (booking.status) {
                BookingStatus.Upcoming -> UpcomingBookingFooter(
                    booking = booking,
                    onViewDetails = onViewDetails,
                )
                BookingStatus.Ongoing -> OngoingBookingFooter(booking = booking)
                BookingStatus.Completed -> CompletedBookingFooter(
                    booking = booking,
                    onDownload = onDownload,
                )
                BookingStatus.Cancelled -> CancelledBookingFooter(booking = booking)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Status-specific footer composables (private)
// ---------------------------------------------------------------------------

@Composable
private fun UpcomingBookingFooter(
    booking: BookingItem,
    onViewDetails: (BookingItem) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_order_history),
                    contentDescription = null,
                    tint = Theme.colors.secondaryFont,
                    modifier = Modifier.size(16.dp),
                )
                BasicText(
                    text = booking.dateLabel,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 13.sp,
                    ),
                )
            }
            // Time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_time),
                    contentDescription = null,
                    tint = Theme.colors.secondaryFont,
                    modifier = Modifier.size(16.dp),
                )
                BasicText(
                    text = booking.timeLabel,
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 13.sp,
                    ),
                )
            }
        }

        PrimaryButton(
            caption = stringResource(R.string.bookings_action_view_details),
            onClick = { onViewDetails(booking) },
            modifier = Modifier.fillMaxWidth(),
            containerColor = Theme.colors.primary,
        )
    }
}

@Composable
private fun OngoingBookingFooter(booking: BookingItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // "Today" with calendar icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_order_history),
                contentDescription = null,
                tint = Theme.colors.secondaryFont,
                modifier = Modifier.size(16.dp),
            )
            BasicText(
                text = booking.dateLabel,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 13.sp,
                ),
            )
        }

        // "Active Now" with clock icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_time),
                contentDescription = null,
                tint = Theme.colors.onPrimaryContainer,
                modifier = Modifier.size(16.dp),
            )
            BasicText(
                text = stringResource(R.string.bookings_label_active_now),
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                ),
            )
        }
    }
}

@Composable
private fun CompletedBookingFooter(
    booking: BookingItem,
    onDownload: (BookingItem) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Date
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_order_history),
                contentDescription = null,
                tint = Theme.colors.secondaryFont,
                modifier = Modifier.size(16.dp),
            )
            BasicText(
                text = booking.dateLabel,
                style = Theme.typography.body.small.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 13.sp,
                ),
            )
        }

        // Download Report action
        BasicText(
            text = stringResource(R.string.bookings_action_download_report),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            ),
            modifier = Modifier.clickable { onDownload(booking) },
        )
    }
}

@Composable
private fun CancelledBookingFooter(booking: BookingItem) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null,
            tint = Theme.colors.error,
            modifier = Modifier.size(16.dp),
        )
        BasicText(
            text = booking.cancelReason
                ?: stringResource(R.string.bookings_label_cancelled_by),
            style = Theme.typography.body.small.copy(
                color = Theme.colors.error,
                fontSize = 13.sp,
            ),
        )
    }
}

// ---------------------------------------------------------------------------
// Booking Tab Row
// ---------------------------------------------------------------------------

/**
 * A scrollable tab row for filtering bookings by status.
 *
 * State is **hoisted** via [selectedIndex] and [onTabSelected] so the parent
 * screen drives which tab is shown.
 *
 * @param selectedIndex The index of the currently selected tab (0–3).
 * @param onTabSelected Called with the new tab index when a tab is clicked.
 */
@Composable
fun BookingTabRow(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tabs = listOf(
        stringResource(R.string.bookings_tab_upcoming),
        stringResource(R.string.bookings_tab_ongoing),
        stringResource(R.string.bookings_tab_completed),
        stringResource(R.string.bookings_tab_cancelled),
    )

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Theme.colors.backGround,
        contentColor = Theme.colors.primaryFont,
        edgePadding = 16.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                height = 2.dp,
                color = Theme.colors.tint,
            )
        },
        divider = {},
        modifier = modifier,
    ) {
        tabs.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Theme.colors.tint else Theme.colors.secondaryFont,
                animationSpec = tween(durationMillis = 200),
                label = "tab_color_$index",
            )

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                selectedContentColor = Theme.colors.tint,
                unselectedContentColor = Theme.colors.secondaryFont,
            ) {
                BasicText(
                    text = label,
                    style = Theme.typography.body.medium.copy(
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                )
            }
        }
    }
}

/**
 *
 * @param bookings            Full list of all bookings (all statuses).
 * @param onViewDetails       Called when the user taps "View Details" on an Upcoming booking.
 * @param onDownloadReport    Called when the user taps "Download Report" on a Completed booking.
 * @param onNotificationClick Called when the notification bell icon is tapped.
 * @param modifier            Optional outer [Modifier].
 */
@Composable
fun MyBookingsScreen(
    bookings: List<BookingItem>,
    onViewDetails: (BookingItem) -> Unit,
    onDownloadReport: (BookingItem) -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ---- State hoisted at screen level ----
    val tabs = BookingStatus.entries.toList()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val filteredBookings = remember(bookings, selectedTabIndex) {
        bookings.filter { it.status == tabs[selectedTabIndex] }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        // Top app bar
        MyBookingsTopBar(onNotificationClick = onNotificationClick)

        // Screen title & subtitle
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 4.dp),
        ) {
            BasicText(
                text = stringResource(R.string.my_bookings_title),
                style = Theme.typography.displayMedium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                ),
            )
            Spacer(Modifier.height(2.dp))
            BasicText(
                text = stringResource(R.string.my_bookings_subtitle),
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 14.sp,
                ),
            )
        }

        Spacer(Modifier.height(8.dp))

        // Scrollable status tab row — state hoisted here
        BookingTabRow(
            selectedIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
        )

        Spacer(Modifier.height(8.dp))

        // Booking cards list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(
                items = filteredBookings,
                key = { it.id },
            ) { booking ->
                BookingCard(
                    booking = booking,
                    onViewDetails = onViewDetails,
                    onDownload = onDownloadReport,
                )
            }

            // Bottom spacing inside list
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun MyBookingsScreenPreview() {
    val sampleBookings = listOf(
        BookingItem(
            id = "1",
            title = "Vaccination Drive",
            patientName = "Arthur Morgan",
            status = BookingStatus.Upcoming,
            dateLabel = "Oct 24, 2023",
            timeLabel = "09:00 AM",
        ),
        BookingItem(
            id = "2",
            title = "Vitals Check",
            patientName = "Elizabeth Swann",
            status = BookingStatus.Ongoing,
            dateLabel = "Today",
            timeLabel = "Active Now",
        ),
        BookingItem(
            id = "3",
            title = "Wound Dressing",
            patientName = "James Norrington",
            status = BookingStatus.Completed,
            dateLabel = "Oct 20, 2023",
            timeLabel = "",
        ),
        BookingItem(
            id = "4",
            title = "Urgent Consultation",
            patientName = "Jack Sparrow",
            status = BookingStatus.Cancelled,
            dateLabel = "",
            timeLabel = "",
            cancelReason = "Cancelled by patient",
        ),
    )

    SpTheme(isDarkTheme = false) {
        MyBookingsScreen(
            bookings = sampleBookings,
            onViewDetails = {},
            onDownloadReport = {},
            onNotificationClick = {},
        )
    }
}
