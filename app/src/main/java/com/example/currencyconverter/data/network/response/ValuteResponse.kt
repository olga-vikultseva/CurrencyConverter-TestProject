package com.example.currencyconverter.data.network.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "ValCurs", strict = false)
class ValuteResponse {
    @field: ElementList(inline = true)
    var valutes: List<Valute> = mutableListOf()
}

@Root(name = "Valute", strict = false)
class Valute @JvmOverloads constructor(
    @field:Element(name = "Name")
    var name: String = "",
    @field:Element(name = "CharCode")
    var code: String = "",
    @field:Element(name = "Nominal")
    var nominal: String = "",
    @field:Element(name = "Value")
    var rubleRate: String = ""
)