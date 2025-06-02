package com.example.financetracker.database.model

enum class CurrencyType(val symbol: String, val displayName: String) {
    EUR("€", "Euro"),
    USD("$", "US Dollar"),
    GBP("£", "British Pound"),
    JPY("¥", "Japanese Yen"),
    CHF("CHF", "Swiss Franc"),
    AUD("A$", "Australian Dollar"),
    CAD("C$", "Canadian Dollar"),
    SEK("kr", "Swedish Krona"),
    NOK("kr", "Norwegian Krone"),
    PLN("zł", "Polish Złoty");

    override fun toString(): String = "$displayName ($symbol)"
}