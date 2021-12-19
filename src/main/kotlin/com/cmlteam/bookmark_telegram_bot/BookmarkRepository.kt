package com.cmlteam.bookmark_telegram_bot

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkRepository : MongoRepository<Bookmark, String> {
  fun countByUserIdAndReadNot(userId: Long, read: Boolean): Long
  fun findByIdAndUserId(id: String, userId: Long): Bookmark?
  fun findByUrlAndUserId(url: String, userId: Long): Bookmark?
}
