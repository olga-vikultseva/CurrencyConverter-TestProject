package com.example.currencyconverter.domain.model

import java.math.BigDecimal

sealed class ConverterState {
    object Loading : ConverterState()

    data class Content(
        val originalValue: BigDecimal,
        val originalCurrencyCode: String,
        val finalValue: BigDecimal,
        val finalCurrencyCode: String,
        val rates: List<CurrencyRate>
    ) : ConverterState()

    data class Error(val message: String) : ConverterState()
}
