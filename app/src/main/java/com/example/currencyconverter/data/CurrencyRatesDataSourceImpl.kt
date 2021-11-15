package com.example.currencyconverter.data

import com.example.currencyconverter.data.network.CurrencyRatesApiService
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyRate

class CurrencyRatesDataSourceImpl(
    private val currencyRatesApiService: CurrencyRatesApiService
) : CurrencyRatesDataSource {

    override suspend fun getCurrencyRates(): List<CurrencyRate> =
        currencyRatesApiService.getCurrencyRates().valutes.map {
            CurrencyRate(
                currency = Currency(it.name, it.code),
                nominal = it.nominal.toInt(),
                rubleRate = it.rubleRate.replace(",", ".").toBigDecimal()
            )
        }
}