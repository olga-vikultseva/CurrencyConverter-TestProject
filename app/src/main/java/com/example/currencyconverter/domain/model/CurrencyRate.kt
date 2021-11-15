package com.example.currencyconverter.domain.model

import java.math.BigDecimal

data class CurrencyRate(
    val currency: Currency,
    val nominal: Int,
    val rubleRate: BigDecimal
)