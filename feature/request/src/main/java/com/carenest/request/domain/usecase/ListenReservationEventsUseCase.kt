package com.carenest.request.domain.usecase

import com.carenest.provider.core.network.socket.model.ReservationEvent
import com.carenest.request.domain.repository.NurseRequestsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenReservationEventsUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    operator fun invoke(reservationId: String): Flow<ReservationEvent> =
        repository.listenReservationEvents(reservationId)
}