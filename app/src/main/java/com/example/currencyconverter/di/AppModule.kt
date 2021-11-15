package com.example.currencyconverter.di

import com.example.currencyconverter.data.CurrencyRatesDataSource
import com.example.currencyconverter.data.CurrencyRatesDataSourceImpl
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.CurrencyRepositoryImpl
import com.example.currencyconverter.data.network.CurrencyRatesApiService
import com.example.currencyconverter.domain.ConverterInteractor
import com.example.currencyconverter.domain.ConverterInteractorImpl
import com.example.currencyconverter.domain.CurrencyValueFormat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideLocale(): Locale =
        Locale.UK

    @Provides
    @Singleton
    fun provideCurrencyRatesApiService(): CurrencyRatesApiService =
        CurrencyRatesApiService.invoke()

    @Provides
    @Singleton
    fun provideCurrencyRatesDataSource(currencyRatesApiService: CurrencyRatesApiService): CurrencyRatesDataSource =
        CurrencyRatesDataSourceImpl(currencyRatesApiService)

    @Provides
    @Singleton
    fun provideCurrencyRepository(currencyRatesDataSource: CurrencyRatesDataSource): CurrencyRepository =
        CurrencyRepositoryImpl(currencyRatesDataSource)

    @Provides
    @Singleton
    fun provideConverterInteractor(
        currencyValueFormat: CurrencyValueFormat,
        currencyRepository: CurrencyRepository
    ): ConverterInteractor =
        ConverterInteractorImpl(
            currencyParser = currencyValueFormat,
            currencyRepository = currencyRepository
        )

    @Provides
    @Singleton
    fun provideCurrencyValueFormat(locale: Locale): CurrencyValueFormat =
        CurrencyValueFormat(locale)
}