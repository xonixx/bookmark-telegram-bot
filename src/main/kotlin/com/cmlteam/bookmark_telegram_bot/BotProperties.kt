package com.cmlteam.bookmark_telegram_bot

import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Profile
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "telegram-bot")
@ConstructorBinding
@Validated
@Profile("!test")
data class BotProperties(
    @field:Positive override val adminUser: Long = 0,
    @field:NotBlank val token: String? = null,
    @field:NotBlank val backupFolder: String? = null,
    @field:NotNull val maxFileSize: Int? = null
) : AdminUserChecker
