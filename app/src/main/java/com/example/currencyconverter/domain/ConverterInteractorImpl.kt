package com.example.currencyconverter.domain

import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.domain.model.ConverterState
import com.example.currencyconverter.domain.model.Currency
import com.example.currencyconverter.domain.model.CurrencyRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal
import java.net.UnknownHostException

class ConverterInteractorImpl(
    private val currencyParser: CurrencyValueFormat,
    private val currencyRepository: CurrencyRepository
) : ConverterInteractor {

    private val _converterStateFlow = MutableStateFlow<ConverterState>(ConverterState.Loading)
    override val converterStateFlow = _converterStateFlow as StateFlow<ConverterState>

    private val _currencyListFlow = MutableStateFlow<List<Currency>>(emptyList())
    override val currencyListFlow = _currencyListFlow as StateFlow<List<Currency>>

    override suspend fun requestCurrencyRatesUpdate() {
        _converterStateFlow.value = ConverterState.Loading
        try {
            val rates = currencyRepository.updateCurrencyRates()
            _currencyListFlow.value = rates.map { it.currency }
            _converterStateFlow.value = rates.toState()
        } catch (e: Throwable) {
            _converterStateFlow.value = ConverterState.Error(
                when (e) {
                    is UnknownHostException -> "Seems like we cannot reach our web site, check you connection please."
                    else -> "Something went wrong while getting the currency rates. Try again later."
                }
            )
        }
    }

    private fun List<CurrencyRate>.toState(): ConverterState =
        when {
            this.size >= MIN_RATES_COUNT -> {
                val (originalCurrency, finalCurrency) = this
                ConverterState.Content(
                    originalValue = DEFAULT_ORIGINAL_VALUE,
                    originalCurrencyCode = originalCurrency.currency.code,
                    finalValue = DEFAULT_FINAL_VALUE,
                    finalCurrencyCode = finalCurrency.currency.code,
                    rates = normalizeCurrencyRates(this)
                )
            }
            else -> {
                ConverterState.Error(
                    message = "Sorry, we don't have enough currencies."
                )
            }
        }

    override fun updateOriginalValue(value: String) {
        val converterData = (_converterStateFlow.value as? ConverterState.Content) ?: return
        val newValue = value.parseCurrencyValue()
        _converterStateFlow.value = converterData.calculateFinalValue(newOriginalValue = newValue)
    }

    override fun updateFinalValue(value: String) {
        val converterData = (_converterStateFlow.value as? ConverterState.Content) ?: return
        val newValue = value.parseCurrencyValue()
        _converterStateFlow.value = converterData.calculateOriginalValue(newFinalValue = newValue)
    }

    override fun updateOriginalCurrency(currencyCode: String) {
        val converterData = (_converterStateFlow.value as? ConverterState.Content) ?: return
        _converterStateFlow.value = converterData.calculateFinalValue(newOriginalCurrencyCode = currencyCode)
    }

    override fun updateFinalCurrency(currencyCode: String) {
        val converterData = (_converterStateFlow.value as? ConverterState.Content) ?: return
        _converterStateFlow.value = converterData.calculateFinalValue(newFinalCurrencyCode = currencyCode)
    }

    private fun ConverterState.Content.calculateFinalValue(
        newOriginalValue: BigDecimal = this.originalValue,
        newOriginalCurrencyCode: String = this.originalCurrencyCode,
        newFinalCurrencyCode: String = this.finalCurrencyCode
    ): ConverterState {
        val originalCurrencyRubleRate = getRubleRate(newOriginalCurrencyCode)
        val finalCurrencyRubleRate = getRubleRate(newFinalCurrencyCode)

        return if (originalCurrencyRubleRate == null || finalCurrencyRubleRate == null) {
            ConverterState.Error("We cannot find the selected currency in the list, try to update")
        } else {
            copy(
                originalCurrencyCode = newOriginalCurrencyCode,
                originalValue = newOriginalValue,
                finalCurrencyCode = newFinalCurrencyCode,
                finalValue = (newOriginalValue * originalCurrencyRubleRate) / finalCurrencyRubleRate
            )
        }
    }

    private fun ConverterState.Content.calculateOriginalValue(
        newFinalValue: BigDecimal = this.finalValue
    ): ConverterState {
        val finalCurrencyRubleRate = getRubleRate(finalCurrencyCode)
        val originalCurrencyRubleRate = getRubleRate(originalCurrencyCode)

        return if (finalCurrencyRubleRate == null || originalCurrencyRubleRate == null) {
            ConverterState.Error("We cannot find the selected currency in the list, try to update")
        } else {
            copy(
                finalValue = newFinalValue,
                originalValue = (newFinalValue * finalCurrencyRubleRate) / originalCurrencyRubleRate
            )
        }
    }

    private fun ConverterState.Content.getRubleRate(currencyCode: String): BigDecimal? =
        rates.find { it.currency.code == currencyCode }?.rubleRate

    private fun String.parseCurrencyValue(): BigDecimal =
        currencyParser.parseCurrencyValue(this) ?: BigDecimal.ZERO

    private fun normalizeCurrencyRates(rates: List<CurrencyRate>): List<CurrencyRate> =
        rates.map { currencyRate ->
            currencyRate.copy(
                nominal = 1,
                rubleRate = currencyRate.rubleRate / currencyRate.nominal.toBigDecimal()
            )
        }

    companion object {
        private const val MIN_RATES_COUNT = 2
        private val DEFAULT_ORIGINAL_VALUE = BigDecimal.ZERO
        private val DEFAULT_FINAL_VALUE = BigDecimal.ZERO
    }
}