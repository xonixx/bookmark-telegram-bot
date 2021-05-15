package com.cmlteam.bookmark_telegram_bot

// TODO how to disallow /mark_read (no ID)?
enum class BotCommand(private val cmd: String, val isAdminCommand: Boolean) {
    START("start", false),
    RANDOM("random", false),
    MARK_READ("mark_read", false),
    UNDO("undo", false);

    fun isCommand(commandCandidate: String): Boolean {
        return commandCandidate.startsWith("/$cmd")
    }

    fun extractId(command: String): String {
        if (!isCommand(command)) throw IllegalArgumentException("Not my command: $command")
        return command.removePrefix("/${cmd}_")
    }

    companion object {
        fun isAdminCommand(commandCandidate: String): Boolean {
            return values().any { it.isCommand(commandCandidate) && it.isAdminCommand }
        }
    }
}