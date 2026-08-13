package com.carenest.provider.designsystem.components.availability

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.carenest.provider.designsystem.R
import com.carenest.provider.designsystem.theme.SpTheme
import com.carenest.provider.designsystem.theme.Theme
import com.carenest.provider.designsystem.util.bounceClick

enum class WorkDay(val labelRes: Int) {
    Monday(R.string.availability_day_mon),
    Tuesday(R.string.availability_day_tue),
    Wednesday(R.string.availability_day_wed),
    Thursday(R.string.availability_day_thu),
    Friday(R.string.availability_day_fri),
    Saturday(R.string.availability_day_sat),
    Sunday(R.string.availability_day_sun),
}

// ---------------------------------------------------------------------------
// Section header helper
// ---------------------------------------------------------------------------

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    BasicText(
        text = title,
        style = Theme.typography.body.large.copy(
            color = Theme.colors.primaryFont,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        ),
        modifier = modifier,
    )
}

// ---------------------------------------------------------------------------
// 1. Accepting Bookings Card
// ---------------------------------------------------------------------------

/**
 * A card with a [Switch] allowing the caregiver to toggle their online status.
 *
 * State is **hoisted**: [isAccepting] and [onToggle] are owned by the caller.
 *
 * @param isAccepting Current toggle state.
 * @param onToggle    Called with the new value when the switch is flipped.
 */
@Composable
fun AcceptingBookingsCard(
    isAccepting: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_work),
                    contentDescription = null,
                    tint = Theme.colors.onPrimaryContainer,
                    modifier = Modifier.size(24.dp),
                )
            }

            // Title & subtitle
            Column(modifier = Modifier.weight(1f)) {
                BasicText(
                    text = stringResource(R.string.availability_accepting_bookings_title),
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.primaryFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                )
                Spacer(Modifier.height(2.dp))
                BasicText(
                    text = stringResource(R.string.availability_accepting_bookings_subtitle),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 12.sp,
                    ),
                )
            }

            // Toggle switch
            Switch(
                checked = isAccepting,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Theme.colors.tint,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Theme.colors.track,
                    uncheckedBorderColor = Color.Transparent,
                ),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 2. Working Days Selector
// ---------------------------------------------------------------------------

/**
 * An animated row of pill chips — one per day — that the user can toggle.
 *
 * State is **hoisted**: [selectedDays] and [onDayToggled] are owned by the caller.
 *
 * @param selectedDays Current set of selected [WorkDay]s.
 * @param onDayToggled Called with the toggled [WorkDay].
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkingDaysSelector(
    selectedDays: Set<WorkDay>,
    onDayToggled: (WorkDay) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            WorkDay.entries.forEach { day ->
                val isSelected = day in selectedDays

                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) Theme.colors.tint else Theme.colors.track,
                    animationSpec = tween(200),
                    label = "day_bg_${day.name}",
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Theme.colors.secondaryFont,
                    animationSpec = tween(200),
                    label = "day_fg_${day.name}",
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .background(containerColor)
                        .bounceClick(
                            shape = RoundedCornerShape(percent = 50),
                            onClick = { onDayToggled(day) },
                        )
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = stringResource(day.labelRes),
                        style = Theme.typography.body.medium.copy(
                            color = contentColor,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp,
                        ),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. Working Hours Card
// ---------------------------------------------------------------------------

/**
 * Start-time / End-time fields with a divider dash and an info note below.
 *
 * State is **hoisted**: [startTime] / [endTime] are owned by the caller.
 * Tapping a field fires [onStartTimeClick] / [onEndTimeClick] so the caller
 * can open a [TimePickerDialog] and push the updated value back in.
 *
 * @param startTime        Formatted start time (e.g. "08:00 AM").
 * @param endTime          Formatted end time (e.g. "06:00 PM").
 * @param onStartTimeClick Called when the start-time field is tapped.
 * @param onEndTimeClick   Called when the end-time field is tapped.
 */
@Composable
fun WorkingHoursCard(
    startTime: String,
    endTime: String,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TimePickerField(
                    label = stringResource(R.string.availability_label_start_time),
                    value = startTime,
                    onClick = onStartTimeClick,
                    modifier = Modifier.weight(1f),
                )
                BasicText(
                    text = "\u2013",
                    style = Theme.typography.body.large.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
                TimePickerField(
                    label = stringResource(R.string.availability_label_end_time),
                    value = endTime,
                    onClick = onEndTimeClick,
                    modifier = Modifier.weight(1f),
                )
            }

            // Informational note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = Theme.colors.onPrimaryContainer,
                    modifier = Modifier.size(16.dp),
                )
                BasicText(
                    text = stringResource(R.string.availability_hours_applied_note),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.onPrimaryContainer,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
    }
}

/** Bordered time field shown inside [WorkingHoursCard]. */
@Composable
private fun TimePickerField(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .border(
                BorderStroke(1.dp, Theme.colors.track),
                shape = RoundedCornerShape(10.dp),
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.secondaryFont,
                fontSize = 11.sp,
            ),
        )
        Spacer(Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            BasicText(
                text = value,
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.primaryFont,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                ),
                modifier = Modifier.weight(1f),
            )
            Icon(
                painter = painterResource(R.drawable.ic_time),
                contentDescription = null,
                tint = Theme.colors.secondaryFont,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 4. Service Areas Card
// ---------------------------------------------------------------------------

/**
 * Displays dismissible area chips and an "Add New Area" dashed action button.
 *
 * State is **hoisted**: [areas] is owned by the caller; mutations happen via
 * [onRemoveArea] and [onAddAreaClick].
 *
 * @param areas          Current list of service area labels.
 * @param onRemoveArea   Called with the area name when its × is tapped.
 * @param onAddAreaClick Called when "Add New Area" is tapped.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceAreasCard(
    areas: List<String>,
    onRemoveArea: (String) -> Unit,
    onAddAreaClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (areas.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    areas.forEach { area ->
                        ServiceAreaChip(
                            label = area,
                            onRemove = { onRemoveArea(area) },
                        )
                    }
                }
            }

            // Dashed "Add New Area" button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        BorderStroke(1.5.dp, Theme.colors.track),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onAddAreaClick() }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_location),
                    contentDescription = null,
                    tint = Theme.colors.secondaryFont,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicText(
                    text = stringResource(R.string.availability_add_new_area),
                    style = Theme.typography.body.medium.copy(
                        color = Theme.colors.secondaryFont,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    ),
                )
            }
        }
    }
}

/** A single area chip with a remove × button. */
@Composable
private fun ServiceAreaChip(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(
                BorderStroke(1.dp, Theme.colors.track),
                shape = RoundedCornerShape(percent = 50),
            )
            .clip(RoundedCornerShape(percent = 50))
            .background(Theme.colors.surface)
            .padding(start = 14.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        BasicText(
            text = label,
            style = Theme.typography.body.small.copy(
                color = Theme.colors.primaryFont,
                fontSize = 13.sp,
            ),
        )
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = "\u00D7",
                style = Theme.typography.body.medium.copy(
                    color = Theme.colors.secondaryFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 5. Travel Distance Card
// ---------------------------------------------------------------------------

/**
 * A card with a [Slider] for selecting the maximum travel radius in miles.
 *
 * State is **hoisted**: [distanceMiles] and [onDistanceChange] are owned by
 * the caller.
 *
 * @param distanceMiles    Current value in miles (within [minMiles]..[maxMiles]).
 * @param onDistanceChange Called while the user drags the slider thumb.
 * @param minMiles         Minimum selectable value (default 1).
 * @param maxMiles         Maximum selectable value (default 50).
 */
@Composable
fun TravelDistanceCard(
    distanceMiles: Float,
    onDistanceChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    minMiles: Float = 1f,
    maxMiles: Float = 50f,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 1.dp, shape = Theme.shapes.large, clip = false),
        shape = Theme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Theme.colors.surface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            // Numeric value + "Maximum radius" label
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    BasicText(
                        text = distanceMiles.toInt().toString(),
                        style = Theme.typography.displayMedium.copy(
                            color = Theme.colors.tint,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                        ),
                    )
                    BasicText(
                        text = stringResource(R.string.availability_distance_unit_miles),
                        style = Theme.typography.body.medium.copy(
                            color = Theme.colors.tint,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                        ),
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
                BasicText(
                    text = stringResource(R.string.availability_distance_max_radius),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 12.sp,
                    ),
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }

            Spacer(Modifier.height(4.dp))

            // Slider
            Slider(
                value = distanceMiles,
                onValueChange = onDistanceChange,
                valueRange = minMiles..maxMiles,
                colors = SliderDefaults.colors(
                    thumbColor = Theme.colors.tint,
                    activeTrackColor = Theme.colors.tint,
                    inactiveTrackColor = Theme.colors.track,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            // Min / Max labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BasicText(
                    text = stringResource(R.string.availability_distance_min_label),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 12.sp,
                    ),
                )
                BasicText(
                    text = stringResource(R.string.availability_distance_max_label),
                    style = Theme.typography.body.small.copy(
                        color = Theme.colors.secondaryFont,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 6. Availability Top Bar
// ---------------------------------------------------------------------------

/**
 * Top app bar for the Availability screen — avatar, screen title, notification bell.
 *
 * @param onNotificationClick Called when the bell icon is tapped.
 */
@Composable
fun AvailabilityTopBar(
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.backGround)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Theme.colors.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = stringResource(R.string.profile_content_description),
                tint = Theme.colors.onPrimaryContainer,
                modifier = Modifier.size(22.dp),
            )
        }

        Spacer(Modifier.width(10.dp))

        BasicText(
            text = stringResource(R.string.availability_title),
            style = Theme.typography.title.copy(
                color = Theme.colors.tint,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
        )

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable { onNotificationClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_notification),
                contentDescription = stringResource(R.string.notifications_content_description),
                tint = Theme.colors.primaryFont,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

// ---------------------------------------------------------------------------
// 7. Availability Screen (Stateful — all section state hoisted here)
// ---------------------------------------------------------------------------

/**
 * Stateful **Availability** screen.
 *
 * All mutable state is owned inside this composable and hoisted down to the
 * individual section components as plain values + callback lambdas. Navigation
 * or dialog-opening events are propagated outward via the caller's lambdas.
 *
 * | State                | Type                  |
 * |----------------------|-----------------------|
 * | isAcceptingBookings  | Boolean               |
 * | selectedDays         | Set<WorkDay>          |
 * | startTime            | String                |
 * | endTime              | String                |
 * | serviceAreas         | SnapshotStateList     |
 * | travelDistance       | Float (miles)         |
 *
 * @param onNotificationClick  Called when the notification bell is tapped.
 * @param onStartTimeClick     Called when the start-time field is tapped (open dialog).
 * @param onEndTimeClick       Called when the end-time field is tapped (open dialog).
 * @param onAddAreaClick       Called when "Add New Area" is tapped.
 * @param initialIsAccepting   Seed value for the toggle (default true).
 * @param initialSelectedDays  Seed value for selected days.
 * @param initialStartTime     Seed start-time string.
 * @param initialEndTime       Seed end-time string.
 * @param initialServiceAreas  Seed list of service areas.
 * @param initialDistance      Seed slider value in miles.
 * @param modifier             Optional outer [Modifier].
 */
@Composable
fun AvailabilityScreen(
    onNotificationClick: () -> Unit,
    onStartTimeClick: () -> Unit,
    onEndTimeClick: () -> Unit,
    onAddAreaClick: () -> Unit,
    modifier: Modifier = Modifier,
    initialIsAccepting: Boolean = true,
    initialSelectedDays: Set<WorkDay> = setOf(
        WorkDay.Monday, WorkDay.Tuesday, WorkDay.Wednesday,
        WorkDay.Thursday, WorkDay.Friday,
    ),
    initialStartTime: String = "08:00 AM",
    initialEndTime: String = "06:00 PM",
    initialServiceAreas: List<String> = listOf("Downtown", "North Park", "Riverside"),
    initialDistance: Float = 15f,
) {
    // ---- All state hoisted at screen level ----
    var isAcceptingBookings by remember { mutableStateOf(initialIsAccepting) }
    var selectedDays by remember { mutableStateOf(initialSelectedDays) }
    @Suppress("UNUSED_VARIABLE")
    var startTime by remember { mutableStateOf(initialStartTime) }
    @Suppress("UNUSED_VARIABLE")
    var endTime by remember { mutableStateOf(initialEndTime) }
    val serviceAreas = remember { mutableStateListOf(*initialServiceAreas.toTypedArray()) }
    var travelDistance by remember { mutableFloatStateOf(initialDistance) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.colors.backGround),
    ) {
        AvailabilityTopBar(onNotificationClick = onNotificationClick)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // Accepting Bookings toggle
            item {
                Spacer(Modifier.height(8.dp))
                AcceptingBookingsCard(
                    isAccepting = isAcceptingBookings,
                    onToggle = { isAcceptingBookings = it },
                )
            }

            // Working Days
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(title = stringResource(R.string.availability_section_working_days))
                Spacer(Modifier.height(10.dp))
                WorkingDaysSelector(
                    selectedDays = selectedDays,
                    onDayToggled = { day ->
                        selectedDays = if (day in selectedDays) {
                            selectedDays - day
                        } else {
                            selectedDays + day
                        }
                    },
                )
            }

            // Working Hours
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(title = stringResource(R.string.availability_section_working_hours))
                Spacer(Modifier.height(10.dp))
                WorkingHoursCard(
                    startTime = initialStartTime,
                    endTime = initialEndTime,
                    onStartTimeClick = onStartTimeClick,
                    onEndTimeClick = onEndTimeClick,
                )
            }

            // Service Areas
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(title = stringResource(R.string.availability_section_service_areas))
                Spacer(Modifier.height(10.dp))
                ServiceAreasCard(
                    areas = serviceAreas.toList(),
                    onRemoveArea = { serviceAreas.remove(it) },
                    onAddAreaClick = onAddAreaClick,
                )
            }

            // Travel Distance
            item {
                Spacer(Modifier.height(20.dp))
                SectionHeader(title = stringResource(R.string.availability_section_travel_distance))
                Spacer(Modifier.height(10.dp))
                TravelDistanceCard(
                    distanceMiles = travelDistance,
                    onDistanceChange = { travelDistance = it },
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preview
// ---------------------------------------------------------------------------

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun AvailabilityScreenPreview() {
    SpTheme(isDarkTheme = false) {
        AvailabilityScreen(
            onNotificationClick = {},
            onStartTimeClick = {},
            onEndTimeClick = {},
            onAddAreaClick = {},
        )
    }
}
