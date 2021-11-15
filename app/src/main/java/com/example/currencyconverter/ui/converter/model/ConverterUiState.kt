package com.example.currencyconverter.ui.converter.model

sealed class ConverterUiState {
    object Loading : ConverterUiState()

    data class Content(
        val originalValue: String,
        val originalCurrencyCode: String,
        val finalValue: String,
        val finalCurrencyCode: String
    ) : ConverterUiState()

    data class Error(val message: String) : ConverterUiState()
}
