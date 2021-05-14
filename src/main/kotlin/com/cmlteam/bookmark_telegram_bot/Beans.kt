package com.cmlteam.bookmark_telegram_bot

import com.cmlteam.telegram_bot_common.ErrorReporter
import com.cmlteam.telegram_bot_common.JsonHelper
import com.cmlteam.telegram_bot_common.LogHelper
import com.cmlteam.telegram_bot_common.TelegramBotWrapper
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.GetMe
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Beans {
    @Bean
    fun getTelegramBot(botProperties: BotProperties): TelegramBot {
        val telegramBot = TelegramBot(botProperties.token)
        val response = telegramBot.execute(GetMe())
        requireNotNull(response.user()) { "bot token is incorrect" }
        return telegramBot
    }

    @Bean
    fun errorReporter(
        telegramBot: TelegramBot, jsonHelper: JsonHelper, botProperties: BotProperties
    ): ErrorReporter {
        return ErrorReporter(telegramBot, jsonHelper, listOf(botProperties.adminUser))
    }

    @Bean
    fun telegramBotWrapper(
        telegramBot: TelegramBot, jsonHelper: JsonHelper, errorReporter: ErrorReporter
    ): TelegramBotWrapper {
        return TelegramBotWrapper(telegramBot, jsonHelper, errorReporter)
    }

    @Bean
    fun botPollingJob(
        botProperties: BotProperties,
        bookmarkService: BookmarkService,
        telegramBotWrapper: TelegramBotWrapper,
        jsonHelper: JsonHelper,
        logHelper: LogHelper,
    ): BotPollingJob {
        return BotPollingJob(
            telegramBotWrapper,
            bookmarkService,
            jsonHelper,
            logHelper,
            botProperties,
            botProperties.maxFileSize ?: 0,
        )
    }

/*    @Bean
    fun videosBackupper(
        botProperties: BotProperties,
        telegramBotWrapper: TelegramBotWrapper?,
        videosService: VideosService?,
        persistedVideoRepository: PersistedVideoRepository?
    ): VideosBackupper {
        return VideosBackupper(
            botProperties.backupFolder,
            botProperties.token,
            telegramBotWrapper,
            videosService,
            persistedVideoRepository
        )
    }

    @Bean
    fun videosReviver(
        botProperties: BotProperties,
        telegramBotWrapper: TelegramBotWrapper?,
        videosService: VideosService?
    ): VideosReviver {
        return VideosReviver(botProperties.backupFolder, telegramBotWrapper, videosService)
    }

    @Bean
    fun videosService(
        botProperties: BotProperties?,
        persistedVideoRepository: PersistedVideoRepository?,
        searchStringMatcher: SearchStringMatcher?
    ): VideosService {
        return VideosService(botProperties, persistedVideoRepository, searchStringMatcher)
    }*/
}