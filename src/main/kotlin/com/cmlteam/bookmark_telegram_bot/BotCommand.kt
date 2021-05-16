package com.cmlteam.bookmark_telegram_bot

class BotCommand(val type: BotCommandType, val id: String?) {
    companion object {
        fun parse(cmd: String?): BotCommand? {
            if (cmd == null)
                return null
            for (type in BotCommandType.values()) {
                if (type.isCommand(cmd)) {
                    return BotCommand(type, type.extractId(cmd))
                }
            }
            return null
        }
    }
}

enum class BotCommandType(private val cmd: String, val hasId: Boolean, val isAdminCommand: Boolean) {
    START("start", false, false),
    RANDOM("random", false, false),
    MARK_READ("mark_read", true, false),
    UNDO("undo", true, false);

    fun isCommand(commandCandidate: String): Boolean {
        return if (hasId)
            commandCandidate.startsWith("/${cmd}_")
        else
            "/$cmd" == commandCandidate
    }

    fun extractId(command: String): String? {
        return if (hasId) command.removePrefix("/${cmd}_") else null
    }

    companion object {
        fun isAdminCommand(commandCandidate: String): Boolean {
            return values().any { it.isCommand(commandCandidate) && it.isAdminCommand }
        }
    }
}