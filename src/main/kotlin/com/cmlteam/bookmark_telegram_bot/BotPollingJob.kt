package com.cmlteam.bookmark_telegram_bot

import com.cmlteam.telegram_bot_common.Emoji
import com.cmlteam.telegram_bot_common.JsonHelper
import com.cmlteam.telegram_bot_common.LogHelper
import com.cmlteam.telegram_bot_common.TelegramBotWrapper
import com.pengrad.telegrambot.request.ForwardMessage
import com.pengrad.telegrambot.request.GetUpdates
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class BotPollingJob(
    private val telegramBot: TelegramBotWrapper,
    private val bookmarkService: BookmarkService,
    private val jsonHelper: JsonHelper,
    private val logHelper: LogHelper,
    private val adminUserChecker: AdminUserChecker,
    private val maxFileSize: Int
) {
    private val getUpdates = GetUpdates()

    @Scheduled(fixedRate = 400)
    fun processUpdates() {
        val updatesResponse = telegramBot.execute(getUpdates)
        if (!updatesResponse.isOk) {
            return
        }
        val updates = updatesResponse.updates()
        for (update in updates) {
            logHelper.captureLogParams(update)
            log.info(
                """
    Received:
    ${jsonHelper.toPrettyString(update)}
    """.trimIndent()
            )
            val message = update.message()
            if (message != null) {
                val chatId = message.chat().id()
                val messageId = message.messageId()
                val text = message.text()
                val user = message.from()
                val userId = user.id()
                val command = BotCommand.parse(text)
                if (command != null) {
                    when (command.type) {
                        BotCommandType.START -> {
                            telegramBot.sendText(chatId, "TODO bot description") // TODO
                        }
                        BotCommandType.RANDOM -> {
                            val randomBookmark = bookmarkService.getRandom(userId)
                            if (randomBookmark == null) {
                                telegramBot.sendText(chatId, Emoji.WARN.msg("You don't have any bookmarks yet"))
                            } else {
                                telegramBot.sendText(
                                    chatId,
                                    "${randomBookmark.url}\n\n/random /mark_read_${randomBookmark.id}"
                                )
                            }
                        }
                        else -> {
                            telegramBot.sendText(chatId, Emoji.WARN.msg("I don't understand..."))
                        }
                    }
                } else if (isValidUrl(text)) {
                    bookmarkService.save(Bookmark(text, userId, messageId))
                    telegramBot.sendText(
                        chatId,
                        Emoji.SUCCESS.msg("Ok, saved link. Links in backlog: ${bookmarkService.getTotal(userId)} /random")
                    )
                } else {
                    telegramBot.sendText(chatId, Emoji.WARN.msg("I don't understand..."))
                }
                if (adminUserChecker.isAdmin(user)) {
//                    if (BotCommand.BACKUP.`is`(text)) {
//                        videosBackupper.startBackup(userId)
//                    } else if (BotCommand.REVIVE.`is`(text)) {
//                        videosReviver.revive(userId)
//                    }
                } else {
                    forwardMessageToAdmin(messageId, chatId)
                }
            }
            getUpdates.offset(update.updateId() + 1)
        }
    }

    private fun forwardMessageToAdmin(messageId: Int, chatId: Long) {
        telegramBot.execute(ForwardMessage(adminUserChecker.adminUser, chatId, messageId))
    }

    companion object {
        private val log = LoggerFactory.getLogger(BotPollingJob::class.java)
    }
}