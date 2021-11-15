package com.example.currencyconverter.domain

import com.example.currencyconverter.domain.model.ConverterState
import com.example.currencyconverter.domain.model.Currency
import kotlinx.coroutines.flow.Flow

interface ConverterInteractor {
    val currencyListFlow: Flow<List<Currency>>
    val converterStateFlow: Flow<ConverterState>

    fun updateOriginalValue(value: String)
    fun updateFinalValue(value: String)
    fun updateOriginalCurrency(currencyCode: String)
    fun updateFinalCurrency(currencyCode: String)

    suspend fun requestCurrencyRatesUpdate()
}