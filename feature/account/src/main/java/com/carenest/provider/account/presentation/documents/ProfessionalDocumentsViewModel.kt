package com.carenest.provider.account.presentation.documents

import androidx.lifecycle.ViewModel
import com.carenest.provider.core.mvi.DefaultEffectPublisher
import com.carenest.provider.core.mvi.DefaultStateHolder
import com.carenest.provider.core.mvi.EffectPublisher
import com.carenest.provider.core.mvi.StateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfessionalDocumentsViewModel @Inject constructor() : ViewModel(),
    StateHolder<ProfessionalDocumentsUiState> by DefaultStateHolder(ProfessionalDocumentsUiState()),
    EffectPublisher<ProfessionalDocumentsEffect> by DefaultEffectPublisher() {

    fun onIntent(intent: ProfessionalDocumentsIntent) {
        when (intent) {
            ProfessionalDocumentsIntent.BackClicked ->
                sendEffect(ProfessionalDocumentsEffect.NavigateBack)
            is ProfessionalDocumentsIntent.DocumentClicked ->
                sendEffect(ProfessionalDocumentsEffect.OpenDocument(intent.id))
            ProfessionalDocumentsIntent.UploadDocumentClicked ->
                sendEffect(ProfessionalDocumentsEffect.UploadDocument)
        }
    }
}
