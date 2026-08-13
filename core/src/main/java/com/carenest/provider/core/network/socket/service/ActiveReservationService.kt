package com.carenest.provider.core.network.socket.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.carenest.provider.core.R
import com.carenest.provider.core.network.socket.client.NurseSocketClient
import com.carenest.provider.core.network.socket.model.ReservationEventType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class ActiveReservationService : Service() {

    @Inject
    lateinit var nurseSocketClient: NurseSocketClient

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var eventCollectorJob: Job? = null

    private var currentServiceRequestId: String? = null
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SERVICE) {
            stopForegroundService()
            return START_NOT_STICKY
        }

        val requestId = intent?.getStringExtra(EXTRA_SERVICE_REQUEST_ID)
        if (!requestId.isNullOrBlank()) {
            currentServiceRequestId = requestId
            setActiveReservationId(this, requestId)
            val notification = buildNotification(
                title = getString(R.string.active_reservation_title),
                content = getString(R.string.active_reservation_monitoring),
                requestId = requestId
            )
            startForeground(NOTIFICATION_ID, notification)
            startSocketMonitoring(requestId)
        } else if (currentServiceRequestId == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        return START_STICKY
    }

    private fun startSocketMonitoring(requestId: String) {
        nurseSocketClient.connect()

        serviceScope.launch {
            nurseSocketClient.subscribeToReservation(requestId)
        }

        eventCollectorJob?.cancel()
        eventCollectorJob = serviceScope.launch {
            nurseSocketClient.reservationEvents.collectLatest { event ->
                if (event.reservationId == requestId || event.reservationId.isNullOrEmpty()) {
                    handleReservationEvent(event.eventType, requestId)
                }
            }
        }
    }

    private fun handleReservationEvent(eventType: ReservationEventType, requestId: String) {
        when (eventType) {
            ReservationEventType.OFFER_ACCEPTED -> {
                updateNotification(
                    getString(R.string.reservation_confirmed_title),
                    getString(R.string.reservation_confirmed_message),
                    requestId,
                )
            }
            ReservationEventType.OFFER_COUNTERED -> {
                updateNotification(
                    getString(R.string.offer_countered_title),
                    getString(R.string.offer_countered_message),
                    requestId,
                )
            }
            ReservationEventType.OFFER_UPDATED -> {
                updateNotification(
                    getString(R.string.offer_updated_title),
                    getString(R.string.offer_updated_message),
                    requestId,
                )
            }
            ReservationEventType.REQUEST_CANCELLED -> {
                updateNotification(
                    getString(R.string.reservation_cancelled_title),
                    getString(R.string.reservation_cancelled_message),
                    requestId,
                )
                serviceScope.launch {
                    delay(5000.milliseconds)
                    stopForegroundService()
                }
            }
            ReservationEventType.COMPLETED -> {
                updateNotification(
                    getString(R.string.visit_completed_title),
                    getString(R.string.visit_completed_message),
                    requestId,
                )
                serviceScope.launch {
                    delay(5000.milliseconds)
                    stopForegroundService()
                }
            }
            else -> {
                updateNotification(
                    getString(R.string.active_reservation_title),
                    getString(R.string.reservation_update_received),
                    requestId,
                )
            }
        }
    }

    private fun updateNotification(title: String, content: String, requestId: String) {
        val notification = buildNotification(title, content, requestId)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(title: String, content: String, requestId: String) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createPendingIntent(requestId))
            .build()

    private fun createPendingIntent(requestId: String): PendingIntent {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_SERVICE_REQUEST_ID, requestId)
        } ?: Intent()

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            this,
            PENDING_INTENT_REQUEST_CODE,
            launchIntent,
            flags
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.reservation_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.reservation_channel_description)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun stopForegroundService() {
        currentServiceRequestId?.let { id ->
            serviceScope.launch {
                try {
                    nurseSocketClient.unsubscribeFromReservation(id)
                } catch (_: Exception) { }
            }
        }
        setActiveReservationId(this, null)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        setActiveReservationId(this, null)
        eventCollectorJob?.cancel()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun String?.isNull_OrBlank(): Boolean = this == null || this.trim().isEmpty()

    companion object {
        const val EXTRA_SERVICE_REQUEST_ID = "EXTRA_SERVICE_REQUEST_ID"
        const val ACTION_START_SERVICE = "com.carenest.provider.ACTION_START_ACTIVE_RESERVATION_SERVICE"
        const val ACTION_STOP_SERVICE = "com.carenest.provider.ACTION_STOP_ACTIVE_RESERVATION_SERVICE"

        private const val NOTIFICATION_ID = 1001
        private const val PENDING_INTENT_REQUEST_CODE = 2001
        private const val CHANNEL_ID = "active_reservation_channel"

        private const val PREFS_NAME = "carenest_active_reservation_prefs"
        private const val KEY_ACTIVE_RESERVATION_ID = "KEY_ACTIVE_RESERVATION_ID"

        @Volatile
        var activeReservationId: String? = null
            private set

        fun getActiveReservationId(context: Context): String? {
            if (!activeReservationId.isNullOrBlank()) return activeReservationId
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val storedId = prefs.getString(KEY_ACTIVE_RESERVATION_ID, null)
            return if (storedId.isNullOrBlank()) null else storedId
        }

        fun isServiceRunning(context: Context): Boolean {
            return getActiveReservationId(context) != null
        }

        private fun setActiveReservationId(context: Context, id: String?) {
            activeReservationId = id
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            if (id.isNullOrBlank()) {
                prefs.edit().remove(KEY_ACTIVE_RESERVATION_ID).apply()
            } else {
                prefs.edit().putString(KEY_ACTIVE_RESERVATION_ID, id).apply()
            }
        }

        fun startService(context: Context, serviceRequestId: String) {
            val intent = Intent(context, ActiveReservationService::class.java).apply {
                action = ACTION_START_SERVICE
                putExtra(EXTRA_SERVICE_REQUEST_ID, serviceRequestId)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, ActiveReservationService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
        }
    }
}
