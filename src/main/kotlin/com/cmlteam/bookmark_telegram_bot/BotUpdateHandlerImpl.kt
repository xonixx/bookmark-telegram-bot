package com.cmlteam.bookmark_telegram_bot

import com.cmlteam.telegram_bot_common.BotUpdateHandler
import com.cmlteam.telegram_bot_common.Emoji
import com.cmlteam.telegram_bot_common.TelegramSender
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.ForwardMessage
import org.slf4j.LoggerFactory

class BotUpdateHandlerImpl(
    private val bookmarkService: BookmarkService,
    private val adminUserChecker: AdminUserChecker,
    private val maxFileSize: Int
) : BotUpdateHandler {
  override fun processUpdate(telegramSender: TelegramSender, update: Update) {
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
            telegramSender.sendText(
                chatId,
                "This is Bookmarks bot, see https://github.com/xonixx/bookmark-telegram-bot/blob/main/README.md")
          }
          BotCommandType.RANDOM -> {
            val randomBookmark = bookmarkService.getRandom(userId)
            if (randomBookmark == null) {
              telegramSender.sendText(chatId, Emoji.WARN.msg("You don't have any bookmarks yet"))
            } else {
              telegramSender.sendText(
                  chatId, "${randomBookmark.url}\n\n/random /mark_read_${randomBookmark.id}")
            }
          }
          BotCommandType.MARK_READ -> {
            val bookmark = bookmarkService.markRead(command.id!!, userId, true)
            if (bookmark == null) {
              telegramSender.sendText(chatId, Emoji.ERROR.msg("Unknown ID!"))
            } else {
              telegramSender.sendText(
                  chatId,
                  Emoji.SUCCESS.msg(
                      "${bookmark.url} marked read.\n/undo_${bookmark.id}\n" +
                          "Links in backlog: ${bookmarkService.getTotal(userId)} /random"))
            }
          }
          BotCommandType.UNDO -> {
            val bookmark = bookmarkService.markRead(command.id!!, userId, false)
            if (bookmark == null) {
              telegramSender.sendText(chatId, Emoji.ERROR.msg("Unknown ID!"))
            } else {
              telegramSender.sendText(
                  chatId,
                  Emoji.SUCCESS.msg(
                      "${bookmark.url} marked unread.\n/mark_read_${bookmark.id}\n" +
                          "Links in backlog: ${bookmarkService.getTotal(userId)} /random"))
            }
          }
        }
      } else if (isValidUrl(text)) {
        val existing = bookmarkService.storeBookmark(Bookmark(text, userId, messageId))
        telegramSender.sendText(
            chatId,
            (if (existing == null) Emoji.SUCCESS.msg("Ok, saved link.")
            else
                Emoji.WARN.msg(
                    "Already in backlog ${if (existing.read) "/undo_" else "/mark_read_"}${existing.id}.")) +
                " Links in backlog: ${bookmarkService.getTotal(userId)} /random")
      } else if (text != null) {
        val links = extractLinks(text)
        if (links.isEmpty()) {
          telegramSender.sendText(chatId, Emoji.WARN.msg("I don't understand..."))
        } else {
          val saved = bookmarkService.storeBookmarks(links.map { Bookmark(it, userId, messageId) })
          telegramSender.sendText(
              chatId,
              Emoji.SUCCESS.msg(
                  "Ok, saved $saved new of ${links.size} links. " +
                      "Links in backlog: ${bookmarkService.getTotal(userId)} /random"))
        }
      } else {
        telegramSender.sendText(chatId, Emoji.WARN.msg("I don't understand..."))
      }
      if (adminUserChecker.isAdmin(user)) {
        // XXX admin commands
      } else {
        forwardMessageToAdmin(telegramSender, messageId, chatId)
      }
    }
  }

  private fun forwardMessageToAdmin(telegramSender: TelegramSender, messageId: Int, chatId: Long) {
    telegramSender.execute(ForwardMessage(adminUserChecker.adminUser, chatId, messageId))
  }

  companion object {
    private val log = LoggerFactory.getLogger(BotUpdateHandlerImpl::class.java)
  }
}
