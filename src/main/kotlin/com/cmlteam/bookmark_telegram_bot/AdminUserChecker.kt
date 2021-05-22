package com.cmlteam.bookmark_telegram_bot

import com.pengrad.telegrambot.model.User

interface AdminUserChecker {
  fun isAdmin(userId: Long): Boolean
  fun isAdmin(user: User): Boolean

  /** The primary admin user to get notified in case of errors, etc. */
  val adminUser: Long
}
