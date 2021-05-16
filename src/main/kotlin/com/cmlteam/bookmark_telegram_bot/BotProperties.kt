package com.cmlteam.bookmark_telegram_bot

import com.pengrad.telegrambot.model.User
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive


@ConfigurationProperties(prefix = "telegram-bot")
@ConstructorBinding
@Validated
data class BotProperties(
    @field:Positive override val adminUser: Long = 0,
    @field:NotBlank val token: String? = null,
    @field:NotBlank val backupFolder: String? = null,
    @field:NotNull val maxFileSize: Int? = null
) : AdminUserChecker {

    override fun isAdmin(userId: Long): Boolean {
        return adminUser == userId
    }

    override fun isAdmin(user: User): Boolean {
        return isAdmin(user.id().toLong())
    }
}