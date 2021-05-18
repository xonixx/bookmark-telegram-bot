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
                            telegramBot.sendText(
                                chatId,
                                "This is Bookmarks bot, see https://github.com/xonixx/bookmark-telegram-bot/blob/main/README.md"
                            )
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
                        BotCommandType.MARK_READ -> {
                            val bookmark = bookmarkService.markRead(command.id!!, userId, true)
                            if (bookmark == null) {
                                telegramBot.sendText(chatId, Emoji.ERROR.msg("Unknown ID!"))
                            } else {
                                telegramBot.sendText(
                                    chatId, Emoji.SUCCESS.msg(
                                        "${bookmark.url} marked read.\n/undo_${bookmark.id}\n" +
                                                "Links in backlog: ${bookmarkService.getTotal(userId)} /random"
                                    )
                                )
                            }
                        }
                        BotCommandType.UNDO -> {
                            val bookmark = bookmarkService.markRead(command.id!!, userId, false)
                            if (bookmark == null) {
                                telegramBot.sendText(chatId, Emoji.ERROR.msg("Unknown ID!"))
                            } else {
                                telegramBot.sendText(
                                    chatId, Emoji.SUCCESS.msg(
                                        "${bookmark.url} marked unread.\n/mark_read_${bookmark.id}\n" +
                                                "Links in backlog: ${bookmarkService.getTotal(userId)} /random"
                                    )
                                )
                            }
                        }
                    }
                } else if (isValidUrl(text)) {
                    bookmarkService.save(Bookmark(text, userId, messageId))
                    telegramBot.sendText(
                        chatId,
                        Emoji.SUCCESS.msg("Ok, saved link. Links in backlog: ${bookmarkService.getTotal(userId)} /random")
                    )
                } else if (text != null) {
                    val links = extractLinks(text)
                    if (links.isEmpty()) {
                        telegramBot.sendText(chatId, Emoji.WARN.msg("I don't understand..."))
                    } else {
                        links.forEach {
                            bookmarkService.save(Bookmark(it, userId, messageId))
                        }
                        telegramBot.sendText(
                            chatId,
                            Emoji.SUCCESS.msg(
                                "Ok, saved ${links.size} links. Links in backlog: ${
                                    bookmarkService.getTotal(
                                        userId
                                    )
                                } /random"
                            )
                        )
                    }
                } else {
                    telegramBot.sendText(chatId, Emoji.WARN.msg("I don't understand..."))
                }
                if (adminUserChecker.isAdmin(user)) {
                    // XXX admin commands
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