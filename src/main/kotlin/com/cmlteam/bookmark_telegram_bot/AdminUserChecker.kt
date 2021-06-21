package com.cmlteam.bookmark_telegram_bot

import com.pengrad.telegrambot.model.User

interface AdminUserChecker {
  fun isAdmin(userId: Long): Boolean {
    return adminUser == userId
  }
  fun isAdmin(user: User): Boolean {
    return isAdmin(user.id().toLong())
  }

  /** The primary admin user to get notified in case of errors, etc. */
  val adminUser: Long
}
