package com.cmlteam.bookmark_telegram_bot

import com.cmlteam.telegram_bot_common.JsonHelper
import com.cmlteam.telegram_bot_common.LogHelper
import com.cmlteam.telegram_bot_common.TelegramBotWrapper
import com.pengrad.telegrambot.request.GetUpdates
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled

class BotPollingJob(
    private val telegramBot: TelegramBotWrapper,
    private val botUpdateHandler: BotUpdateHandler,
    private val jsonHelper: JsonHelper,
    private val logHelper: LogHelper,
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
      log.info("Received:\n${jsonHelper.toPrettyString(update)}")

      botUpdateHandler.processUpdate(update)
      getUpdates.offset(update.updateId() + 1)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(BotPollingJob::class.java)
  }
}
