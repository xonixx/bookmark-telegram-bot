package com.cmlteam.bookmark_telegram_bot

import com.cmlteam.telegram_bot_common.Emoji
import com.cmlteam.telegram_bot_common.JsonHelper
import com.cmlteam.telegram_bot_common.LogHelper
import com.cmlteam.telegram_bot_common.TelegramBotWrapper
import com.cmlteam.util.Util
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultCachedVideo
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.pengrad.telegrambot.request.ForwardMessage
import com.pengrad.telegrambot.request.GetUpdates
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class BotPollingJob(
    private val telegramBot: TelegramBotWrapper,
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
                val video = message.video()
                val user = message.from()
                val userId = user.id()
                val replyToMessage = message.replyToMessage()
                val replyToVideo = replyToMessage?.video()
                if (BotCommand.START.`is`(text)) {
                    telegramBot.sendText(chatId, "Please start from uploading video")
                } else if (BotCommand.DELETE.`is`(text)) {
//                    handleDeleteVideo(chatId, userId, replyToVideo)
                } else if (StringUtils.isNotBlank(text)) {
                    telegramBot.sendText(chatId, "Hello world!")
                } else {
                    telegramBot.sendText(
                        chatId,
                        Emoji.WARN.msg(
                            "The uploaded document doesn't look like .MP4 video. "
                                    + "Please try again with other file."
                        )
                    )
                }
                if (adminUserChecker.isAdmin(user)) {
                    if (BotCommand.BACKUP.`is`(text)) {
//                        videosBackupper.startBackup(userId)
                    } else if (BotCommand.REVIVE.`is`(text)) {
//                        videosReviver.revive(userId)
                    }
                } else {
                    forwardMessageToAdmin(messageId, chatId)
                }
            }
            val inlineQuery = update.inlineQuery()
            inlineQuery?.let { handleInlineQuery(update, it) }
            getUpdates.offset(update.updateId() + 1)
        }
    }

    private fun handleInlineQuery(update: Update, inlineQuery: InlineQuery) {

        var query = inlineQuery.query()
        val offset = inlineQuery.offset()
/*        var isSize = false
        if ("!size" == query || query.startsWith("!size ")) {
            isSize = true
            query = query.replaceFirst("^!size".toRegex(), "").trim { it <= ' ' }
        }
        val videosPage: VideosPage = videosService.searchVideo(inlineQuery.from().id(), query, offset)

        //        log.info("offset: {}, nextOffset: {}", offset, videosPage.getNextOffset());
        val results: MutableList<InlineQueryResultCachedVideo> = ArrayList<Any?>(videosPage.getPersistedVideos().size())
        for (v in videosPage.getPersistedVideos()) {
            results.add(
                InlineQueryResultCachedVideo(
                    v.getFileUniqueId(),
                    v.getFileId(), (if (isSize) "[" + Util.renderFileSize(v.getSize()) + "] " else "")
                            + v.getKeywords().get(0)
                )
            )
        }
        telegramBot.execute(
            update,
            AnswerInlineQuery(inlineQuery.id(), *results.toTypedArray())
                .nextOffset(videosPage.getNextOffset())
        )
*/
    }

    private fun forwardMessageToAdmin(messageId: Int, chatId: Long) {
        telegramBot.execute(ForwardMessage(adminUserChecker.adminUser, chatId, messageId))
    }

    companion object {
        private val log = LoggerFactory.getLogger(BotPollingJob::class.java)
    }
}