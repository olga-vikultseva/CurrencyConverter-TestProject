package com.example.currencyconverter.ui.currencypicker

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.currencyconverter.domain.ConverterInteractor
import com.example.currencyconverter.domain.model.ConverterState
import com.example.currencyconverter.ui.currencypicker.model.CurrencyPickerItem
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance

class CurrencyPickerViewModel @AssistedInject constructor(
    @Assisted private val isOriginal: Boolean,
    private val converterInteractor: ConverterInteractor
) : ViewModel() {

    val currencyPickerItems: LiveData<List<CurrencyPickerItem>> =
        createCurrencyItemsLiveData()

    fun onCurrencyClicked(currencyCode: String) {
        if (isOriginal) {
            converterInteractor.updateOriginalCurrency(currencyCode)
        } else {
            converterInteractor.updateFinalCurrency(currencyCode)
        }
    }

    private fun createCurrencyItemsLiveData(): LiveData<List<CurrencyPickerItem>> {
        val converterStateFlow =
            converterInteractor.converterStateFlow.filterIsInstance<ConverterState.Content>()

        return converterInteractor.currencyListFlow.combine(converterStateFlow) { currencyList, converterState ->
            val selectedCurrencyCode =
                if (isOriginal) converterState.originalCurrencyCode else converterState.finalCurrencyCode

            currencyList.map { currency ->
                CurrencyPickerItem(
                    currency = currency,
                    isSelected = currency.code == selectedCurrencyCode
                )
            }
        }.asLiveData()
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(isOriginal: Boolean): CurrencyPickerViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            isOriginal: Boolean
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                assistedFactory.create(isOriginal) as T
        }
    }
}