package com.example.currencyconverter.ui.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.domain.ConverterInteractor
import com.example.currencyconverter.domain.CurrencyValueFormat
import com.example.currencyconverter.domain.model.ConverterState
import com.example.currencyconverter.ui.converter.model.ConverterUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(
    private val currencyFormatter: CurrencyValueFormat,
    private val converterInteractor: ConverterInteractor
) : ViewModel() {

    val converterUiState = converterInteractor.converterStateFlow
        .map { converterState ->
            when (converterState) {
                ConverterState.Loading -> ConverterUiState.Loading
                is ConverterState.Content -> ConverterUiState.Content(
                    originalValue = converterState.originalValue.format(),
                    originalCurrencyCode = converterState.originalCurrencyCode,
                    finalValue = converterState.finalValue.format(),
                    finalCurrencyCode = converterState.finalCurrencyCode
                )
                is ConverterState.Error -> ConverterUiState.Error(
                    message = converterState.message
                )
            }
        }
        .asLiveData()

    init {
        viewModelScope.launch { converterInteractor.requestCurrencyRatesUpdate() }
    }

    fun onOriginalValueChanged(value: String) {
        converterInteractor.updateOriginalValue(value)
    }

    fun onFinalValueChanged(value: String) {
        converterInteractor.updateFinalValue(value)
    }

    fun onTryAgainClicked() {
        viewModelScope.launch {
            converterInteractor.requestCurrencyRatesUpdate()
        }
    }

    private fun BigDecimal.format(): String =
        currencyFormatter.formatCurrencyValue(this)
}