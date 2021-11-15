package com.example.currencyconverter.data

import com.example.currencyconverter.domain.model.CurrencyRate

interface CurrencyRatesDataSource {
    suspend fun getCurrencyRates(): List<CurrencyRate>
}