package com.example.currencyconverter.ui.currencypicker.model

import com.example.currencyconverter.domain.model.Currency

data class CurrencyPickerItem(
    val currency: Currency,
    val isSelected: Boolean
)