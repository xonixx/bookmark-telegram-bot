package com.cmlteam.bookmark_telegram_bot

import java.util.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BookmarkRepository : MongoRepository<Bookmark, String> {
  fun countByUserIdAndReadNot(userId: Int, read: Boolean): Long
  fun findByIdAndUserId(id: String, userId: Int): Optional<Bookmark>
  fun existsByUrlAndUserId(url: String, userId: Int): Boolean
}
