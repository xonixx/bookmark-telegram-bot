package com.cmlteam.bookmark_telegram_bot

import java.util.stream.Stream

enum class BotCommand(private val cmd: String, val isAdminCommand: Boolean) {
    START("start", false),
    DELETE("delete", false),
    BACKUP("backup", true),
    REVIVE("revive", true);

    fun `is`(commandCandidate: String): Boolean {
        return "/$cmd" == commandCandidate
    }

    companion object {
        fun isAdminCommand(commandCandidate: String): Boolean {
            return Stream.of(*values())
                .anyMatch { botCommand: BotCommand -> botCommand.`is`(commandCandidate) && botCommand.isAdminCommand }
        }
    }
}