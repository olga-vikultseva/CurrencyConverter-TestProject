package com.example.currencyconverter.data

import com.example.currencyconverter.domain.model.CurrencyRate

interface CurrencyRepository {
    suspend fun updateCurrencyRates(): List<CurrencyRate>
}