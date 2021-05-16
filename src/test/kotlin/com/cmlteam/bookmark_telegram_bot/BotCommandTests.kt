package com.cmlteam.bookmark_telegram_bot

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BotCommandTests {
    @Test
    fun test1() {
        assertTrue(BotCommand.parse("/mark_read_123")!!.type == BotCommandType.MARK_READ)
    }

    @Test
    fun test2() {
        assertEquals("123", BotCommand.parse("/mark_read_123")!!.id)
    }

    @Test
    fun test3() {
        assertNull(BotCommand.parse("/unknown"))
    }

    @Test
    fun test4() {
        assertNull(BotCommand.parse("/start_123"))
    }

    @Test
    fun test5() {
        assertNull(BotCommand.parse("some text..."))
    }

    @Test
    fun test6() {
        assertNull(BotCommand.parse(null))
    }
}