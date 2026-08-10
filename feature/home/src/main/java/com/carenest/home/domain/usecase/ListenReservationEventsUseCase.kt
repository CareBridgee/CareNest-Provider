package com.carenest.home.domain.usecase

import com.carenest.home.domain.repository.NurseRequestsRepository
import com.carenest.provider.core.network.socket.model.ReservationEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListenReservationEventsUseCase @Inject constructor(
    private val repository: NurseRequestsRepository
) {
    operator fun invoke(reservationId: String): Flow<ReservationEvent> =
        repository.listenReservationEvents(reservationId)
}
