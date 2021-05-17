package com.cmlteam.bookmark_telegram_bot

import java.net.MalformedURLException
import java.net.URL

private val space = "\\s".toRegex()

fun isValidUrl(candidate: String?): Boolean {
    if (candidate == null) return false

    try {
        URL(candidate)
    } catch (ex: MalformedURLException) {
        return false
    }

    val candidateLc = candidate.toLowerCase()
    return (candidateLc.startsWith("https:") || candidateLc.startsWith("http:")) && !candidateLc.contains(space)
}

fun extractLinks(text: String): List<String> {
    return text.lines().map { it.trim() }.filter { isValidUrl(it) }
}