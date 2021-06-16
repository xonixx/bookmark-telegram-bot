package com.cmlteam.bookmark_telegram_bot

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkRepository : MongoRepository<Bookmark, String> {
  fun countByUserIdAndReadNot(userId: Int, read: Boolean): Long
  fun findByIdAndUserId(id: String, userId: Int): Bookmark?
  fun findByUrlAndUserId(url: String, userId: Int): Bookmark?
}
