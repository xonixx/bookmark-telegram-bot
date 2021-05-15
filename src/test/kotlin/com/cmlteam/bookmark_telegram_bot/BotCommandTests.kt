package com.cmlteam.bookmark_telegram_bot

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BotCommandTests {
    @Test
    fun test1() {
        assertTrue(BotCommand.MARK_READ.isCommand("/mark_read_123"))
    }

    @Test
    fun test2() {
        assertEquals("123", BotCommand.MARK_READ.extractId("/mark_read_123"))
    }
}