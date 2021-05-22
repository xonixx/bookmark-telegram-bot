package com.cmlteam.bookmark_telegram_bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.cmlteam"])
class BookmarkTelegramBotApplication

fun main(args: Array<String>) {
  runApplication<BookmarkTelegramBotApplication>(*args)
}
