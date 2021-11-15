package com.example.currencyconverter.data

import com.example.currencyconverter.domain.model.CurrencyRate

class CurrencyRepositoryImpl(
    private val currencyRatesDataSource: CurrencyRatesDataSource
) : CurrencyRepository {

    override suspend fun updateCurrencyRates(): List<CurrencyRate> =
        currencyRatesDataSource.getCurrencyRates()
}