package com.example.currencyconverter.data.network

import com.example.currencyconverter.data.network.response.ValuteResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET

/**
 * https://www.cbr.ru/scripts/XML_daily.asp
 */
interface CurrencyRatesApiService {

    @GET("scripts/XML_daily.asp")
    suspend fun getCurrencyRates(): ValuteResponse

    companion object {

        operator fun invoke(): CurrencyRatesApiService {
            val logging = HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
                .create(CurrencyRatesApiService::class.java)
        }

        private const val BASE_URL = "https://www.cbr.ru/"
    }
}