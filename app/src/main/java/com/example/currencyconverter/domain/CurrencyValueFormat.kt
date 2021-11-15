package com.example.currencyconverter.domain

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class CurrencyValueFormat(locale: Locale) {

    private val numberFormat = NumberFormat.getNumberInstance(locale)

    fun formatCurrencyValue(value: BigDecimal): String =
        numberFormat.format(value)

    fun parseCurrencyValue(value: String): BigDecimal? =
        try {
            if (numberFormat is DecimalFormat) {
                numberFormat.isParseBigDecimal = true
                numberFormat.parse(value) as? BigDecimal
            } else {
                value.replace(",", "").toBigDecimalOrNull()
            }
        } catch (e: Exception) {
            null
        }
}