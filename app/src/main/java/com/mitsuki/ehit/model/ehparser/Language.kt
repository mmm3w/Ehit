package com.mitsuki.ehit.model.ehparser

import android.text.TextUtils

object Language {
    val LANGUAGE_SET = arrayOf(
        "language:english",
        "language:chinese",
        "language:spanish",
        "language:korean",
        "language:russian",
        "language:french",
        "language:portuguese",
        "language:thai",
        "language:german",
        "language:italian",
        "language:vietnamese",
        "language:polish",
        "language:hungarian",
        "language:dutch"
    )

    val LANGUAGE_S_SET = arrayOf(
        "EN",
        "ZH",
        "ES",
        "KO",
        "RU",
        "FR",
        "PT",
        "TH",
        "DE",
        "IT",
        "VI",
        "PL",
        "HU",
        "NL",
        "JA"
    )

    fun getLangSimple(lang: String): String {
        if (TextUtils.isEmpty(lang)) return ""
        for (i in LANGUAGE_SET.indices) {
            if (LANGUAGE_SET[i].contains(lang, ignoreCase = true)) {
                return LANGUAGE_S_SET[i]
            }
        }
        return ""
    }
}